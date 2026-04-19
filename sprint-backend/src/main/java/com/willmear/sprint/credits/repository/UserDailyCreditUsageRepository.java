package com.willmear.sprint.credits.repository;

import com.willmear.sprint.credits.entity.UserDailyCreditUsageEntity;
import java.time.LocalDate;
import java.util.Optional;
import java.util.UUID;
import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;

public interface UserDailyCreditUsageRepository extends JpaRepository<UserDailyCreditUsageEntity, UUID> {

    Optional<UserDailyCreditUsageEntity> findByUser_IdAndUsageDate(UUID userId, LocalDate usageDate);

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("select usage from UserDailyCreditUsageEntity usage where usage.user.id = :userId and usage.usageDate = :usageDate")
    Optional<UserDailyCreditUsageEntity> findByUserIdAndUsageDateForUpdate(UUID userId, LocalDate usageDate);
}
