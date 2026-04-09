package com.willmear.sprint.sprintreview.entity;

import com.willmear.sprint.persistence.entity.AuditableEntity;
import jakarta.persistence.Column;
import jakarta.persistence.MappedSuperclass;
import java.util.UUID;

@MappedSuperclass
public class SprintReviewEntity extends AuditableEntity {

    @Column(name = "sprint_id", nullable = false, unique = true)
    private UUID sprintId;

    @Column(nullable = false)
    private String status;

    @Column(columnDefinition = "text")
    private String markdown;

    public UUID getSprintId() {
        return sprintId;
    }

    public void setSprintId(UUID sprintId) {
        this.sprintId = sprintId;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getMarkdown() {
        return markdown;
    }

    public void setMarkdown(String markdown) {
        this.markdown = markdown;
    }
}
