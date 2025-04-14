package com.poalimflex.dto.financial;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

/**
 * DTO for user financial profile
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "User financial profile information")
public class FinancialProfileDto {
    @Schema(description = "User ID", example = "USER12345")
    private String userId;

    @Schema(description = "Total annual income", example = "85000.00")
    @NotNull(message = "Annual income is required")
    @Positive(message = "Annual income must be positive")
    private BigDecimal totalAnnualIncome;

    @Schema(description = "Credit score", example = "720")
    @Min(value = 300, message = "Credit score must be at least 300")
    @Max(value = 850, message = "Credit score cannot exceed 850")
    private Integer creditScore;

    @Schema(description = "Employment status")
    private EmploymentStatus employmentStatus;

    @Schema(description = "Debt-to-income ratio", example = "28.5")
    private BigDecimal debtToIncomeRatio;

    @Schema(description = "Employment start date")
    private LocalDate employmentStartDate;

    @Schema(description = "Monthly expenses")
    private List<MonthlyExpenseDto> monthlyExpenses;

    @Schema(description = "Financial stability score (0-100)", example = "75.3")
    private Double financialStabilityScore;

    @Schema(description = "Last assessment date")
    private LocalDate lastAssessmentDate;

    /**
     * Enum for employment status
     */
    public enum EmploymentStatus {
        FULL_TIME,
        PART_TIME,
        SELF_EMPLOYED,
        CONTRACTOR,
        UNEMPLOYED,
        RETIRED
    }
}
