package com.poalimflex.repository;

import com.poalimflex.entity.Mortgage;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

@Repository
public interface MortgageRepository extends MongoRepository<Mortgage, String> {
    /**
     * Find mortgage by account number
     */
    Optional<Mortgage> findByAccountNumber(String accountNumber);

    /**
     * Find mortgages for a specific user
     */
    List<Mortgage> findByUserId(String userId);

    /**
     * Check if user is eligible for mortgage adjustment
     */
    @Query("{ 'userId': ?0, " +
            "'isActive': true, " +
            "'$expr': { " +
            "    $and: [ " +
            "        { $gt: [ { $divide: ['$currentBalance', '$originalLoanAmount'] }, 0.2 ] }, " +
            "        { $gt: ['$remainingTermMonths', 12] } " +
            "    ] " +
            "} }")
    boolean isUserEligibleForAdjustment(String userId);

    /**
     * Find active mortgages with balance above a certain threshold
     */
    List<Mortgage> findByIsActiveTrueAndCurrentBalanceGreaterThan(BigDecimal threshold);

    /**
     * Count active mortgages for a user
     */
    long countByUserIdAndIsActiveTrue(String userId);
}