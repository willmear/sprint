package com.willmear.sprint.export.renderer;

import com.willmear.sprint.common.exception.PowerPointExportException;
import com.willmear.sprint.export.domain.BinaryExportPayload;
import com.willmear.sprint.export.domain.ExportFormat;
import com.willmear.sprint.export.domain.PowerPointDeckRenderModel;
import com.willmear.sprint.export.domain.PowerPointShapeElementRenderModel;
import com.willmear.sprint.export.domain.PowerPointSlideRenderModel;
import com.willmear.sprint.export.domain.PowerPointTextElementRenderModel;
import com.willmear.sprint.presentation.domain.PresentationDeck;
import com.willmear.sprint.presentation.domain.SlideElementRole;
import com.willmear.sprint.presentation.domain.TextAlignment;
import com.willmear.sprint.presentation.theme.domain.PresentationTheme;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.geom.Rectangle2D;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import org.apache.poi.sl.usermodel.ShapeType;
import org.apache.poi.sl.usermodel.Placeholder;
import org.apache.poi.sl.usermodel.TextParagraph;
import org.apache.poi.sl.usermodel.VerticalAlignment;
import org.apache.poi.xslf.usermodel.XSLFAutoShape;
import org.apache.poi.xslf.usermodel.XSLFNotes;
import org.apache.poi.xslf.usermodel.XMLSlideShow;
import org.apache.poi.xslf.usermodel.XSLFSlide;
import org.apache.poi.xslf.usermodel.XSLFTextBox;
import org.apache.poi.xslf.usermodel.XSLFTextParagraph;
import org.apache.poi.xslf.usermodel.XSLFTextRun;
import org.apache.poi.xslf.usermodel.XSLFTextShape;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Component
public class PowerPointExportRenderer {

    static final String POWERPOINT_CONTENT_TYPE = "application/vnd.openxmlformats-officedocument.presentationml.presentation";
    private static final Logger log = LoggerFactory.getLogger(PowerPointExportRenderer.class);

    private final ExportFileNameGenerator exportFileNameGenerator;
    private final PowerPointCoordinateMapper coordinateMapper;
    private final PowerPointThemeResolver powerPointThemeResolver;
    private final PowerPointSlideStyleMapper powerPointSlideStyleMapper;
    private final PowerPointTypographyMapper powerPointTypographyMapper;

    public PowerPointExportRenderer(
            ExportFileNameGenerator exportFileNameGenerator,
            PowerPointCoordinateMapper coordinateMapper,
            PowerPointThemeResolver powerPointThemeResolver,
            PowerPointSlideStyleMapper powerPointSlideStyleMapper,
            PowerPointTypographyMapper powerPointTypographyMapper
    ) {
        this.exportFileNameGenerator = exportFileNameGenerator;
        this.coordinateMapper = coordinateMapper;
        this.powerPointThemeResolver = powerPointThemeResolver;
        this.powerPointSlideStyleMapper = powerPointSlideStyleMapper;
        this.powerPointTypographyMapper = powerPointTypographyMapper;
    }

    public BinaryExportPayload render(PresentationDeck deck, PowerPointDeckRenderModel renderModel) {
        try (XMLSlideShow slideShow = new XMLSlideShow(); ByteArrayOutputStream outputStream = new ByteArrayOutputStream()) {
            slideShow.setPageSize(new Dimension(PowerPointCoordinateMapper.PPT_SLIDE_WIDTH, PowerPointCoordinateMapper.PPT_SLIDE_HEIGHT));
            PresentationTheme theme = powerPointThemeResolver.resolve(renderModel.themeId());
            log.info("powerpoint.export.start deckId={} slideCount={} mapping={}", deck.id(), renderModel.slides().size(), coordinateMapper.describeMapping());

            for (PowerPointSlideRenderModel slideModel : renderModel.slides()) {
                if (slideModel.hidden()) {
                    continue;
                }
                log.info(
                        "powerpoint.export.slide slideOrder={} textElementCount={} shapeElementCount={}",
                        slideModel.slideOrder(),
                        slideModel.textElements().size(),
                        slideModel.shapeElements().size()
                );
                XSLFSlide slide = slideShow.createSlide();
                PowerPointSlideStyle slideStyle = powerPointSlideStyleMapper.resolve(theme, slideModel);
                applySlideTheme(slide, slideModel, slideStyle);
                renderSlideElements(slide, slideModel, theme, slideStyle);
                renderSpeakerNotes(slideShow, slide, slideModel);
            }

            slideShow.write(outputStream);
            return new BinaryExportPayload(
                    ExportFormat.POWERPOINT,
                    exportFileNameGenerator.powerPointFileName(deck),
                    POWERPOINT_CONTENT_TYPE,
                    outputStream.toByteArray(),
                    Instant.now()
            );
        } catch (IOException exception) {
            throw new PowerPointExportException("Failed to generate PowerPoint export for deck " + deck.id() + ".", exception);
        }
    }

