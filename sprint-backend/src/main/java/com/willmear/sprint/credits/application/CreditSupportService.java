package com.willmear.sprint.credits.application;

import com.willmear.sprint.auth.entity.AppUserEntity;
import com.willmear.sprint.auth.repository.AppUserRepository;
import com.willmear.sprint.common.exception.InsufficientDailyCreditsException;
import com.willmear.sprint.common.exception.UserNotFoundException;
import com.willmear.sprint.config.CreditsProperties;
import com.willmear.sprint.credits.domain.UserCreditPolicy;
import com.willmear.sprint.credits.domain.UserCreditSummary;
import com.willmear.sprint.credits.entity.UserDailyCreditUsageEntity;
import com.willmear.sprint.credits.repository.UserDailyCreditUsageRepository;
import jakarta.transaction.Transactional;
import java.time.Clock;
import java.time.LocalDate;
import java.time.ZoneOffset;
import java.util.UUID;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;

@Service
public class CreditSupportService {

    private final CreditsProperties creditsProperties;
    private final AppUserRepository appUserRepository;
    private final UserDailyCreditUsageRepository usageRepository;
    private final Clock clock;

    public CreditSupportService(
            CreditsProperties creditsProperties,
            AppUserRepository appUserRepository,
            UserDailyCreditUsageRepository usageRepository
    ) {
        this.creditsProperties = creditsProperties;
        this.appUserRepository = appUserRepository;
        this.usageRepository = usageRepository;
        this.clock = Clock.systemUTC();
    }

    public UserCreditPolicy getPolicy() {
        return new UserCreditPolicy(
                creditsProperties.dailyGenerationLimit(),
                creditsProperties.enabled()
        );
    }

    public UserCreditSummary getSummary(UUID userId) {
        UserCreditPolicy policy = getPolicy();
        LocalDate usageDate = currentUsageDate();
        int usedToday = usageRepository.findByUser_IdAndUsageDate(userId, usageDate)
                .map(UserDailyCreditUsageEntity::getGenerationCount)
                .orElse(0);
        return buildSummary(userId, usageDate, usedToday, policy);
    }

    @Transactional
    public UserCreditSummary consumeGenerationCredit(UUID userId) {
        UserCreditPolicy policy = getPolicy();
        LocalDate usageDate = currentUsageDate();

        if (!policy.enabled()) {
            return buildSummary(userId, usageDate, 0, policy);
        }

        UserDailyCreditUsageEntity usage = usageRepository.findByUserIdAndUsageDateForUpdate(userId, usageDate)
                .orElseGet(() -> createUsageRow(userId, usageDate));

        if (usage.getGenerationCount() >= policy.dailyGenerationLimit()) {
            throw new InsufficientDailyCreditsException(userId, usageDate, policy.dailyGenerationLimit(), usage.getGenerationCount());
        }

        usage.setGenerationCount(usage.getGenerationCount() + 1);
        UserDailyCreditUsageEntity savedUsage = usageRepository.save(usage);
        return buildSummary(userId, usageDate, savedUsage.getGenerationCount(), policy);
    }

    private UserDailyCreditUsageEntity createUsageRow(UUID userId, LocalDate usageDate) {
        AppUserEntity user = appUserRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundException(userId));

        UserDailyCreditUsageEntity usage = new UserDailyCreditUsageEntity();
        usage.setUser(user);
        usage.setUsageDate(usageDate);
        usage.setGenerationCount(0);

        try {
            return usageRepository.saveAndFlush(usage);
        } catch (DataIntegrityViolationException exception) {
            return usageRepository.findByUserIdAndUsageDateForUpdate(userId, usageDate)
                    .orElseThrow(() -> exception);
        }
    }

    private UserCreditSummary buildSummary(UUID userId, LocalDate usageDate, int usedToday, UserCreditPolicy policy) {
        int remainingToday = policy.enabled()
                ? Math.max(0, policy.dailyGenerationLimit() - usedToday)
                : Math.max(0, policy.dailyGenerationLimit() - usedToday);
        boolean canGenerate = !policy.enabled() || usedToday < policy.dailyGenerationLimit();
        return new UserCreditSummary(
                userId,
                policy.dailyGenerationLimit(),
                usedToday,
                remainingToday,
                usageDate,
                canGenerate
        );
    }

    private LocalDate currentUsageDate() {
        return LocalDate.now(clock.withZone(ZoneOffset.UTC));
    }
}
