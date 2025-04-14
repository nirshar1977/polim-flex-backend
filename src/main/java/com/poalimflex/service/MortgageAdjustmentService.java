package com.poalimflex.service;

import com.poalimflex.dto.MortgageAdjustmentRequestDto;
import com.poalimflex.dto.MortgageAdjustmentResponseDto;

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
}