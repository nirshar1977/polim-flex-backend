package com.poalimflex.dto.financial;

import com.poalimflex.dto.mortage.adjustment.MortgageAdjustmentRequestDto.FinancialPressureType;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;


/**
 * DTO for financial stress analysis
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "AI-powered financial stress analysis")
public class FinancialStressAnalysisDto {
    @Schema(description = "High financial stress indicator", example = "true")
    private Boolean highFinancialStress;

    @Schema(description = "Payment difficulty score (0.0-1.0)", example = "0.72")
    private Double paymentDifficultyScore;

    @Schema(description = "Recommended reduction amount", example = "1200.00")
    private BigDecimal recommendedReductionAmount;

    @Schema(description = "Financial health insights")
    private Map<String, Object> financialHealthInsights;

    @Schema(description = "Potential financial pressure types")
    private List<FinancialPressureType> potentialPressureTypes;
}
