package com.willmear.sprint.presentation.entity;

import com.willmear.sprint.persistence.entity.AuditableEntity;
import com.willmear.sprint.presentation.domain.SlideElementRole;
import com.willmear.sprint.presentation.domain.SlideElementType;
import com.willmear.sprint.presentation.domain.ShapeType;
import com.willmear.sprint.presentation.domain.TextAlignment;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

@Entity
@Table(name = "presentation_slide_element")
public class PresentationSlideElementEntity extends AuditableEntity {

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "slide_id", nullable = false)
    private PresentationSlideEntity slide;

    @Column(name = "element_order", nullable = false)
    private Integer elementOrder;

    @Enumerated(EnumType.STRING)
    @Column(name = "element_type", nullable = false, length = 50)
    private SlideElementType elementType;

    @Enumerated(EnumType.STRING)
    @Column(name = "element_role", nullable = false, length = 50)
    private SlideElementRole role;

    @Column(name = "text_content", columnDefinition = "text", nullable = false)
    private String textContent;

    @Column(name = "position_x", nullable = false)
    private Double x;

    @Column(name = "position_y", nullable = false)
    private Double y;

    @Column(name = "width_px", nullable = false)
    private Double width;

    @Column(name = "height_px", nullable = false)
    private Double height;

    @Column(name = "z_index", nullable = false)
    private Integer zIndex;

    @Column(name = "rotation_degrees")
    private Double rotationDegrees;

    @Column(name = "fill_color", length = 20)
    private String fillColor;

    @Column(name = "border_color", length = 20)
    private String borderColor;

    @Column(name = "border_width")
    private Integer borderWidth;

    @Column(name = "text_color", length = 20)
    private String textColor;

    @Column(name = "font_family", length = 120)
    private String fontFamily;

    @Column(name = "font_size")
    private Integer fontSize;

    @Column(name = "is_bold", nullable = false)
    private boolean bold;

    @Column(name = "is_italic", nullable = false)
    private boolean italic;

    @Column(name = "is_underline", nullable = false)
    private boolean underline;

    @Enumerated(EnumType.STRING)
    @Column(name = "text_alignment", length = 20)
    private TextAlignment textAlignment;

    @Enumerated(EnumType.STRING)
    @Column(name = "shape_type", length = 40)
    private ShapeType shapeType;

    @Column(name = "hidden", nullable = false)
    private boolean hidden;

    public PresentationSlideEntity getSlide() {
        return slide;
    }

    public void setSlide(PresentationSlideEntity slide) {
        this.slide = slide;
    }

    public Integer getElementOrder() {
        return elementOrder;
    }

    public void setElementOrder(Integer elementOrder) {
        this.elementOrder = elementOrder;
    }

    public SlideElementType getElementType() {
        return elementType;
    }

    public void setElementType(SlideElementType elementType) {
        this.elementType = elementType;
    }

    public SlideElementRole getRole() {
        return role;
    }

    public void setRole(SlideElementRole role) {
        this.role = role;
    }

    public String getTextContent() {
        return textContent;
    }

    public void setTextContent(String textContent) {
        this.textContent = textContent;
    }

    public Double getX() {
        return x;
    }

    public void setX(Double x) {
        this.x = x;
    }

    public Double getY() {
        return y;
    }

    public void setY(Double y) {
        this.y = y;
    }

    public Double getWidth() {
        return width;
    }

    public void setWidth(Double width) {
        this.width = width;
    }

    public Double getHeight() {
        return height;
    }

    public void setHeight(Double height) {
        this.height = height;
    }

    public Integer getZIndex() {
        return zIndex;
    }

    public void setZIndex(Integer zIndex) {
        this.zIndex = zIndex;
    }

    public Double getRotationDegrees() {
        return rotationDegrees;
    }

    public void setRotationDegrees(Double rotationDegrees) {
        this.rotationDegrees = rotationDegrees;
    }

    public String getFillColor() {
        return fillColor;
    }

    public void setFillColor(String fillColor) {
        this.fillColor = fillColor;
    }

    public String getBorderColor() {
        return borderColor;
    }

    public void setBorderColor(String borderColor) {
        this.borderColor = borderColor;
    }

    public Integer getBorderWidth() {
        return borderWidth;
    }

    public void setBorderWidth(Integer borderWidth) {
        this.borderWidth = borderWidth;
    }

    public String getTextColor() {
        return textColor;
    }

    public void setTextColor(String textColor) {
        this.textColor = textColor;
    }

    public String getFontFamily() {
        return fontFamily;
    }

    public void setFontFamily(String fontFamily) {
        this.fontFamily = fontFamily;
    }

    public Integer getFontSize() {
        return fontSize;
    }

    public void setFontSize(Integer fontSize) {
        this.fontSize = fontSize;
    }

    public boolean isBold() {
        return bold;
    }

    public void setBold(boolean bold) {
        this.bold = bold;
    }

    public boolean isItalic() {
        return italic;
    }

    public void setItalic(boolean italic) {
        this.italic = italic;
    }

    public boolean isUnderline() {
        return underline;
    }

    public void setUnderline(boolean underline) {
        this.underline = underline;
    }

    public TextAlignment getTextAlignment() {
        return textAlignment;
    }

    public void setTextAlignment(TextAlignment textAlignment) {
        this.textAlignment = textAlignment;
    }

    public ShapeType getShapeType() {
        return shapeType;
    }

    public void setShapeType(ShapeType shapeType) {
        this.shapeType = shapeType;
    }

    public boolean isHidden() {
        return hidden;
    }

    public void setHidden(boolean hidden) {
        this.hidden = hidden;
    }
}
