package com.poalimflex.controller;

import com.poalimflex.dto.financial.FinancialStressAnalysisDto;
import com.poalimflex.dto.mortage.adjustment.*;
import com.poalimflex.service.MortgageAdjustmentService;
import com.poalimflex.service.AiFinancialAnalysisService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/mortgage/adjustment")
@RequiredArgsConstructor
@Tag(name = "Mortgage Adjustment", description = "Flexible Mortgage Repayment Management")
public class MortgageAdjustmentController {

    private final MortgageAdjustmentService mortgageAdjustmentService;
    private final AiFinancialAnalysisService aiFinancialAnalysisService;

    @PostMapping("/request")
    @Operation(summary = "Request Mortgage Payment Adjustment",
            description = "Allows customers to temporarily reduce mortgage payments")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Adjustment request processed successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid input parameters"),
            @ApiResponse(responseCode = "404", description = "Mortgage or user not found"),
            @ApiResponse(responseCode = "409", description = "Adjustment conflicts with existing adjustments")
    })
    public ResponseEntity<MortgageAdjustmentResponseDto> requestAdjustment(
            @Valid @RequestBody MortgageAdjustmentRequestDto request
    ) {
        MortgageAdjustmentResponseDto response = mortgageAdjustmentService.processMortgageAdjustment(request);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/eligibility/{userId}")
    @Operation(summary = "Check Mortgage Adjustment Eligibility",
            description = "Determines if a user is eligible for mortgage payment flexibility")
    public ResponseEntity<Boolean> checkEligibility(@PathVariable String userId) {
        boolean isEligible = mortgageAdjustmentService.checkEligibility(userId);
        return ResponseEntity.ok(isEligible);
    }

    @GetMapping("/history/{userId}")
    @Operation(summary = "Get Adjustment History",
            description = "Retrieves the history of mortgage adjustments for a user")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully retrieved adjustment history"),
            @ApiResponse(responseCode = "404", description = "User not found")
    })
    public ResponseEntity<List<MortgageAdjustmentResponseDto>> getAdjustmentHistory(
            @PathVariable String userId,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fromDate,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate toDate
    ) {
        List<MortgageAdjustmentResponseDto> history = mortgageAdjustmentService.getAdjustmentHistory(userId, fromDate, toDate);
        return ResponseEntity.ok(history);
    }

    @GetMapping("/recommendation/{userId}")
    @Operation(summary = "Get Adjustment Recommendations",
            description = "Provides AI-powered recommendations for mortgage adjustments based on financial profile")
    public ResponseEntity<MortgageAdjustmentRecommendationDto> getAdjustmentRecommendation(
            @PathVariable String userId
    ) {
        MortgageAdjustmentRecommendationDto recommendation = mortgageAdjustmentService.generateAdjustmentRecommendation(userId);
        return ResponseEntity.ok(recommendation);
    }

    @PostMapping("/simulate")
    @Operation(summary = "Simulate Adjustment Impact",
            description = "Simulates the financial impact of a proposed mortgage adjustment")
    public ResponseEntity<AdjustmentSimulationResultDto> simulateAdjustment(
            @Valid @RequestBody AdjustmentSimulationRequestDto request
    ) {
        AdjustmentSimulationResultDto result = mortgageAdjustmentService.simulateAdjustment(request);
        return ResponseEntity.ok(result);
    }

    @PutMapping("/cancel/{adjustmentId}")
    @Operation(summary = "Cancel Pending Adjustment",
            description = "Cancels a pending mortgage adjustment request")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Adjustment successfully cancelled"),
            @ApiResponse(responseCode = "404", description = "Adjustment not found"),
            @ApiResponse(responseCode = "409", description = "Adjustment cannot be cancelled (already approved/processed)")
    })
    public ResponseEntity<Void> cancelAdjustment(
            @PathVariable String adjustmentId
    ) {
        mortgageAdjustmentService.cancelAdjustment(adjustmentId);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/financial-stress/{userId}")
    @Operation(summary = "Analyze Financial Stress",
            description = "Analyzes the user's financial stress indicators using AI analysis")
    public ResponseEntity<FinancialStressAnalysisDto> analyzeFinancialStress(
            @PathVariable String userId
    ) {
        boolean isHighStress = aiFinancialAnalysisService.assessFinancialStress(userId);
        double paymentDifficulty = aiFinancialAnalysisService.predictPaymentDifficulty(userId);
        BigDecimal recommendedReduction = aiFinancialAnalysisService.calculateRecommendedReductionAmount(userId);

        FinancialStressAnalysisDto analysis = FinancialStressAnalysisDto.builder()
                .highFinancialStress(isHighStress)
                .paymentDifficultyScore(paymentDifficulty)
                .recommendedReductionAmount(recommendedReduction)
                .financialHealthInsights(aiFinancialAnalysisService.generateFinancialHealthInsights(userId))
                .potentialPressureTypes(aiFinancialAnalysisService.identifyFinancialPressureTypes(userId))
                .build();

        return ResponseEntity.ok(analysis);
    }

    @PostMapping("/long-term-impact")
    @Operation(summary = "Analyze Long-term Financial Impact",
            description = "Analyzes the long-term financial impact of proposed mortgage adjustments")
    public ResponseEntity<Map<String, Object>> analyzeLongTermImpact(
            @RequestParam String userId,
            @RequestParam BigDecimal reductionAmount
    ) {
        Map<String, Object> impact = aiFinancialAnalysisService.predictLongTermFinancialImpact(userId, reductionAmount);
        return ResponseEntity.ok(impact);
    }
}