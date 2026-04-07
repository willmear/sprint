package com.willmear.sprint.persistence.entity;

import jakarta.persistence.MappedSuperclass;

@MappedSuperclass
public abstract class AuditableEntity extends BaseEntity {
    // Semantic extension point for entities that participate in audit-related concerns.
}
