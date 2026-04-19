package com.willmear.sprint.credits.application;

import com.willmear.sprint.auth.application.CurrentUserService;
import com.willmear.sprint.credits.domain.UserCreditSummary;
import org.springframework.stereotype.Service;

@Service
public class GetUserCreditSummaryUseCase {

    private final CurrentUserService currentUserService;
    private final CreditSupportService creditSupportService;

    public GetUserCreditSummaryUseCase(
            CurrentUserService currentUserService,
            CreditSupportService creditSupportService
    ) {
        this.currentUserService = currentUserService;
        this.creditSupportService = creditSupportService;
    }

    public UserCreditSummary get() {
        return creditSupportService.getSummary(currentUserService.requireCurrentUserId());
    }
}
