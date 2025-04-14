package com.poalimflex.controller;

import com.poalimflex.dto.*;
import com.poalimflex.dto.financial.FinancialRiskReportDto;
import com.poalimflex.dto.mortage.adjustment.AdjustmentSummaryReportDto;
import com.poalimflex.dto.mortage.adjustment.AdjustmentTrendsReportDto;
import com.poalimflex.dto.mortage.adjustment.MortgageAdjustmentRequestDto;
import com.poalimflex.dto.user.UserDemographicsReportDto;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.*;

/**
 * Controller for administrative reporting and analytics
 */
@RestController
@RequestMapping("/api/admin/reports")
@RequiredArgsConstructor
@Slf4j
@PreAuthorize("hasRole('ADMIN')")
@SecurityRequirement(name = "bearerAuth")
@Tag(name = "Admin Reporting", description = "Administrative APIs for system-wide reporting and analytics")
public class AdminReportingController {

    @GetMapping("/adjustment-summary")
    @Operation(summary = "Get Adjustment Summary",
            description = "Retrieves summary statistics for mortgage adjustments")
    public ResponseEntity<AdjustmentSummaryReportDto> getAdjustmentSummary(
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fromDate,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate toDate) {

        log.info("Generating adjustment summary report from {} to {}", fromDate, toDate);

        // Mock implementation - in a real application, calculate from repository data
        AdjustmentSummaryReportDto report = AdjustmentSummaryReportDto.builder()
                .reportDate(LocalDate.now())
                .totalAdjustmentRequests(157)
                .approvedAdjustments(128)
                .partiallyApprovedAdjustments(12)
                .rejectedAdjustments(17)
                .totalReductionAmount(new BigDecimal("765000.00"))
                .averageReductionAmount(new BigDecimal("6000.00"))
                .totalAdditionalInterest(new BigDecimal("40250.75"))
                .mostCommonPressureType(MortgageAdjustmentRequestDto.FinancialPressureType.EDUCATION)
                .averageRiskScore(0.48)
                .build();

        return ResponseEntity.ok(report);
    }

    @GetMapping("/user-demographics")
    @Operation(summary = "Get User Demographics",
            description = "Retrieves demographic information about system users")
    public ResponseEntity<UserDemographicsReportDto> getUserDemographics() {
        log.info("Generating user demographics report");

        // Mock implementation - in a real application, calculate from repository data
        Map<String, Integer> employmentStatusDistribution = new HashMap<>();
        employmentStatusDistribution.put("FULL_TIME", 750);
        employmentStatusDistribution.put("PART_TIME", 125);
        employmentStatusDistribution.put("SELF_EMPLOYED", 87);
        employmentStatusDistribution.put("CONTRACTOR", 45);
        employmentStatusDistribution.put("UNEMPLOYED", 12);
        employmentStatusDistribution.put("RETIRED", 34);

        Map<String, Integer> ageDistribution = new HashMap<>();
        ageDistribution.put("18-25", 95);
        ageDistribution.put("26-35", 320);
        ageDistribution.put("36-45", 405);
        ageDistribution.put("46-55", 178);
        ageDistribution.put("56-65", 43);
        ageDistribution.put("65+", 12);

        Map<String, Integer> incomeDistribution = new HashMap<>();
        incomeDistribution.put("Under 30K", 85);
        incomeDistribution.put("30K-50K", 245);
        incomeDistribution.put("50K-75K", 320);
        incomeDistribution.put("75K-100K", 215);
        incomeDistribution.put("100K-150K", 142);
        incomeDistribution.put("150K+", 46);

        Map<String, Integer> creditScoreDistribution = new HashMap<>();
        creditScoreDistribution.put("Under 600", 68);
        creditScoreDistribution.put("600-650", 125);
        creditScoreDistribution.put("650-700", 248);
        creditScoreDistribution.put("700-750", 317);
        creditScoreDistribution.put("750-800", 215);
        creditScoreDistribution.put("800+", 80);

        UserDemographicsReportDto report = UserDemographicsReportDto.builder()
                .reportDate(LocalDate.now())
                .totalUsers(1053)
                .activeUsers(978)
                .employmentStatusDistribution(employmentStatusDistribution)
                .ageDistribution(ageDistribution)
                .incomeDistribution(incomeDistribution)
                .creditScoreDistribution(creditScoreDistribution)
                .averageAnnualIncome(new BigDecimal("78500.00"))
                .averageCreditScore(712)
                .build();

        return ResponseEntity.ok(report);
    }

    @GetMapping("/financial-risk")
    @Operation(summary = "Get Financial Risk Report",
            description = "Retrieves system-wide financial risk assessment")
    public ResponseEntity<FinancialRiskReportDto> getFinancialRiskReport() {
        log.info("Generating financial risk report");

        // Mock implementation - in a real application, calculate from repository data
        List<RiskBandDto> riskBands = new ArrayList<>();
        riskBands.add(new RiskBandDto("Low Risk (0.0-0.3)", 450, new BigDecimal("2500000.00")));
        riskBands.add(new RiskBandDto("Moderate Risk (0.3-0.6)", 380, new BigDecimal("3750000.00")));
        riskBands.add(new RiskBandDto("High Risk (0.6-0.9)", 170, new BigDecimal("1200000.00")));
        riskBands.add(new RiskBandDto("Very High Risk (0.9-1.0)", 45, new BigDecimal("550000.00")));

        FinancialRiskReportDto report = FinancialRiskReportDto.builder()
                .reportDate(LocalDate.now())
                .totalAssessedUsers(1045)
                .averageRiskScore(0.41)
                .highRiskPercentage(20.6)
                .adjustmentEligibleUsers(750)
                .totalOutstandingBalance(new BigDecimal("8000000.00"))
                .riskBands(riskBands)
                .estimatedDefaultRate(3.2)
                .build();

        return ResponseEntity.ok(report);
    }

