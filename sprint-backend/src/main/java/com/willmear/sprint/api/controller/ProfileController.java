package com.willmear.sprint.api.controller;

import com.willmear.sprint.auth.application.GetCurrentUserProfileUseCase;
import com.willmear.sprint.credits.api.response.UserCreditSummaryResponse;
import com.willmear.sprint.credits.application.GetUserCreditSummaryUseCase;
import com.willmear.sprint.credits.mapper.UserDailyCreditUsageMapper;
import com.willmear.sprint.profile.api.response.UserProfileResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/profile")
public class ProfileController {

    private final GetCurrentUserProfileUseCase getCurrentUserProfileUseCase;
    private final GetUserCreditSummaryUseCase getUserCreditSummaryUseCase;
    private final UserDailyCreditUsageMapper userDailyCreditUsageMapper;

    public ProfileController(
            GetCurrentUserProfileUseCase getCurrentUserProfileUseCase,
            GetUserCreditSummaryUseCase getUserCreditSummaryUseCase,
            UserDailyCreditUsageMapper userDailyCreditUsageMapper
    ) {
        this.getCurrentUserProfileUseCase = getCurrentUserProfileUseCase;
        this.getUserCreditSummaryUseCase = getUserCreditSummaryUseCase;
        this.userDailyCreditUsageMapper = userDailyCreditUsageMapper;
    }

    @GetMapping
    public ResponseEntity<UserProfileResponse> getProfile() {
        return ResponseEntity.ok(getCurrentUserProfileUseCase.get());
    }

    @GetMapping("/credits")
    public ResponseEntity<UserCreditSummaryResponse> getCredits() {
        return ResponseEntity.ok(userDailyCreditUsageMapper.toResponse(getUserCreditSummaryUseCase.get()));
    }
}
