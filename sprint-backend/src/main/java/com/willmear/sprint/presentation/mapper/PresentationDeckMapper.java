package com.willmear.sprint.presentation.mapper;

import com.willmear.sprint.presentation.api.request.UpdateDeckRequest;
import com.willmear.sprint.presentation.api.response.ColorPaletteResponse;
import com.willmear.sprint.presentation.api.response.PresentationDeckResponse;
import com.willmear.sprint.presentation.api.response.PresentationThemeSummaryResponse;
import com.willmear.sprint.presentation.api.response.TypographyScaleResponse;
import com.willmear.sprint.presentation.domain.PresentationDeck;
import com.willmear.sprint.presentation.entity.PresentationDeckEntity;
import com.willmear.sprint.presentation.entity.PresentationSlideEntity;
import com.willmear.sprint.presentation.theme.domain.PresentationTheme;
import com.willmear.sprint.presentation.theme.registry.ThemeRegistry;
import java.util.ArrayList;
import java.util.List;
import org.springframework.stereotype.Component;

@Component
public class PresentationDeckMapper {

    private final PresentationSlideMapper presentationSlideMapper;
    private final ThemeRegistry themeRegistry;

    public PresentationDeckMapper(PresentationSlideMapper presentationSlideMapper, ThemeRegistry themeRegistry) {
        this.presentationSlideMapper = presentationSlideMapper;
        this.themeRegistry = themeRegistry;
    }

    public PresentationDeck toDomain(PresentationDeckEntity entity) {
        return new PresentationDeck(
                entity.getId(),
                entity.getWorkspaceId(),
                entity.getReferenceType(),
                entity.getReferenceId(),
                entity.getTitle(),
                entity.getSubtitle(),
                entity.getThemeId(),
                entity.getStatus(),
                entity.getSlides().stream().map(presentationSlideMapper::toDomain).toList(),
                entity.getSourceArtifactId(),
                entity.getCreatedAt(),
                entity.getUpdatedAt()
        );
    }

    public PresentationDeckResponse toResponse(PresentationDeck deck) {
        PresentationTheme theme = themeRegistry.resolve(deck.themeId());
        return new PresentationDeckResponse(
                deck.id(),
                deck.workspaceId(),
                deck.referenceType(),
                deck.referenceId(),
                deck.title(),
                deck.subtitle(),
                deck.themeId(),
                theme.displayName(),
                new PresentationThemeSummaryResponse(
                        theme.themeId(),
                        theme.displayName(),
                        new ColorPaletteResponse(
                                theme.colorPalette().background(),
                                theme.colorPalette().surface(),
                                theme.colorPalette().textPrimary(),
                                theme.colorPalette().textSecondary(),
                                theme.colorPalette().accent(),
                                theme.colorPalette().accentSecondary(),
                                theme.colorPalette().danger(),
                                theme.colorPalette().mutedBorder()
                        ),
                        new TypographyScaleResponse(
                                theme.typography().titleFontFamily(),
                                theme.typography().bodyFontFamily(),
                                theme.typography().titleFontSize(),
                                theme.typography().subtitleFontSize(),
                                theme.typography().bodyFontSize(),
                                theme.typography().smallFontSize()
                        )
                ),
                deck.status().name(),
                deck.sourceArtifactId(),
                deck.slides().stream().map(presentationSlideMapper::toResponse).toList(),
                deck.createdAt(),
                deck.updatedAt()
        );
    }

    public PresentationDeckEntity toEntity(PresentationDeck deck) {
        PresentationDeckEntity entity = new PresentationDeckEntity();
        entity.setId(deck.id());
        entity.setWorkspaceId(deck.workspaceId());
        entity.setReferenceType(deck.referenceType());
        entity.setReferenceId(deck.referenceId());
        entity.setTitle(deck.title());
        entity.setSubtitle(deck.subtitle());
        entity.setThemeId(deck.themeId());
        entity.setStatus(deck.status());
        entity.setSourceArtifactId(deck.sourceArtifactId());
        entity.setCreatedAt(deck.createdAt());
        entity.setUpdatedAt(deck.updatedAt());
        List<PresentationSlideEntity> slides = new ArrayList<>();
        for (var slide : deck.slides()) {
            slides.add(presentationSlideMapper.toEntity(entity, slide));
        }
        entity.setSlides(slides);
        return entity;
    }

    public PresentationDeck toDomain(PresentationDeckEntity existing, UpdateDeckRequest request) {
        List<com.willmear.sprint.presentation.domain.PresentationSlide> slides = new ArrayList<>();
        for (int index = 0; index < request.slides().size(); index++) {
            slides.add(presentationSlideMapper.toDomain(existing.getId(), index, request.slides().get(index)));
        }
        return new PresentationDeck(
                existing.getId(),
                existing.getWorkspaceId(),
                existing.getReferenceType(),
                existing.getReferenceId(),
                request.title(),
                request.subtitle(),
                request.themeId() == null || request.themeId().isBlank() ? existing.getThemeId() : request.themeId(),
                request.status(),
                slides,
                existing.getSourceArtifactId(),
                existing.getCreatedAt(),
                existing.getUpdatedAt()
        );
    }
}
