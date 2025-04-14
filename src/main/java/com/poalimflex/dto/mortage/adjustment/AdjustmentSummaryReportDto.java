package com.poalimflex.dto.mortage.adjustment;

import com.poalimflex.dto.mortage.adjustment.MortgageAdjustmentRequestDto.FinancialPressureType;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;


/**
 * DTO for mortgage adjustment summary report
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Mortgage adjustment summary report")
public class AdjustmentSummaryReportDto {
    @Schema(description = "Report generation date")
    private LocalDate reportDate;

    @Schema(description = "Total adjustment requests", example = "157")
    private Integer totalAdjustmentRequests;

    @Schema(description = "Approved adjustments", example = "128")
    private Integer approvedAdjustments;

    @Schema(description = "Partially approved adjustments", example = "12")
    private Integer partiallyApprovedAdjustments;

    @Schema(description = "Rejected adjustments", example = "17")
    private Integer rejectedAdjustments;

    @Schema(description = "Total reduction amount", example = "765000.00")
    private BigDecimal totalReductionAmount;

    @Schema(description = "Average reduction amount", example = "6000.00")
    private BigDecimal averageReductionAmount;

    @Schema(description = "Total additional interest", example = "40250.75")
    private BigDecimal totalAdditionalInterest;

    @Schema(description = "Most common financial pressure type")
    private FinancialPressureType mostCommonPressureType;

    @Schema(description = "Average risk assessment score", example = "0.48")
    private Double averageRiskScore;
}
