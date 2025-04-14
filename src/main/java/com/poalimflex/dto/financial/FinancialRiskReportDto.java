package com.poalimflex.dto.financial;

import com.poalimflex.dto.RiskBandDto;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

/**
 * DTO for financial risk report
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Financial risk assessment report")
public class FinancialRiskReportDto {
    @Schema(description = "Report generation date")
    private LocalDate reportDate;

    @Schema(description = "Total assessed users", example = "1045")
    private Integer totalAssessedUsers;

    @Schema(description = "Average risk score", example = "0.41")
    private Double averageRiskScore;

    @Schema(description = "Percentage of high-risk users", example = "20.6")
    private Double highRiskPercentage;

    @Schema(description = "Number of adjustment-eligible users", example = "750")
    private Integer adjustmentEligibleUsers;

    @Schema(description = "Total outstanding balance", example = "8000000.00")
    private BigDecimal totalOutstandingBalance;

    @Schema(description = "Risk bands breakdown")
    private List<RiskBandDto> riskBands;

    @Schema(description = "Estimated default rate percentage", example = "3.2")
    private Double estimatedDefaultRate;
}
