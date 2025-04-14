package com.poalimflex.dto.mortage.adjustment;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.List;

/**
 * DTO for adjustment simulation results
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Mortgage adjustment simulation result")
public class AdjustmentSimulationResultDto {
    @Schema(description = "Projected adjusted monthly payment", example = "4500.00")
    private BigDecimal projectedAdjustedPayment;

    @Schema(description = "Total additional interest", example = "250.50")
    private BigDecimal totalAdditionalInterest;

    @Schema(description = "Projected impact on loan term in months", example = "2")
    private Integer projectedLoanTermImpact;

    @Schema(description = "Monthly payment after adjustment period", example = "6100.00")
    private BigDecimal postAdjustmentMonthlyPayment;

    @Schema(description = "Projected monthly breakdown")
    private List<MortgageAdjustmentResponseDto.MonthlyProjection> monthlyProjections;

    @Schema(description = "Financial risk assessment score", example = "0.45")
    private Double riskAssessmentScore;

    @Schema(description = "Eligibility status")
    private Boolean eligibleForAdjustment;

    @Schema(description = "Potential improvement suggestions")
    private List<String> improvementSuggestions;
}