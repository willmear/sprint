package com.willmear.sprint.presentation.entity;

import com.willmear.sprint.presentation.domain.DeckStatus;
import com.willmear.sprint.persistence.entity.AuditableEntity;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import org.hibernate.annotations.Fetch;
import org.hibernate.annotations.FetchMode;

@Entity
@Table(name = "presentation_deck")
public class PresentationDeckEntity extends AuditableEntity {

    @Column(name = "workspace_id", nullable = false)
    private UUID workspaceId;

    @Column(name = "reference_type", nullable = false, length = 100)
    private String referenceType;

    @Column(name = "reference_id", nullable = false, length = 255)
    private String referenceId;

    @Column(nullable = false, length = 255)
    private String title;

    @Column(length = 255)
    private String subtitle;

    @Column(name = "theme_id", length = 100)
    private String themeId;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 50)
    private DeckStatus status;

    @Column(name = "source_artifact_id")
    private UUID sourceArtifactId;

    @OneToMany(mappedBy = "deck", cascade = CascadeType.ALL, orphanRemoval = true)
    @jakarta.persistence.OrderBy("slideOrder ASC")
    @Fetch(FetchMode.SUBSELECT)
    private List<PresentationSlideEntity> slides = new ArrayList<>();

    public UUID getWorkspaceId() {
        return workspaceId;
    }

    public void setWorkspaceId(UUID workspaceId) {
        this.workspaceId = workspaceId;
    }

    public String getReferenceType() {
        return referenceType;
    }

    public void setReferenceType(String referenceType) {
        this.referenceType = referenceType;
    }

    public String getReferenceId() {
        return referenceId;
    }

    public void setReferenceId(String referenceId) {
        this.referenceId = referenceId;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getSubtitle() {
        return subtitle;
    }

    public void setSubtitle(String subtitle) {
        this.subtitle = subtitle;
    }

    public String getThemeId() {
        return themeId;
    }

    public void setThemeId(String themeId) {
        this.themeId = themeId;
    }

    public DeckStatus getStatus() {
        return status;
    }

    public void setStatus(DeckStatus status) {
        this.status = status;
    }

    public UUID getSourceArtifactId() {
        return sourceArtifactId;
    }

    public void setSourceArtifactId(UUID sourceArtifactId) {
        this.sourceArtifactId = sourceArtifactId;
    }

    public List<PresentationSlideEntity> getSlides() {
        return slides;
    }

    public void setSlides(List<PresentationSlideEntity> slides) {
        this.slides = slides;
    }
}
