package com.willmear.sprint.workspace.entity;

import com.willmear.sprint.auth.entity.AppUserEntity;
import com.willmear.sprint.persistence.entity.AuditableEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

@Entity
@Table(name = "workspace")
public class WorkspaceEntity extends AuditableEntity {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "owner_user_id")
    private AppUserEntity owner;

    @Column(nullable = false, length = 255)
    private String name;

    @Column(columnDefinition = "text")
    private String description;

    public String getName() {
        return name;
    }

    public AppUserEntity getOwner() {
        return owner;
    }

    public void setOwner(AppUserEntity owner) {
        this.owner = owner;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
}
