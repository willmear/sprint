package com.willmear.sprint.presentation.entity;

import com.willmear.sprint.persistence.converter.StringListConverter;
import com.willmear.sprint.persistence.entity.AuditableEntity;
import com.willmear.sprint.presentation.domain.SlideLayoutType;
import com.willmear.sprint.presentation.domain.SlideType;
import jakarta.persistence.Column;
import jakarta.persistence.Convert;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import java.util.ArrayList;
import java.util.List;
import org.hibernate.annotations.Fetch;
import org.hibernate.annotations.FetchMode;

@Entity
@Table(name = "presentation_slide")
public class PresentationSlideEntity extends AuditableEntity {

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "deck_id", nullable = false)
    private PresentationDeckEntity deck;

    @Column(name = "slide_order", nullable = false)
    private Integer slideOrder;

    @Enumerated(EnumType.STRING)
    @Column(name = "slide_type", nullable = false, length = 50)
    private SlideType slideType;

    @Column(nullable = false, length = 255)
    private String title;

    @Convert(converter = StringListConverter.class)
    @Column(name = "bullet_points", columnDefinition = "text", nullable = false)
    private List<String> bulletPoints;

    @Column(name = "body_text", columnDefinition = "text")
    private String bodyText;

    @Column(name = "speaker_notes", columnDefinition = "text")
    private String speakerNotes;

    @Column(name = "section_label", length = 100)
    private String sectionLabel;

    @Enumerated(EnumType.STRING)
    @Column(name = "layout_type", nullable = false, length = 50)
    private SlideLayoutType layoutType;

    @Column(nullable = false)
    private boolean hidden;

    @OneToMany(mappedBy = "slide", cascade = jakarta.persistence.CascadeType.ALL, orphanRemoval = true)
    @jakarta.persistence.OrderBy("elementOrder ASC")
    @Fetch(FetchMode.SUBSELECT)
    private List<PresentationSlideElementEntity> elements = new ArrayList<>();

    public PresentationDeckEntity getDeck() {
        return deck;
    }

    public void setDeck(PresentationDeckEntity deck) {
        this.deck = deck;
    }

    public Integer getSlideOrder() {
        return slideOrder;
    }

    public void setSlideOrder(Integer slideOrder) {
        this.slideOrder = slideOrder;
    }

    public SlideType getSlideType() {
        return slideType;
    }

    public void setSlideType(SlideType slideType) {
        this.slideType = slideType;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public List<String> getBulletPoints() {
        return bulletPoints;
    }

    public void setBulletPoints(List<String> bulletPoints) {
        this.bulletPoints = bulletPoints;
    }

    public String getBodyText() {
        return bodyText;
    }

    public void setBodyText(String bodyText) {
        this.bodyText = bodyText;
    }

    public String getSpeakerNotes() {
        return speakerNotes;
    }

    public void setSpeakerNotes(String speakerNotes) {
        this.speakerNotes = speakerNotes;
    }

    public String getSectionLabel() {
        return sectionLabel;
    }

    public void setSectionLabel(String sectionLabel) {
        this.sectionLabel = sectionLabel;
    }

    public SlideLayoutType getLayoutType() {
        return layoutType;
    }

    public void setLayoutType(SlideLayoutType layoutType) {
        this.layoutType = layoutType;
    }

    public boolean isHidden() {
        return hidden;
    }

    public void setHidden(boolean hidden) {
        this.hidden = hidden;
    }

    public List<PresentationSlideElementEntity> getElements() {
        return elements;
    }

    public void setElements(List<PresentationSlideElementEntity> elements) {
        this.elements = elements;
    }
}
