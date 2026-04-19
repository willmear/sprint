package com.willmear.sprint.credits.mapper;

import com.willmear.sprint.auth.entity.AppUserEntity;
import com.willmear.sprint.credits.domain.UserDailyCreditUsage;
import com.willmear.sprint.credits.domain.UserCreditSummary;
import com.willmear.sprint.credits.entity.UserDailyCreditUsageEntity;
import com.willmear.sprint.credits.api.response.UserCreditSummaryResponse;
import org.springframework.stereotype.Component;

@Component
public class UserDailyCreditUsageMapper {

    public UserDailyCreditUsage toDomain(UserDailyCreditUsageEntity entity) {
        return new UserDailyCreditUsage(
                entity.getId(),
                entity.getUser().getId(),
                entity.getUsageDate(),
                entity.getGenerationCount(),
                entity.getCreatedAt(),
                entity.getUpdatedAt()
        );
    }

    public UserDailyCreditUsageEntity toNewEntity(AppUserEntity user, UserDailyCreditUsage usage) {
        UserDailyCreditUsageEntity entity = new UserDailyCreditUsageEntity();
        entity.setId(usage.id());
        entity.setUser(user);
        entity.setUsageDate(usage.usageDate());
        entity.setGenerationCount(usage.generationCount());
        entity.setCreatedAt(usage.createdAt());
        entity.setUpdatedAt(usage.updatedAt());
        return entity;
    }

    public UserCreditSummaryResponse toResponse(UserCreditSummary summary) {
        return new UserCreditSummaryResponse(
                summary.userId(),
                summary.dailyLimit(),
                summary.usedToday(),
                summary.remainingToday(),
                summary.usageDate(),
                summary.canGenerate()
        );
    }
}
