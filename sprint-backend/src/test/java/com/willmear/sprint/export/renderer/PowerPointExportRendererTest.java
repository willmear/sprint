package com.willmear.sprint.export.renderer;

import static org.assertj.core.api.Assertions.assertThat;

import com.willmear.sprint.export.domain.ExportFormat;
import com.willmear.sprint.export.mapper.PresentationDeckToPowerPointMapper;
import com.willmear.sprint.presentation.domain.DeckStatus;
import com.willmear.sprint.presentation.domain.PresentationDeck;
import com.willmear.sprint.presentation.domain.PresentationSlide;
import com.willmear.sprint.presentation.domain.PresentationSlideElement;
import com.willmear.sprint.presentation.domain.SlideElementRole;
import com.willmear.sprint.presentation.domain.SlideElementType;
import com.willmear.sprint.presentation.domain.SlideLayoutType;
import com.willmear.sprint.presentation.domain.SlideType;
import com.willmear.sprint.presentation.domain.TextAlignment;
import com.willmear.sprint.presentation.domain.ShapeType;
import com.willmear.sprint.presentation.theme.application.PresentationThemeApplicationService;
import com.willmear.sprint.presentation.theme.registry.ThemeRegistry;
import java.awt.Color;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.List;
import java.util.UUID;
import org.apache.poi.xslf.usermodel.XMLSlideShow;
import org.apache.poi.xslf.usermodel.XSLFAutoShape;
import org.apache.poi.xslf.usermodel.XSLFTextShape;
import org.junit.jupiter.api.Test;
import java.awt.geom.Rectangle2D;

class PowerPointExportRendererTest {

    private final ThemeRegistry themeRegistry = new ThemeRegistry("corporate-clean");
    private final PowerPointExportRenderer renderer =
            new PowerPointExportRenderer(
                    new ExportFileNameGenerator(),
                    new PowerPointCoordinateMapper(),
                    new PowerPointThemeResolver(new PresentationThemeApplicationService(themeRegistry)),
                    new PowerPointSlideStyleMapper(),
                    new PowerPointTypographyMapper(new PowerPointCoordinateMapper())
            );
    private final PresentationDeckToPowerPointMapper mapper = new PresentationDeckToPowerPointMapper();

    @Test
    void shouldRenderRealPowerPointFromPresentationDeck() throws IOException {
        PresentationDeck deck = new PresentationDeck(
                UUID.randomUUID(),
                UUID.randomUUID(),
                "SPRINT",
                "42",
                "Sprint Review Payments",
                "Iteration 42",
                "corporate-clean",
                DeckStatus.DRAFT,
                List.of(new PresentationSlide(
                        UUID.randomUUID(),
                        UUID.randomUUID(),
                        0,
                        SlideType.TITLE,
                        "Sprint Review Payments",
                        List.of(),
                        null,
                        "Talk track",
                        "Intro",
                        "#0F172A",
                        com.willmear.sprint.presentation.domain.BackgroundStyleType.SOLID,
                        false,
                        SlideLayoutType.SECTION_SUMMARY,
                        com.willmear.sprint.presentation.template.SlideTemplateType.TITLE_SLIDE,
                        List.of(
                                new PresentationSlideElement(
                                        UUID.randomUUID(),
                                        UUID.randomUUID(),
                                        0,
                                        SlideElementType.SHAPE,
                                        SlideElementRole.FREEFORM,
                                        "",
                                        860.0,
                                        160.0,
                                        220.0,
                                        140.0,
                                        0,
                                        0.0,
                                        "#38BDF8",
                                        "#0EA5E9",
                                        3,
                                        null,
                                        null,
                                        null,
                                        false,
                                        false,
                                        false,
                                        null,
                                        ShapeType.ROUNDED_RECTANGLE,
                                        false,
                                        null,
                                        null
                                ),
                                new PresentationSlideElement(
                                        UUID.randomUUID(),
                                        UUID.randomUUID(),
                                        1,
                                        SlideElementType.TEXT_BOX,
                                        SlideElementRole.TITLE,
                                        "Sprint Review Payments",
                                        96.0,
                                        74.0,
                                        1088.0,
                                        88.0,
                                        1,
                                        null,
                                        null,
                                        null,
                                        null,
                                        "#E2E8F0",
                                        "Aptos",
                                        30,
                                        true,
                                        false,
                                        true,
                                        TextAlignment.LEFT,
                                        null,
                                        false,
                                        null,
                                        null
                                ),
                                new PresentationSlideElement(
                                        UUID.randomUUID(),
                                        UUID.randomUUID(),
                                        2,
                                        SlideElementType.TEXT_BOX,
                                        SlideElementRole.BODY,
                                        "Line one\n• First point\n• Second point",
                                        96.0,
                                        190.0,
                                        760.0,
                                        220.0,
                                        2,
                                        null,
                                        null,
                                        null,
                                        null,
                                        "#CBD5E1",
                                        "Aptos",
                                        22,
                                        false,
                                        false,
                                        false,
                                        TextAlignment.LEFT,
                                        null,
                                        false,
                                        null,
                                        null
                                )
                        ),
                        false,
                        null,
                        null
                )),
                null,
                null,
                null
        );

        var payload = renderer.render(deck, mapper.toRenderModel(deck));

        assertThat(payload.format()).isEqualTo(ExportFormat.POWERPOINT);
        assertThat(payload.fileName()).isEqualTo("sprint-review-payments.pptx");
        assertThat(payload.binaryContent()).isNotEmpty();

        try (XMLSlideShow slideShow = new XMLSlideShow(new ByteArrayInputStream(payload.binaryContent()))) {
            assertThat(slideShow.getSlides()).hasSize(1);
            assertThat(slideShow.getSlides().getFirst().getShapes()).hasSize(5);
            assertThat(((XSLFAutoShape) slideShow.getSlides().getFirst().getShapes().getFirst()).getFillColor())
                    .isEqualTo(Color.decode("#0F172A"));
            assertThat(((XSLFAutoShape) slideShow.getSlides().getFirst().getShapes().get(1)).getFillColor())
                    .isEqualTo(Color.decode(themeRegistry.resolve("corporate-clean").colorPalette().accent()));
            assertThat(slideShow.getSlides().getFirst().getBackground().getFillColor())
                    .isEqualTo(Color.decode("#0F172A"));
            assertThat(((XSLFAutoShape) slideShow.getSlides().getFirst().getShapes().get(2)).getFillColor())
                    .isEqualTo(Color.decode("#38BDF8"));
            assertThat(((XSLFAutoShape) slideShow.getSlides().getFirst().getShapes().get(2)).getLineColor())
                    .isEqualTo(Color.decode("#0EA5E9"));
            assertThat(((XSLFTextShape) slideShow.getSlides().getFirst().getShapes().get(3)).getText()).contains("Sprint Review Payments");
            assertThat(((XSLFTextShape) slideShow.getSlides().getFirst().getShapes().get(4)).getText()).contains("First point");
            assertThat(slideShow.getSlides().getFirst().getNotes()).isNotNull();
            assertThat(slideShow.getSlides().getFirst().getNotes().getTextParagraphs())
                    .flatExtracting(paragraphs -> paragraphs)
                    .extracting(paragraph -> paragraph.getText())
                    .anyMatch(text -> text.contains("Talk track"));
        }
    }

