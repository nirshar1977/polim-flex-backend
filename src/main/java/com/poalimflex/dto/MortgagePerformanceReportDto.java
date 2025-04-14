package com.poalimflex.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;


/**
 * DTO for mortgage performance report
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Mortgage performance report")
public class MortgagePerformanceReportDto {
    @Schema(description = "Report generation date")
    private LocalDate reportDate;

    @Schema(description = "Total active mortgages", example = "1053")
    private Integer totalActiveMortgages;

    @Schema(description = "Total mortgage value", example = "275000000.00")
    private BigDecimal totalMortgageValue;

    @Schema(description = "Average mortgage amount", example = "261158.59")
    private BigDecimal averageMortgageAmount;

    @Schema(description = "Average interest rate", example = "3.85")
    private BigDecimal averageInterestRate;

    @Schema(description = "Average remaining term in months", example = "278")
    private Integer averageRemainingTerm;

    @Schema(description = "Number of mortgages with adjustments", example = "157")
    private Integer mortgagesWithAdjustments;

    @Schema(description = "Adjustment utilization rate percentage", example = "14.9")
    private Double adjustmentUtilizationRate;

    @Schema(description = "Average loan-to-value ratio", example = "68.5")
    private BigDecimal averageLoanToValueRatio;

    @Schema(description = "Delinquency rate percentage", example = "1.2")
    private BigDecimal delinquencyRate;
}
