package com.poalimflex.dto.financial;

import com.poalimflex.dto.mortage.adjustment.MortgageAdjustmentRequestDto.FinancialPressureType;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
/**
 * DTO for financial health report
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Comprehensive financial health report")
public class FinancialHealthReportDto {
    @Schema(description = "User ID", example = "USER12345")
    private String userId;

    @Schema(description = "Report generation date")
    private LocalDate reportDate;

    @Schema(description = "Overall financial health score (0-100)", example = "68.5")
    private Double financialHealthScore;

    @Schema(description = "Debt-to-income ratio", example = "32.7")
    private BigDecimal debtToIncomeRatio;

    @Schema(description = "Credit score", example = "720")
    private Integer creditScore;

    @Schema(description = "Financial stability score", example = "75.3")
    private Double financialStabilityScore;

    @Schema(description = "Probability of payment difficulty (0.0-1.0)", example = "0.35")
    private Double paymentDifficultyProbability;

    @Schema(description = "Eligible for mortgage adjustment", example = "true")
    private Boolean mortgageAdjustmentEligible;

    @Schema(description = "Financial pressure areas")
    private List<FinancialPressureType> financialPressureAreas;

    @Schema(description = "Financial recommendations")
    private List<String> recommendations;
}