    @Test
    void shouldRenderFullHeightSideRailAndClosingBottomBand() throws IOException {
        PresentationDeck deck = new PresentationDeck(
                UUID.randomUUID(),
                UUID.randomUUID(),
                "SPRINT",
                "34",
                "Deck",
                null,
                "executive-dark-accent",
                DeckStatus.DRAFT,
                List.of(
                        new PresentationSlide(
                                UUID.randomUUID(),
                                UUID.randomUUID(),
                                0,
                                SlideType.OVERVIEW,
                                "Overview",
                                List.of(),
                                null,
                                null,
                                null,
                                null,
                                com.willmear.sprint.presentation.domain.BackgroundStyleType.SOLID,
                                false,
                                SlideLayoutType.TITLE_AND_BULLETS,
                                com.willmear.sprint.presentation.template.SlideTemplateType.EXECUTIVE_SUMMARY,
                                List.of(),
                                false,
                                null,
                                null
                        ),
                        new PresentationSlide(
                                UUID.randomUUID(),
                                UUID.randomUUID(),
                                1,
                                SlideType.CUSTOM,
                                "Wrap up",
                                List.of(),
                                null,
                                null,
                                null,
                                "#dbeafe",
                                com.willmear.sprint.presentation.domain.BackgroundStyleType.SOLID,
                                false,
                                SlideLayoutType.SECTION_SUMMARY,
                                com.willmear.sprint.presentation.template.SlideTemplateType.CLOSING_SUMMARY,
                                List.of(),
                                false,
                                null,
                                null
                        )
                ),
                null,
                null,
                null
        );

        var payload = renderer.render(deck, mapper.toRenderModel(deck));

        try (XMLSlideShow slideShow = new XMLSlideShow(new ByteArrayInputStream(payload.binaryContent()))) {
            assertThat(slideShow.getSlides()).hasSize(2);

            XSLFAutoShape overviewRail = (XSLFAutoShape) slideShow.getSlides().get(0).getShapes().get(1);
            Rectangle2D overviewAnchor = overviewRail.getAnchor();
            assertThat(overviewAnchor.getX()).isEqualTo(0.0);
            assertThat(overviewAnchor.getY()).isEqualTo(0.0);
            assertThat(overviewAnchor.getHeight()).isEqualTo(PowerPointCoordinateMapper.PPT_SLIDE_HEIGHT);

            XSLFAutoShape closingBackground = (XSLFAutoShape) slideShow.getSlides().get(1).getShapes().getFirst();
            assertThat(closingBackground.getFillColor()).isEqualTo(Color.decode("#dbeafe"));
            XSLFAutoShape closingBottomBand = (XSLFAutoShape) slideShow.getSlides().get(1).getShapes().get(1);
            Rectangle2D closingAnchor = closingBottomBand.getAnchor();
            assertThat(slideShow.getSlides().get(1).getBackground().getFillColor()).isEqualTo(Color.decode("#dbeafe"));
            assertThat(closingAnchor.getY()).isEqualTo(PowerPointCoordinateMapper.PPT_SLIDE_HEIGHT - 32.0);
            assertThat(closingAnchor.getWidth()).isEqualTo(PowerPointCoordinateMapper.PPT_SLIDE_WIDTH);
        }
    }
}
