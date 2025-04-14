package com.poalimflex.repository;

import com.poalimflex.entity.UserFinancialProfile;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

@Repository
public interface UserFinancialProfileRepository extends MongoRepository<UserFinancialProfile, String> {
    /**
     * Find financial profile by user ID
     */
    Optional<UserFinancialProfile> findByUserId(String userId);

    /**
     * Find profiles with credit score above a threshold
     */
    List<UserFinancialProfile> findByCreditScoreGreaterThan(Integer creditScoreThreshold);

    /**
     * Find profiles with debt-to-income ratio below a threshold
     */
    List<UserFinancialProfile> findByDebtToIncomeRatioLessThan(BigDecimal maxDebtToIncomeRatio);

    /**
     * Count profiles with specific employment status
     */
    long countByEmploymentStatus(UserFinancialProfile.EmploymentStatus status);

    /**
     * Find profiles with financial stability score above a threshold
     */
    @Query("{ 'financialStabilityScore': { $gt: ?0 } }")
    List<UserFinancialProfile> findHighStabilityProfiles(Double stabilityScoreThreshold);
}