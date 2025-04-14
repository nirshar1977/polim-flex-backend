package com.poalimflex.service;

import com.poalimflex.dto.MortgageAdjustmentRequestDto;
import com.poalimflex.entity.UserFinancialProfile;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

public interface AiFinancialAnalysisService {
    /**
     * Assess financial stress for a given user
     *
     * @param userId Unique identifier of the user
     * @return Boolean indicating high financial stress
     */
    boolean assessFinancialStress(String userId);

    /**
     * Predict likelihood of mortgage payment difficulty
     *
     * @param userId Unique identifier of the user
     * @return Probability of payment difficulty (0.0 - 1.0)
     */
    double predictPaymentDifficulty(String userId);

    /**
     * Analyze potential financial pressure types
     *
     * @param userId Unique identifier of the user
     * @return List of potential financial pressure types
     */
    List<MortgageAdjustmentRequestDto.FinancialPressureType> identifyFinancialPressureTypes(String userId);

    /**
     * Calculate recommended mortgage adjustment amount
     *
     * @param userId Unique identifier of the user
     * @return Recommended reduction amount
     */
    BigDecimal calculateRecommendedReductionAmount(String userId);

    /**
     * Generate financial health insights
     *
     * @param userId Unique identifier of the user
     * @return Map of financial health indicators
     */
    Map<String, Object> generateFinancialHealthInsights(String userId);

    /**
     * Evaluate user's eligibility for flexible mortgage adjustment
     *
     * @param userFinancialProfile User's financial profile
     * @return Eligibility score (0.0 - 1.0)
     */
    double evaluateFlexibilityEligibility(UserFinancialProfile userFinancialProfile);

    /**
     * Predict long-term financial impact of mortgage adjustment
     *
     * @param userId Unique identifier of the user
     * @param reductionAmount Proposed reduction amount
     * @return Projected financial impact details
     */
    Map<String, Object> predictLongTermFinancialImpact(String userId, BigDecimal reductionAmount);
}