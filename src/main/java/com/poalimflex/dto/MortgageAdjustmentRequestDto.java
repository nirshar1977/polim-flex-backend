package com.poalimflex.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Detailed Mortgage Payment Adjustment Request")
public class MortgageAdjustmentRequestDto {
    @Schema(description = "Unique User Identifier",
            example = "USER12345",
            requiredMode = Schema.RequiredMode.REQUIRED)
    @NotBlank(message = "User ID cannot be blank")
    @Pattern(regexp = "^USER\\d{5}$", message = "User ID must follow the pattern USER12345")
    private String userId;

    @Schema(description = "Mortgage Account Number",
            example = "MORT98765",
            requiredMode = Schema.RequiredMode.REQUIRED)
    @NotBlank(message = "Mortgage account number cannot be blank")
    @Pattern(regexp = "^MORT\\d{5}$", message = "Mortgage account number must follow the pattern MORT98765")
    private String mortgageAccountNumber;

    @Schema(description = "Requested Reduction Amount",
            example = "1500.00",
            requiredMode = Schema.RequiredMode.REQUIRED)
    @NotNull(message = "Reduction amount is required")
    @Positive(message = "Reduction amount must be positive")
    @DecimalMax(value = "10000.00", message = "Reduction amount cannot exceed 10,000")
    @DecimalMin(value = "100.00", message = "Minimum reduction amount is 100")
    private BigDecimal reductionAmount;

    @Schema(description = "Desired Adjustment Month",
            example = "2024-07-01",
            requiredMode = Schema.RequiredMode.REQUIRED)
    @NotNull(message = "Adjustment month is required")
    @Future(message = "Adjustment month must be in the future")
    private LocalDate adjustmentMonth;

    @Schema(description = "Reason for Adjustment",
            example = "Financial Pressure",
            requiredMode = Schema.RequiredMode.NOT_REQUIRED)
    @Size(max = 500, message = "Reason cannot exceed 500 characters")
    private String adjustmentReason;

    @Schema(description = "Type of Financial Pressure",
            example = "EDUCATION",
            requiredMode = Schema.RequiredMode.NOT_REQUIRED)
    private FinancialPressureType financialPressureType;

    @Schema(description = "Preferred Repayment Strategy",
            example = "SPREAD_EVENLY",
            requiredMode = Schema.RequiredMode.NOT_REQUIRED)
    private RepaymentStrategy repaymentStrategy;

    @Schema(description = "Additional Supporting Documentation Available",
            example = "true")
    private boolean hasSuportingDocumentation;

    /**
     * Enum representing types of financial pressure
     */
    public enum FinancialPressureType {
        EDUCATION,
        MEDICAL_EXPENSES,
        HOME_REPAIRS,
        FAMILY_EMERGENCY,
        CAREER_TRANSITION,
        OTHER
    }

    /**
     * Enum representing different repayment strategies
     */
    public enum RepaymentStrategy {
        SPREAD_EVENLY,
        BACK_LOADED,
        FRONT_LOADED,
        FLEXIBLE
    }
}