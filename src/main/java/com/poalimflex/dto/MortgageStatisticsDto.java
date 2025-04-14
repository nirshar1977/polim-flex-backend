package com.poalimflex.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

/**
 * DTO for mortgage statistics
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Mortgage statistics summary")
public class MortgageStatisticsDto {
    @Schema(description = "Total original loan amount", example = "750000.00")
    private BigDecimal totalOriginalLoanAmount;

    @Schema(description = "Total current balance", example = "620000.00")
    private BigDecimal totalCurrentBalance;

    @Schema(description = "Weighted average interest rate", example = "3.85")
    private BigDecimal averageInterestRate;

    @Schema(description = "Total monthly payment", example = "3750.00")
    private BigDecimal totalMonthlyPayment;

    @Schema(description = "Repayment progress percentage", example = "17.33")
    private BigDecimal repaymentProgressPercentage;

    @Schema(description = "Total active mortgages", example = "2")
    private Integer totalActiveMortgages;
}
