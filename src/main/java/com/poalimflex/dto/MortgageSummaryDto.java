package com.poalimflex.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;

/**
 * DTO for mortgage summary information
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Mortgage summary information")
public class MortgageSummaryDto {
    @Schema(description = "Mortgage account number", example = "MORT98765")
    private String accountNumber;

    @Schema(description = "Original loan amount", example = "500000.00")
    private BigDecimal originalLoanAmount;

    @Schema(description = "Current loan balance", example = "425000.00")
    private BigDecimal currentBalance;

    @Schema(description = "Monthly payment amount", example = "2500.00")
    private BigDecimal monthlyPayment;

    @Schema(description = "Interest rate", example = "3.75")
    private BigDecimal interestRate;

    @Schema(description = "Remaining loan term in months", example = "321")
    private Integer remainingTermMonths;

    @Schema(description = "Next payment date")
    private LocalDate nextPaymentDate;

    @Schema(description = "Whether the mortgage is active", example = "true")
    private Boolean isActive;
}

