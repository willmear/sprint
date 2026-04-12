package com.willmear.sprint.export.renderer;

import java.awt.geom.Rectangle2D;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Component
public class PowerPointCoordinateMapper {

    public static final int EDITOR_SLIDE_WIDTH = 1280;
    public static final int EDITOR_SLIDE_HEIGHT = 720;
    public static final int PPT_SLIDE_WIDTH = 960;
    public static final int PPT_SLIDE_HEIGHT = 540;
    private static final Logger log = LoggerFactory.getLogger(PowerPointCoordinateMapper.class);

    private final double xScale;
    private final double yScale;

    public PowerPointCoordinateMapper() {
        this.xScale = (double) PPT_SLIDE_WIDTH / EDITOR_SLIDE_WIDTH;
        this.yScale = (double) PPT_SLIDE_HEIGHT / EDITOR_SLIDE_HEIGHT;
    }

    public Rectangle2D toAnchor(String slideKey, int elementIndex, double x, double y, double width, double height) {
        double mappedX = clamp(scaleX(x), 0, PPT_SLIDE_WIDTH);
        double mappedY = clamp(scaleY(y), 0, PPT_SLIDE_HEIGHT);
        double mappedWidth = Math.max(8, scaleX(width));
        double mappedHeight = Math.max(8, scaleY(height));

        if (mappedX + mappedWidth > PPT_SLIDE_WIDTH) {
            mappedWidth = Math.max(8, PPT_SLIDE_WIDTH - mappedX);
            logCoordinateAdjustment(slideKey, elementIndex, "width clipped to slide bounds");
        }
        if (mappedY + mappedHeight > PPT_SLIDE_HEIGHT) {
            mappedHeight = Math.max(8, PPT_SLIDE_HEIGHT - mappedY);
            logCoordinateAdjustment(slideKey, elementIndex, "height clipped to slide bounds");
        }

        return new Rectangle2D.Double(mappedX, mappedY, mappedWidth, mappedHeight);
    }

    public double toPointFontSize(Integer fontSizePx) {
        int safeFontSize = fontSizePx == null ? 24 : fontSizePx;
        return Math.max(8.0d, safeFontSize * yScale);
    }

    public double horizontalPadding() {
        return 6 * xScale;
    }

    public double verticalPadding() {
        return 4 * yScale;
    }

    public String describeMapping() {
        return EDITOR_SLIDE_WIDTH + "x" + EDITOR_SLIDE_HEIGHT + " editor canvas mapped proportionally to "
                + PPT_SLIDE_WIDTH + "x" + PPT_SLIDE_HEIGHT + " PowerPoint canvas";
    }

    private double scaleX(double value) {
        return value * xScale;
    }

    private double scaleY(double value) {
        return value * yScale;
    }

    private double clamp(double value, double min, double max) {
        return Math.max(min, Math.min(value, max));
    }

    private void logCoordinateAdjustment(String slideKey, int elementIndex, String message) {
        if (log.isDebugEnabled()) {
            log.debug("powerpoint.export.coordinate_adjustment slide={} elementIndex={} message={}", slideKey, elementIndex, message);
        }
    }
}
