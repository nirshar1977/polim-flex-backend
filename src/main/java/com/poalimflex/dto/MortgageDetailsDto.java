package com.poalimflex.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;

/**
 * DTO for detailed mortgage information
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Detailed mortgage information")
public class MortgageDetailsDto {
    @Schema(description = "Mortgage account number", example = "MORT98765")
    private String accountNumber;

    @Schema(description = "User ID", example = "USER12345")
    private String userId;

    @Schema(description = "Original loan amount", example = "500000.00")
    private BigDecimal originalLoanAmount;

    @Schema(description = "Current loan balance", example = "425000.00")
    private BigDecimal currentBalance;

    @Schema(description = "Interest rate", example = "3.75")
    private BigDecimal interestRate;

    @Schema(description = "Loan start date")
    private LocalDate loanStartDate;

    @Schema(description = "Original loan term in months", example = "360")
    private Integer originalLoanTermMonths;

    @Schema(description = "Remaining loan term in months", example = "321")
    private Integer remainingTermMonths;

    @Schema(description = "Monthly payment amount", example = "2500.00")
    private BigDecimal monthlyPayment;

    @Schema(description = "Mortgage type")
    private MortgageType mortgageType;

    @Schema(description = "Next payment date")
    private LocalDate nextPaymentDate;

    @Schema(description = "Whether the mortgage is active", example = "true")
    private Boolean isActive;

    /**
     * Enum for mortgage types
     */
    public enum MortgageType {
        FIXED_RATE,
        VARIABLE_RATE,
        HYBRID,
        GOVERNMENT_BACKED
    }
}
