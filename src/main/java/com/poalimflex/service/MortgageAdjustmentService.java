package com.poalimflex.service;

import com.poalimflex.dto.mortage.adjustment.*;

import java.time.LocalDate;
import java.util.List;

/**
 * Interface for mortgage adjustment services
 */
public interface MortgageAdjustmentService {
    /**
     * Process a mortgage payment adjustment request
     *
     * @param request The mortgage adjustment request details
     * @return Detailed response of the adjustment request
     */
    MortgageAdjustmentResponseDto processMortgageAdjustment(MortgageAdjustmentRequestDto request);

    /**
     * Check if a user is eligible for mortgage payment adjustment
     *
     * @param userId The unique identifier of the user
     * @return Boolean indicating eligibility for adjustment
     */
    boolean checkEligibility(String userId);

    /**
     * Calculate additional interest for the proposed adjustment
     *
     * @param request The mortgage adjustment request
     * @return Calculated additional interest amount
     */
    default double calculateAdditionalInterest(MortgageAdjustmentRequestDto request) {
        // Default implementation can be overridden
        return 0.0;
    }

    /**
     * Retrieve adjustment history for a user
     *
     * @param userId The unique identifier of the user
     * @param fromDate Optional start date for filtering
     * @param toDate Optional end date for filtering
     * @return List of adjustment history entries
     */
    List<MortgageAdjustmentResponseDto> getAdjustmentHistory(String userId, LocalDate fromDate, LocalDate toDate);

    /**
     * Generate AI-powered adjustment recommendations
     *
     * @param userId The unique identifier of the user
     * @return Personalized adjustment recommendations
     */
    MortgageAdjustmentRecommendationDto generateAdjustmentRecommendation(String userId);

    /**
     * Simulate the impact of a proposed adjustment
     *
     * @param request The simulation request parameters
     * @return Detailed simulation results
     */
    AdjustmentSimulationResultDto simulateAdjustment(AdjustmentSimulationRequestDto request);

    /**
     * Cancel a pending adjustment request
     *
     * @param adjustmentId The unique identifier of the adjustment
     */
    void cancelAdjustment(String adjustmentId);
}