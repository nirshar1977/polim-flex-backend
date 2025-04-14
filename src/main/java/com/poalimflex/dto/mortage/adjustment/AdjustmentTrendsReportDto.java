package com.poalimflex.dto.mortage.adjustment;

import com.poalimflex.dto.mortage.adjustment.MortgageAdjustmentRequestDto.FinancialPressureType;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Map;

/**
 * DTO for adjustment trends report
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Mortgage adjustment trends report")
public class AdjustmentTrendsReportDto {
    @Schema(description = "Report generation date")
    private LocalDate reportDate;

    @Schema(description = "From date")
    private LocalDate fromDate;

    @Schema(description = "To date")
    private LocalDate toDate;

    @Schema(description = "Monthly request counts")
    private Map<String, Integer> monthlyRequestCounts;

    @Schema(description = "Monthly reduction amounts")
    private Map<String, BigDecimal> monthlyReductionAmounts;

    @Schema(description = "Monthly approval rates (percentage)")
    private Map<String, Double> monthlyApprovalRates;

    @Schema(description = "Financial pressure type distribution")
    private Map<FinancialPressureType, Integer> pressureTypeDistribution;
}
