package com.willmear.sprint.artifact.entity;

import com.fasterxml.jackson.databind.JsonNode;
import com.willmear.sprint.artifact.domain.ArtifactStatus;
import com.willmear.sprint.artifact.domain.ArtifactType;
import com.willmear.sprint.persistence.converter.JsonNodeConverter;
import com.willmear.sprint.persistence.entity.AuditableEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Convert;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Table;
import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "artifact")
public class ArtifactEntity extends AuditableEntity {

    @Column(name = "workspace_id", nullable = false)
    private UUID workspaceId;

    @Enumerated(EnumType.STRING)
    @Column(name = "artifact_type", nullable = false, length = 100)
    private ArtifactType artifactType;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 50)
    private ArtifactStatus status;

    @Column(name = "reference_type", nullable = false, length = 100)
    private String referenceType;

    @Column(name = "reference_id", nullable = false, length = 255)
    private String referenceId;

    @Convert(converter = JsonNodeConverter.class)
    @Column(name = "structured_content", columnDefinition = "text")
    private JsonNode structuredContent;

    @Column(name = "rendered_markdown", columnDefinition = "text")
    private String renderedMarkdown;

    @Column(length = 255)
    private String title;

    @Column(columnDefinition = "text")
    private String summary;

    @Column(name = "generator_type", length = 100)
    private String generatorType;

    @Column(name = "generator_version", length = 100)
    private String generatorVersion;

    @Column(name = "generated_at", nullable = false)
    private Instant generatedAt;

    public UUID getWorkspaceId() {
        return workspaceId;
    }

    public void setWorkspaceId(UUID workspaceId) {
        this.workspaceId = workspaceId;
    }

    public ArtifactType getArtifactType() {
        return artifactType;
    }

    public void setArtifactType(ArtifactType artifactType) {
        this.artifactType = artifactType;
    }

    public ArtifactStatus getStatus() {
        return status;
    }

    public void setStatus(ArtifactStatus status) {
        this.status = status;
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

    public JsonNode getStructuredContent() {
        return structuredContent;
    }

    public void setStructuredContent(JsonNode structuredContent) {
        this.structuredContent = structuredContent;
    }

    public String getRenderedMarkdown() {
        return renderedMarkdown;
    }

    public void setRenderedMarkdown(String renderedMarkdown) {
        this.renderedMarkdown = renderedMarkdown;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getSummary() {
        return summary;
    }

    public void setSummary(String summary) {
        this.summary = summary;
    }

    public String getGeneratorType() {
        return generatorType;
    }

    public void setGeneratorType(String generatorType) {
        this.generatorType = generatorType;
    }

    public String getGeneratorVersion() {
        return generatorVersion;
    }

    public void setGeneratorVersion(String generatorVersion) {
        this.generatorVersion = generatorVersion;
    }

    public Instant getGeneratedAt() {
        return generatedAt;
    }

    public void setGeneratedAt(Instant generatedAt) {
        this.generatedAt = generatedAt;
    }
}