    private void renderSlideElements(
            XSLFSlide slide,
            PowerPointSlideRenderModel slideModel,
            PresentationTheme theme,
            PowerPointSlideStyle slideStyle
    ) {
        List<RenderableElement> elements = new ArrayList<>();
        for (PowerPointTextElementRenderModel textElement : slideModel.textElements()) {
            elements.add(new RenderableElement(textElement.zIndex(), textElement.elementId(), () -> renderTextElement(slide, slideModel, theme, slideStyle, textElement)));
        }
        for (PowerPointShapeElementRenderModel shapeElement : slideModel.shapeElements()) {
            elements.add(new RenderableElement(shapeElement.zIndex(), shapeElement.elementId(), () -> renderShapeElement(slide, slideModel, slideStyle, shapeElement)));
        }
        elements.stream()
                .sorted(Comparator.comparing(RenderableElement::zIndex).thenComparing(RenderableElement::elementId, Comparator.nullsLast(String::compareTo)))
                .forEach(renderable -> renderable.render().run());
    }

    private void renderTextElement(
            XSLFSlide slide,
            PowerPointSlideRenderModel slideModel,
            PresentationTheme theme,
            PowerPointSlideStyle slideStyle,
            PowerPointTextElementRenderModel element
    ) {
        XSLFTextBox textBox = slide.createTextBox();
        textBox.clearText();
        textBox.setAnchor(coordinateMapper.toAnchor(
                Integer.toString(slideModel.slideOrder()),
                element.zIndex(),
                element.x(),
                element.y(),
                element.width(),
                element.height()
        ));
        textBox.setWordWrap(true);
        textBox.setVerticalAlignment(VerticalAlignment.TOP);
        textBox.setLeftInset(coordinateMapper.horizontalPadding());
        textBox.setRightInset(coordinateMapper.horizontalPadding());
        textBox.setTopInset(element.role() == SlideElementRole.TITLE ? slideStyle.topInset() : slideStyle.bodyTopInset());
        textBox.setBottomInset(coordinateMapper.verticalPadding());
        applyElementChrome(textBox, element, slideStyle);
        renderTextParagraphs(textBox, slideModel, element, theme, slideStyle);
    }

    private void renderShapeElement(
            XSLFSlide slide,
            PowerPointSlideRenderModel slideModel,
            PowerPointSlideStyle slideStyle,
            PowerPointShapeElementRenderModel element
    ) {
        org.apache.poi.sl.usermodel.ShapeType poiShapeType = toPoiShapeType(element);
        if (poiShapeType == null) {
            log.debug("powerpoint.export.shape_unsupported slideOrder={} shapeType={}", slideModel.slideOrder(), element.shapeType());
            return;
        }

        XSLFAutoShape shape = slide.createAutoShape();
        shape.setShapeType(poiShapeType);
        shape.setAnchor(coordinateMapper.toAnchor(
                Integer.toString(slideModel.slideOrder()),
                element.zIndex(),
                element.x(),
                element.y(),
                element.width(),
                element.height()
        ));
        if (element.rotationDegrees() != 0.0d) {
            shape.setRotation(element.rotationDegrees());
        }

        Color fill = decodeColor(element.fillColor(), slideStyle.accentSecondaryColor());
        Color border = decodeColor(element.borderColor(), slideStyle.accentColor());
        if (element.shapeType() == com.willmear.sprint.presentation.domain.ShapeType.LINE) {
            shape.setFillColor(null);
        } else {
            shape.setFillColor(fill);
        }
        shape.setLineColor(border);
        shape.setLineWidth(Math.max(0.75d, element.borderWidth()));
    }

    private void renderTextParagraphs(
            XSLFTextBox textBox,
            PowerPointSlideRenderModel slideModel,
            PowerPointTextElementRenderModel element,
            PresentationTheme theme,
            PowerPointSlideStyle slideStyle
    ) {
        String text = element.textContent() == null ? "" : element.textContent();
        String[] lines = text.split("\\R", -1);
        if (lines.length == 0) {
            lines = new String[]{""};
        }

        PowerPointTypography typography = powerPointTypographyMapper.resolve(theme, slideModel.slideType(), element);

        for (String line : lines) {
            XSLFTextParagraph paragraph = textBox.addNewTextParagraph();
            paragraph.setTextAlign(toParagraphAlignment(element.textAlignment()));
            paragraph.setSpaceAfter(slideStyle.paragraphSpaceAfter());
            paragraph.setSpaceBefore(0.0);

            ParsedLine parsedLine = parseLine(line);
            if (parsedLine.bullet()) {
                paragraph.setBullet(true);
                paragraph.setIndent(slideStyle.bulletIndent());
                paragraph.setLeftMargin(slideStyle.bulletLeftMargin());
            }

            XSLFTextRun run = paragraph.addNewTextRun();
            run.setText(parsedLine.text());
            run.setFontFamily(typography.fontFamily());
            run.setFontSize(typography.fontSize());
            run.setBold(element.bold());
            run.setItalic(element.italic());
            run.setUnderlined(element.underline());
            run.setFontColor(resolveTextColor(slideStyle, element));
        }
    }

