package com.poalimflex.dto.mortage.adjustment;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Detailed Response for Mortgage Payment Adjustment")
public class MortgageAdjustmentResponseDto {
    @Schema(description = "Unique Adjustment Request Identifier",
            example = "ADJ-A1B2C3D4",
            requiredMode = Schema.RequiredMode.REQUIRED)
    private String adjustmentId;

    @Schema(description = "User Identifier",
            example = "USER12345",
            requiredMode = Schema.RequiredMode.REQUIRED)
    private String userId;

    @Schema(description = "Mortgage Account Number",
            example = "MORT98765",
            requiredMode = Schema.RequiredMode.REQUIRED)
    private String mortgageAccountNumber;

    @Schema(description = "Original Monthly Payment",
            example = "6000.00",
            requiredMode = Schema.RequiredMode.REQUIRED)
    private BigDecimal originalMonthlyPayment;

    @Schema(description = "Approved Reduction Amount",
            example = "1500.00",
            requiredMode = Schema.RequiredMode.REQUIRED)
    private BigDecimal approvedReductionAmount;

    @Schema(description = "Adjusted Monthly Payment",
            example = "4500.00",
            requiredMode = Schema.RequiredMode.REQUIRED)
    private BigDecimal adjustedMonthlyPayment;

    @Schema(description = "Additional Interest Accrued",
            example = "35.75",
            requiredMode = Schema.RequiredMode.REQUIRED)
    private BigDecimal additionalInterest;

    @Schema(description = "Adjustment Status",
            example = "APPROVED",
            requiredMode = Schema.RequiredMode.REQUIRED)
    private AdjustmentStatus status;

    @Schema(description = "Detailed Status Description",
            example = "Partial reduction approved based on financial assessment",
            requiredMode = Schema.RequiredMode.NOT_REQUIRED)
    private String statusDescription;

    @Schema(description = "Date of Adjustment Request",
            requiredMode = Schema.RequiredMode.REQUIRED)
    private LocalDateTime requestTimestamp;

    @Schema(description = "Adjustment Month",
            requiredMode = Schema.RequiredMode.REQUIRED)
    private LocalDate adjustmentMonth;

    @Schema(description = "Repayment Schedule Start Date",
            requiredMode = Schema.RequiredMode.REQUIRED)
    private LocalDate repaymentScheduleStart;

    @Schema(description = "Projected Total Additional Cost",
            example = "250.50",
            requiredMode = Schema.RequiredMode.REQUIRED)
    private BigDecimal projectedAdditionalCost;

    @Schema(description = "Risk Assessment Score",
            example = "0.65",
            requiredMode = Schema.RequiredMode.NOT_REQUIRED)
    private Double riskAssessmentScore;

    @Schema(description = "Recommended Future Actions",
            requiredMode = Schema.RequiredMode.NOT_REQUIRED)
    private List<String> recommendedActions;

    @Schema(description = "Detailed Repayment Breakdown",
            requiredMode = Schema.RequiredMode.NOT_REQUIRED)
    private RepaymentBreakdown repaymentBreakdown;

    /**
     * Enum representing the status of the mortgage adjustment request
     */
    public enum AdjustmentStatus {
        APPROVED,
        PARTIALLY_APPROVED,
        REJECTED,
        PENDING_REVIEW,
        NEEDS_ADDITIONAL_INFO
    }

    /**
     * Inner class representing detailed repayment breakdown
     */
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    @Schema(description = "Detailed Repayment Breakdown After Adjustment")
    public static class RepaymentBreakdown {
        @Schema(description = "Remaining Principal",
                example = "250000.00",
                requiredMode = Schema.RequiredMode.REQUIRED)
        private BigDecimal remainingPrincipal;

        @Schema(description = "Total Interest Impact",
                example = "500.25",
                requiredMode = Schema.RequiredMode.REQUIRED)
        private BigDecimal totalInterestImpact;

        @Schema(description = "Projected Loan Completion Date",
                requiredMode = Schema.RequiredMode.REQUIRED)
        private LocalDate projectedLoanCompletionDate;

        @Schema(description = "Months Added to Loan Term Due to Adjustment",
                example = "3",
                requiredMode = Schema.RequiredMode.REQUIRED)
        private Integer additionalMonths;

        @Schema(description = "Detailed Monthly Projections",
                requiredMode = Schema.RequiredMode.NOT_REQUIRED)
        private List<MonthlyProjection> monthlyProjections;
    }

    /**
     * Inner class representing monthly projection details
     */
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    @Schema(description = "Monthly Projection After Mortgage Adjustment")
    public static class MonthlyProjection {
        @Schema(description = "Month of Projection",
                example = "2024-07-01",
                requiredMode = Schema.RequiredMode.REQUIRED)
        private LocalDate month;

        @Schema(description = "Projected Payment",
                example = "4500.00",
                requiredMode = Schema.RequiredMode.REQUIRED)
        private BigDecimal projectedPayment;

        @Schema(description = "Principal Portion",
                example = "2000.00",
                requiredMode = Schema.RequiredMode.REQUIRED)
        private BigDecimal principalPortion;

        @Schema(description = "Interest Portion",
                example = "2500.00",
                requiredMode = Schema.RequiredMode.REQUIRED)
        private BigDecimal interestPortion;
    }
}