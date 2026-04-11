package com.willmear.sprint.export.renderer;

import com.willmear.sprint.common.exception.UnsupportedExportFormatException;
import com.willmear.sprint.export.domain.ExportFormat;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;
import org.springframework.stereotype.Component;

@Component
public class ExportRendererRegistry {

    private final Map<ExportFormat, ExportRenderer> renderersByFormat;

    public ExportRendererRegistry(List<ExportRenderer> renderers) {
        this.renderersByFormat = new EnumMap<>(ExportFormat.class);
        renderers.forEach(renderer -> renderersByFormat.put(renderer.supports(), renderer));
    }

    public ExportRenderer getRenderer(ExportFormat format) {
        if (format == ExportFormat.POWERPOINT) {
            // TODO: Add PPTX or Google Slides integration behind the same export boundary.
            throw new UnsupportedExportFormatException("PowerPoint export is planned but not implemented yet.");
        }
        ExportRenderer renderer = renderersByFormat.get(format);
        if (renderer == null) {
            throw new UnsupportedExportFormatException("Export format " + format + " is not supported.");
        }
        return renderer;
    }
}
