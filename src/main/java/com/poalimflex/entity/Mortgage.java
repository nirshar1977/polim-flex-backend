package com.poalimflex.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Document(collection = "mortgages")
public class Mortgage {
    @Id
    private String id;

    @Field("account_number")
    @Indexed(unique = true)
    private String accountNumber;

    @Field("user_id")
    @Indexed
    private String userId;

    @Field("original_loan_amount")
    private BigDecimal originalLoanAmount;

    @Field("current_balance")
    private BigDecimal currentBalance;

    @Field("interest_rate")
    private BigDecimal interestRate;

    @Field("loan_start_date")
    private LocalDate loanStartDate;

    @Field("original_loan_term_months")
    private Integer originalLoanTermMonths;

    @Field("monthly_payment")
    private BigDecimal monthlyPayment;

    @Field("mortgage_type")
    private MortgageType mortgageType;

    @Field("remaining_term_months")
    private Integer remainingTermMonths;

    @Field("adjustments")
    private List<MortgageAdjustment> adjustments;

    @Field("next_payment_date")
    private LocalDate nextPaymentDate;

    @Field("is_active")
    private Boolean isActive;

    // Enum for different mortgage types
    public enum MortgageType {
        FIXED_RATE,
        VARIABLE_RATE,
        HYBRID,
        GOVERNMENT_BACKED
    }
}