    private void renderSpeakerNotes(XMLSlideShow slideShow, XSLFSlide slide, PowerPointSlideRenderModel slideModel) {
        String speakerNotes = slideModel.speakerNotes();
        if (speakerNotes == null || speakerNotes.isBlank()) {
            return;
        }

        try {
            ensureNotesSupport(slideShow, slideModel.slideOrder());
            XSLFNotes notes = slideShow.getNotesSlide(slide);
            XSLFTextShape notesShape = resolveNotesTextShape(notes);
            notesShape.clearText();
            notesShape.setWordWrap(true);
            notesShape.setVerticalAlignment(VerticalAlignment.TOP);
            renderNotesParagraphs(notesShape, speakerNotes);
        } catch (RuntimeException exception) {
            log.debug("powerpoint.export.notes_unavailable slideOrder={} message={}", slideModel.slideOrder(), exception.getMessage());
        }
    }

    private void ensureNotesSupport(XMLSlideShow slideShow, int slideOrder) {
        if (slideShow.getNotesMaster() != null) {
            return;
        }
        try {
            slideShow.createNotesMaster();
        } catch (RuntimeException exception) {
            log.debug("powerpoint.export.notes_master_unavailable slideOrder={} message={}", slideOrder, exception.getMessage());
            throw exception;
        }
    }

    private XSLFTextShape resolveNotesTextShape(XSLFNotes notes) {
        if (notes.getPlaceholder(Placeholder.BODY) instanceof XSLFTextShape placeholderShape) {
            return placeholderShape;
        }

        XSLFTextBox textBox = notes.createTextBox();
        textBox.setAnchor(new Rectangle2D.Double(36, 324, 648, 180));
        return textBox;
    }

    private void renderNotesParagraphs(XSLFTextShape notesShape, String speakerNotes) {
        String[] lines = speakerNotes.split("\\R", -1);
        if (lines.length == 0) {
            lines = new String[]{speakerNotes};
        }

        for (String line : lines) {
            XSLFTextParagraph paragraph = notesShape.addNewTextParagraph();
            paragraph.setTextAlign(TextParagraph.TextAlign.LEFT);
            paragraph.setSpaceAfter(0.0);
            paragraph.setSpaceBefore(0.0);

            ParsedLine parsedLine = parseLine(line);
            if (parsedLine.bullet()) {
                paragraph.setBullet(true);
                paragraph.setIndent(0.0);
                paragraph.setLeftMargin(12.0);
            }

            XSLFTextRun run = paragraph.addNewTextRun();
            run.setText(parsedLine.text());
            run.setFontFamily("Aptos");
            run.setFontSize(14.0);
        }
    }

    private ParsedLine parseLine(String line) {
        if (line.startsWith("• ")) {
            return new ParsedLine(line.substring(2), true);
        }
        if (line.startsWith("- ")) {
            return new ParsedLine(line.substring(2), true);
        }
        return new ParsedLine(line, false);
    }

    private TextParagraph.TextAlign toParagraphAlignment(TextAlignment alignment) {
        if (alignment == null) {
            return TextParagraph.TextAlign.LEFT;
        }
        return switch (alignment) {
            case CENTER -> TextParagraph.TextAlign.CENTER;
            case RIGHT -> TextParagraph.TextAlign.RIGHT;
            case LEFT -> TextParagraph.TextAlign.LEFT;
        };
    }

