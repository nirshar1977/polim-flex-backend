package com.poalimflex.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Document(collection = "mortgage_adjustments")
public class MortgageAdjustment {
    @Id
    private String id;

    @Field("mortgage_id")
    private String mortgageId;

    @Field("adjustment_date")
    private LocalDateTime adjustmentDate;

    @Field("original_monthly_payment")
    private BigDecimal originalMonthlyPayment;

    @Field("reduced_payment")
    private BigDecimal reducedPayment;

    @Field("additional_interest")
    private BigDecimal additionalInterest;

    @Field("status")
    private AdjustmentStatus status;

    @Field("adjustment_month")
    private LocalDate adjustmentMonth;

    @Field("repayment_start_date")
    private LocalDate repaymentStartDate;

    @Field("reason")
    private String reason;

    @Field("financial_pressure_type")
    private FinancialPressureType financialPressureType;

    @Field("risk_assessment_score")
    private Double riskAssessmentScore;

    // Enum for adjustment status
    public enum AdjustmentStatus {
        APPROVED,
        PARTIALLY_APPROVED,
        REJECTED,
        PENDING_REVIEW
    }

    // Enum for financial pressure types
    public enum FinancialPressureType {
        EDUCATION,
        MEDICAL_EXPENSES,
        HOME_REPAIRS,
        FAMILY_EMERGENCY,
        CAREER_TRANSITION,
        OTHER
    }
}