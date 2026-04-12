package com.willmear.sprint.export.renderer;

import com.willmear.sprint.presentation.domain.PresentationDeck;
import java.util.Locale;
import org.springframework.stereotype.Component;

@Component
public class ExportFileNameGenerator {

    public String powerPointFileName(PresentationDeck deck) {
        String base = deck.title();
        if (base == null || base.isBlank()) {
            base = "sprint-review-" + deck.referenceId();
        }
        String slug = base.toLowerCase(Locale.ROOT)
                .replaceAll("[^a-z0-9]+", "-")
                .replaceAll("(^-|-$)", "");
        if (slug.isBlank()) {
            slug = "presentation-deck";
        }
        return slug + ".pptx";
    }
}
