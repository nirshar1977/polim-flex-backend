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
@Document(collection = "user_financial_profiles")
public class UserFinancialProfile {
    @Id
    private String id;

    @Field("user_id")
    @Indexed(unique = true)
    private String userId;

    @Field("total_annual_income")
    private BigDecimal totalAnnualIncome;

    @Field("credit_score")
    private Integer creditScore;

    @Field("employment_status")
    private EmploymentStatus employmentStatus;

    @Field("debt_to_income_ratio")
    private BigDecimal debtToIncomeRatio;

    @Field("employment_start_date")
    private LocalDate employmentStartDate;

    @Field("financial_documents")
    private List<FinancialDocument> financialDocuments;

    @Field("monthly_expenses")
    private List<MonthlyExpense> monthlyExpenses;

    @Field("financial_stability_score")
    private Double financialStabilityScore;

    @Field("last_assessment_date")
    private LocalDate lastAssessmentDate;

    // Enum for employment status
    public enum EmploymentStatus {
        FULL_TIME,
        PART_TIME,
        SELF_EMPLOYED,
        CONTRACTOR,
        UNEMPLOYED,
        RETIRED
    }

    // Inner class for financial documents
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class FinancialDocument {
        @Field("id")
        private String id;

        @Field("document_type")
        private DocumentType documentType;

        @Field("file_path")
        private String filePath;

        @Field("upload_date")
        private LocalDate uploadDate;

        // Enum for document types
        public enum DocumentType {
            PAYSLIP,
            TAX_RETURN,
            BANK_STATEMENT,
            EMPLOYMENT_CONTRACT,
            OTHER
        }
    }

    // Inner class for monthly expenses
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class MonthlyExpense {
        @Field("id")
        private String id;

        @Field("expense_type")
        private ExpenseType expenseType;

        @Field("amount")
        private BigDecimal amount;

        @Field("description")
        private String description;

        // Enum for expense types
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
}