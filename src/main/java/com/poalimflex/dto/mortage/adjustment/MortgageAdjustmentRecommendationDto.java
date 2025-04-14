package com.poalimflex.dto.mortage.adjustment;

import com.poalimflex.dto.mortage.adjustment.MortgageAdjustmentRequestDto.FinancialPressureType;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.List;

/**
 * DTO for mortgage adjustment recommendations
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "AI-powered mortgage adjustment recommendations")
public class MortgageAdjustmentRecommendationDto {
    @Schema(description = "User ID", example = "USER12345")
    private String userId;

    @Schema(description = "Mortgage account number", example = "MORT98765")
    private String mortgageAccountNumber;

    @Schema(description = "Recommended reduction amount", example = "1200.00")
    private BigDecimal recommendedReductionAmount;

    @Schema(description = "Recommended adjustment duration in months", example = "3")
    private Integer recommendedDurationMonths;

    @Schema(description = "Potential financial pressure types detected")
    private List<FinancialPressureType> detectedPressureTypes;

    @Schema(description = "Financial risk score (0.0-1.0)", example = "0.65")
    private Double financialRiskScore;

    @Schema(description = "Recommended repayment strategy")
    private MortgageAdjustmentRequestDto.RepaymentStrategy recommendedRepaymentStrategy;

    @Schema(description = "Projected impact on loan term in months", example = "2")
    private Integer projectedLoanTermImpact;
}