    @GetMapping("/adjustment-trends")
    @Operation(summary = "Get Adjustment Trends",
            description = "Retrieves trends in mortgage adjustment requests over time")
    public ResponseEntity<AdjustmentTrendsReportDto> getAdjustmentTrends(
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fromDate,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate toDate) {

        log.info("Generating adjustment trends report from {} to {}", fromDate, toDate);

        // Use default date range if not specified
        LocalDate start = fromDate != null ? fromDate : LocalDate.now().minusMonths(6);
        LocalDate end = toDate != null ? toDate : LocalDate.now();

        // Mock implementation - in a real application, calculate from repository data
        Map<String, Integer> monthlyRequests = new HashMap<>();
        monthlyRequests.put("2024-11", 18);
        monthlyRequests.put("2024-12", 25);
        monthlyRequests.put("2025-01", 32);
        monthlyRequests.put("2025-02", 27);
        monthlyRequests.put("2025-03", 35);
        monthlyRequests.put("2025-04", 20);

        Map<String, BigDecimal> monthlyReductionAmounts = new HashMap<>();
        monthlyReductionAmounts.put("2024-11", new BigDecimal("108000.00"));
        monthlyReductionAmounts.put("2024-12", new BigDecimal("150000.00"));
        monthlyReductionAmounts.put("2025-01", new BigDecimal("192000.00"));
        monthlyReductionAmounts.put("2025-02", new BigDecimal("162000.00"));
        monthlyReductionAmounts.put("2025-03", new BigDecimal("210000.00"));
        monthlyReductionAmounts.put("2025-04", new BigDecimal("120000.00"));

        Map<String, Double> monthlyApprovalRates = new HashMap<>();
        monthlyApprovalRates.put("2024-11", 83.3);
        monthlyApprovalRates.put("2024-12", 80.0);
        monthlyApprovalRates.put("2025-01", 87.5);
        monthlyApprovalRates.put("2025-02", 85.2);
        monthlyApprovalRates.put("2025-03", 82.9);
        monthlyApprovalRates.put("2025-04", 85.0);

        Map<MortgageAdjustmentRequestDto.FinancialPressureType, Integer> pressureTypeDistribution = new HashMap<>();
        pressureTypeDistribution.put(MortgageAdjustmentRequestDto.FinancialPressureType.EDUCATION, 45);
        pressureTypeDistribution.put(MortgageAdjustmentRequestDto.FinancialPressureType.MEDICAL_EXPENSES, 35);
        pressureTypeDistribution.put(MortgageAdjustmentRequestDto.FinancialPressureType.HOME_REPAIRS, 22);
        pressureTypeDistribution.put(MortgageAdjustmentRequestDto.FinancialPressureType.FAMILY_EMERGENCY, 18);
        pressureTypeDistribution.put(MortgageAdjustmentRequestDto.FinancialPressureType.CAREER_TRANSITION, 28);
        pressureTypeDistribution.put(MortgageAdjustmentRequestDto.FinancialPressureType.OTHER, 9);

        AdjustmentTrendsReportDto report = AdjustmentTrendsReportDto.builder()
                .reportDate(LocalDate.now())
                .fromDate(start)
                .toDate(end)
                .monthlyRequestCounts(monthlyRequests)
                .monthlyReductionAmounts(monthlyReductionAmounts)
                .monthlyApprovalRates(monthlyApprovalRates)
                .pressureTypeDistribution(pressureTypeDistribution)
                .build();

        return ResponseEntity.ok(report);
    }

    @GetMapping("/mortgage-performance")
    @Operation(summary = "Get Mortgage Performance Report",
            description = "Retrieves performance metrics for mortgages in the system")
    public ResponseEntity<MortgagePerformanceReportDto> getMortgagePerformance() {
        log.info("Generating mortgage performance report");

        // Mock implementation - in a real application, calculate from repository data
        MortgagePerformanceReportDto report = MortgagePerformanceReportDto.builder()
                .reportDate(LocalDate.now())
                .totalActiveMortgages(1053)
                .totalMortgageValue(new BigDecimal("275000000.00"))
                .averageMortgageAmount(new BigDecimal("261158.59"))
                .averageInterestRate(new BigDecimal("3.85"))
                .averageRemainingTerm(278)
                .mortgagesWithAdjustments(157)
                .adjustmentUtilizationRate(14.9)
                .averageLoanToValueRatio(new BigDecimal("68.5"))
                .delinquencyRate(new BigDecimal("1.2"))
                .build();

        return ResponseEntity.ok(report);
    }
}
