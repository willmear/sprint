package com.willmear.sprint.credits.application;

import com.willmear.sprint.credits.api.CreditService;
import com.willmear.sprint.credits.domain.UserCreditSummary;
import org.springframework.stereotype.Service;

@Service
public class CreditApplicationService implements CreditService {

    private final GetUserCreditSummaryUseCase getUserCreditSummaryUseCase;
    private final CheckGenerationCreditAvailabilityUseCase checkGenerationCreditAvailabilityUseCase;
    private final ConsumeGenerationCreditUseCase consumeGenerationCreditUseCase;

    public CreditApplicationService(
            GetUserCreditSummaryUseCase getUserCreditSummaryUseCase,
            CheckGenerationCreditAvailabilityUseCase checkGenerationCreditAvailabilityUseCase,
            ConsumeGenerationCreditUseCase consumeGenerationCreditUseCase
    ) {
        this.getUserCreditSummaryUseCase = getUserCreditSummaryUseCase;
        this.checkGenerationCreditAvailabilityUseCase = checkGenerationCreditAvailabilityUseCase;
        this.consumeGenerationCreditUseCase = consumeGenerationCreditUseCase;
    }

    @Override
    public UserCreditSummary getCurrentUserCreditSummary() {
        return getUserCreditSummaryUseCase.get();
    }

    @Override
    public UserCreditSummary checkCurrentUserGenerationAvailability() {
        return checkGenerationCreditAvailabilityUseCase.check();
    }

    @Override
    public UserCreditSummary consumeCurrentUserGenerationCredit() {
        return consumeGenerationCreditUseCase.consume();
    }
}
