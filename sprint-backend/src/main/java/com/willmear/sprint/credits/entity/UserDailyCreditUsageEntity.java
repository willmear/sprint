package com.willmear.sprint.credits.entity;

import com.willmear.sprint.auth.entity.AppUserEntity;
import com.willmear.sprint.persistence.entity.AuditableEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import java.time.LocalDate;

@Entity
@Table(
        name = "user_daily_credit_usage",
        uniqueConstraints = @UniqueConstraint(name = "uk_user_daily_credit_usage_user_date", columnNames = {"user_id", "usage_date"})
)
public class UserDailyCreditUsageEntity extends AuditableEntity {

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "user_id", nullable = false)
    private AppUserEntity user;

    @Column(name = "usage_date", nullable = false)
    private LocalDate usageDate;

    @Column(name = "generation_count", nullable = false)
    private int generationCount;

    public AppUserEntity getUser() {
        return user;
    }

    public void setUser(AppUserEntity user) {
        this.user = user;
    }

    public LocalDate getUsageDate() {
        return usageDate;
    }

    public void setUsageDate(LocalDate usageDate) {
        this.usageDate = usageDate;
    }

    public int getGenerationCount() {
        return generationCount;
    }

    public void setGenerationCount(int generationCount) {
        this.generationCount = generationCount;
    }
}
