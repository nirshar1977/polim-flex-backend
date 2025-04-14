package com.poalimflex.dto.user;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Map;

/**
 * DTO for user demographics report
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "User demographics report")
public class UserDemographicsReportDto {
    @Schema(description = "Report generation date")
    private LocalDate reportDate;

    @Schema(description = "Total users", example = "1053")
    private Integer totalUsers;

    @Schema(description = "Active users", example = "978")
    private Integer activeUsers;

    @Schema(description = "Employment status distribution")
    private Map<String, Integer> employmentStatusDistribution;

    @Schema(description = "Age distribution")
    private Map<String, Integer> ageDistribution;

    @Schema(description = "Income distribution")
    private Map<String, Integer> incomeDistribution;

    @Schema(description = "Credit score distribution")
    private Map<String, Integer> creditScoreDistribution;

    @Schema(description = "Average annual income", example = "78500.00")
    private BigDecimal averageAnnualIncome;

    @Schema(description = "Average credit score", example = "712")
    private Integer averageCreditScore;
}