    private void applySlideTheme(XSLFSlide slide, PowerPointSlideRenderModel slideModel, PowerPointSlideStyle slideStyle) {
        Color resolvedBackground = decodeColor(slideModel.backgroundColor(), slideStyle.defaultBackgroundColor());
        slide.getBackground().setFillColor(resolvedBackground);

        XSLFAutoShape backgroundLayer = slide.createAutoShape();
        backgroundLayer.setShapeType(ShapeType.RECT);
        backgroundLayer.setAnchor(new Rectangle2D.Double(0, 0, PowerPointCoordinateMapper.PPT_SLIDE_WIDTH, PowerPointCoordinateMapper.PPT_SLIDE_HEIGHT));
        backgroundLayer.setFillColor(resolvedBackground);
        backgroundLayer.setLineColor(resolvedBackground);

        if (slideStyle.fullBleedHeaderAccent()) {
            XSLFAutoShape headerBand = slide.createAutoShape();
            headerBand.setShapeType(ShapeType.RECT);
            headerBand.setAnchor(new Rectangle2D.Double(0, 0, PowerPointCoordinateMapper.PPT_SLIDE_WIDTH, 42));
            headerBand.setFillColor(slideStyle.accentColor());
            headerBand.setLineColor(slideStyle.accentColor());
        }
        if (slideStyle.leftAccentRail()) {
            XSLFAutoShape accentRail = slide.createAutoShape();
            accentRail.setShapeType(ShapeType.RECT);
            accentRail.setAnchor(new Rectangle2D.Double(0, 0, 18, PowerPointCoordinateMapper.PPT_SLIDE_HEIGHT));
            accentRail.setFillColor(slideStyle.accentColor());
            accentRail.setLineColor(slideStyle.accentColor());
        }
        if (slideStyle.fullBleedBottomAccent()) {
            XSLFAutoShape bottomBand = slide.createAutoShape();
            bottomBand.setShapeType(ShapeType.RECT);
            bottomBand.setAnchor(new Rectangle2D.Double(0, PowerPointCoordinateMapper.PPT_SLIDE_HEIGHT - 32, PowerPointCoordinateMapper.PPT_SLIDE_WIDTH, 32));
            bottomBand.setFillColor(slideStyle.accentColor());
            bottomBand.setLineColor(slideStyle.accentColor());
        }
    }

    private void applyElementChrome(XSLFTextBox textBox, PowerPointTextElementRenderModel element, PowerPointSlideStyle slideStyle) {
        if (slideStyle.emphasizeCallouts() && (element.role() == SlideElementRole.FREEFORM || element.role() == SlideElementRole.CALLOUT)) {
            textBox.setFillColor(slideStyle.accentSecondaryColor());
            textBox.setLineColor(slideStyle.accentColor());
            textBox.setLineWidth(1.5);
            textBox.setLeftInset(14.0);
            textBox.setRightInset(14.0);
            textBox.setTopInset(12.0);
            textBox.setBottomInset(12.0);
            return;
        }
        textBox.setLineColor(null);
    }

    private Color resolveTextColor(PowerPointSlideStyle slideStyle, PowerPointTextElementRenderModel element) {
        if (element.textColor() != null && !element.textColor().isBlank()) {
            return decodeColor(element.textColor(), slideStyle.bodyColor());
        }
        SlideElementRole role = element.role();
        if (role == SlideElementRole.TITLE) {
            return slideStyle.titleColor();
        }
        if (role == SlideElementRole.SECTION_LABEL) {
            return slideStyle.accentColor();
        }
        if (role == SlideElementRole.SUBTITLE || role == SlideElementRole.FREEFORM || role == SlideElementRole.FOOTER) {
            return slideStyle.subtitleColor();
        }
        if (role == SlideElementRole.CALLOUT || role == SlideElementRole.METRIC) {
            return slideStyle.titleColor();
        }
        return slideStyle.bodyColor();
    }

    private org.apache.poi.sl.usermodel.ShapeType toPoiShapeType(PowerPointShapeElementRenderModel element) {
        if (element.shapeType() == null) {
            return org.apache.poi.sl.usermodel.ShapeType.RECT;
        }
        return switch (element.shapeType()) {
            case RECTANGLE -> org.apache.poi.sl.usermodel.ShapeType.RECT;
            case ROUNDED_RECTANGLE -> org.apache.poi.sl.usermodel.ShapeType.ROUND_RECT;
            case ELLIPSE, CIRCLE -> org.apache.poi.sl.usermodel.ShapeType.ELLIPSE;
            case TRIANGLE -> org.apache.poi.sl.usermodel.ShapeType.TRIANGLE;
            case DIAMOND -> org.apache.poi.sl.usermodel.ShapeType.DIAMOND;
            case LINE -> org.apache.poi.sl.usermodel.ShapeType.LINE;
            case ARROW -> org.apache.poi.sl.usermodel.ShapeType.RIGHT_ARROW;
            // TODO: Add cleaner export mappings for additional editor shape types if they become first-class in the model.
        };
    }

    private Color decodeColor(String hex, Color fallback) {
        if (hex == null || hex.isBlank()) {
            return fallback;
        }
        try {
            return Color.decode(hex);
        } catch (NumberFormatException exception) {
            log.debug("powerpoint.export.invalid_color value={} message={}", hex, exception.getMessage());
            return fallback;
        }
    }

    private record RenderableElement(int zIndex, String elementId, Runnable render) {
    }

    private record ParsedLine(String text, boolean bullet) {
    }
}
