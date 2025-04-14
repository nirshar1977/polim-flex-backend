package com.poalimflex.dto.mortage.adjustment;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;


/**
 * DTO for adjustment simulation requests
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Mortgage adjustment simulation request")
public class AdjustmentSimulationRequestDto {
    @Schema(description = "User ID", example = "USER12345")
    @NotBlank(message = "User ID cannot be blank")
    private String userId;

    @Schema(description = "Mortgage account number", example = "MORT98765")
    @NotBlank(message = "Mortgage account number cannot be blank")
    private String mortgageAccountNumber;

    @Schema(description = "Proposed reduction amount", example = "1500.00")
    @NotNull(message = "Reduction amount is required")
    @Positive(message = "Reduction amount must be positive")
    private BigDecimal proposedReductionAmount;

    @Schema(description = "Proposed adjustment start date", example = "2024-07-01")
    @NotNull(message = "Adjustment start date is required")
    @Future(message = "Adjustment start date must be in the future")
    private LocalDate proposedStartDate;

    @Schema(description = "Proposed adjustment duration in months", example = "3")
    @NotNull(message = "Duration is required")
    @Min(value = 1, message = "Duration must be at least 1 month")
    @Max(value = 12, message = "Duration cannot exceed 12 months")
    private Integer durationMonths;

    @Schema(description = "Proposed repayment strategy")
    private MortgageAdjustmentRequestDto.RepaymentStrategy repaymentStrategy;
}