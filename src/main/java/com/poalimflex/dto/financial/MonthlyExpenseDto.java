package com.poalimflex.dto.financial;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

/**
 * DTO for monthly expense
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Monthly expense information")
public class MonthlyExpenseDto {
    @Schema(description = "Expense ID", example = "EXP-A1B2C3D4")
    private String id;

    @Schema(description = "Expense type")
    @NotNull(message = "Expense type is required")
    private ExpenseType expenseType;

    @Schema(description = "Expense amount", example = "850.00")
    @NotNull(message = "Amount is required")
    @Positive(message = "Amount must be positive")
    private BigDecimal amount;

    @Schema(description = "Expense description", example = "Monthly rent payment")
    @Size(max = 200, message = "Description cannot exceed 200 characters")
    private String description;

    /**
     * Enum for expense types
     */
    public enum ExpenseType {
        RENT,
        UTILITIES,
        TRANSPORTATION,
        FOOD,
        INSURANCE,
        EDUCATION,
        ENTERTAINMENT,
        OTHER
    }
}
