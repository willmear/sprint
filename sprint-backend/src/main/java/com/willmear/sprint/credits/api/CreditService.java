package com.willmear.sprint.credits.api;

import com.willmear.sprint.credits.domain.UserCreditSummary;

public interface CreditService {

    UserCreditSummary getCurrentUserCreditSummary();

    UserCreditSummary checkCurrentUserGenerationAvailability();

    UserCreditSummary consumeCurrentUserGenerationCredit();
}
