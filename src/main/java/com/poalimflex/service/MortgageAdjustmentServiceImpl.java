package com.poalimflex.service;

import com.poalimflex.dto.MortgageAdjustmentRequestDto;
import com.poalimflex.dto.MortgageAdjustmentResponseDto;
import com.poalimflex.dto.MortgageAdjustmentResponseDto.AdjustmentStatus;
import com.poalimflex.repository.MortgageRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.UUID;

@Service
@Slf4j
@RequiredArgsConstructor
public class MortgageAdjustmentServiceImpl implements MortgageAdjustmentService {
    private final MortgageRepository mortgageRepository;
    private final AiFinancialAnalysisService aiFinancialAnalysisService;

    @Override
    @Transactional
    public MortgageAdjustmentResponseDto processMortgageAdjustment(MortgageAdjustmentRequestDto request) {
        // Log the incoming request
        log.info("Processing mortgage adjustment request for user: {}", request.getUserId());

        // Check eligibility
        if (!checkEligibility(request.getUserId())) {
            return MortgageAdjustmentResponseDto.builder()
                    .status(AdjustmentStatus.REJECTED)
                    .statusDescription("User not eligible for mortgage adjustment")
                    .build();
        }

        // Perform AI-powered financial stress analysis
        boolean isHighRisk = aiFinancialAnalysisService.assessFinancialStress(request.getUserId());

        // Validate and process the adjustment
        BigDecimal approvedReduction = validateAndCalculateReduction(request, isHighRisk);

        // Calculate additional interest
        BigDecimal additionalInterest = calculateAdditionalInterestAmount(request, approvedReduction);

        // Generate unique adjustment ID
        String adjustmentId = generateAdjustmentId();

        // Build and return response
        return MortgageAdjustmentResponseDto.builder()
                .adjustmentId(adjustmentId)
                .approvedReductionAmount(approvedReduction)
                .status(determineAdjustmentStatus(approvedReduction, request))
                .statusDescription(generateStatusDescription(approvedReduction, isHighRisk))
                .additionalInterest(additionalInterest)
                .repaymentScheduleStart(request.getAdjustmentMonth())
                .build();
    }

    @Override
    public boolean checkEligibility(String userId) {
        // Implement eligibility checks
        // Consider factors like:
        // - Payment history
        // - Loan-to-value ratio
        // - Credit score
        // - Number of previous adjustments
        return mortgageRepository.isUserEligibleForAdjustment(userId);
    }

    private BigDecimal validateAndCalculateReduction(
            MortgageAdjustmentRequestDto request,
            boolean isHighRisk
    ) {
        BigDecimal maxAllowedReduction = calculateMaxReduction(request.getUserId());

        // Adjust reduction based on risk assessment
        if (isHighRisk) {
            return request.getReductionAmount()
                    .min(maxAllowedReduction.multiply(BigDecimal.valueOf(0.5)));
        }

        return request.getReductionAmount()
                .min(maxAllowedReduction);
    }

    private BigDecimal calculateAdditionalInterestAmount(
            MortgageAdjustmentRequestDto request,
            BigDecimal approvedReduction
    ) {
        // Complex interest calculation logic
        // Consider:
        // - Original loan terms
        // - Reduction amount
        // - Remaining loan period
        return BigDecimal.ZERO; // Placeholder
    }

    private String generateAdjustmentId() {
        return "ADJ-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase();
    }

    private AdjustmentStatus determineAdjustmentStatus(
            BigDecimal approvedReduction,
            MortgageAdjustmentRequestDto request
    ) {
        if (approvedReduction.compareTo(request.getReductionAmount()) == 0) {
            return AdjustmentStatus.APPROVED;
        } else if (approvedReduction.signum() > 0) {
            return AdjustmentStatus.PARTIALLY_APPROVED;
        }

        return AdjustmentStatus.REJECTED;
    }

    private String generateStatusDescription(
            BigDecimal approvedReduction,
            boolean isHighRisk
    ) {
        if (isHighRisk) {
            return "Reduced adjustment due to high financial risk";
        }

        return "Mortgage adjustment processed successfully";
    }

    private BigDecimal calculateMaxReduction(String userId) {
        // Implement logic to calculate maximum allowed reduction
        // Based on user's mortgage details, income, etc.
        return BigDecimal.valueOf(5000);
    }
}