package com.willmear.sprint.credits.application;

import com.willmear.sprint.auth.application.CurrentUserService;
import com.willmear.sprint.credits.domain.UserCreditSummary;
import org.springframework.stereotype.Service;

@Service
public class ConsumeGenerationCreditUseCase {

    private final CurrentUserService currentUserService;
    private final CreditSupportService creditSupportService;

    public ConsumeGenerationCreditUseCase(
            CurrentUserService currentUserService,
            CreditSupportService creditSupportService
    ) {
        this.currentUserService = currentUserService;
        this.creditSupportService = creditSupportService;
    }

    public UserCreditSummary consume() {
        return creditSupportService.consumeGenerationCredit(currentUserService.requireCurrentUserId());
    }
}
