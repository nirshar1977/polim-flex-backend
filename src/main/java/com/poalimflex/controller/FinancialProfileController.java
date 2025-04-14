package com.poalimflex.controller;

import com.poalimflex.dto.financial.FinancialHealthReportDto;
import com.poalimflex.dto.financial.FinancialProfileDto;
import com.poalimflex.dto.financial.MonthlyExpenseDto;
import com.poalimflex.entity.UserFinancialProfile;
import com.poalimflex.repository.UserFinancialProfileRepository;
import com.poalimflex.service.AiFinancialAnalysisService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/financial-profile")
@RequiredArgsConstructor
@Slf4j
@Tag(name = "Financial Profile", description = "APIs for user financial profile management and analysis")
public class FinancialProfileController {

    private final UserFinancialProfileRepository userFinancialProfileRepository;
    private final AiFinancialAnalysisService aiFinancialAnalysisService;

    @GetMapping("/{userId}")
    @Operation(summary = "Get User Financial Profile",
            description = "Retrieves the financial profile of a specific user")
    @ApiResponse(responseCode = "200", description = "Successfully retrieved financial profile")
    @ApiResponse(responseCode = "404", description = "Financial profile not found")
    public ResponseEntity<FinancialProfileDto> getUserFinancialProfile(@PathVariable String userId) {
        return userFinancialProfileRepository.findByUserId(userId)
                .map(this::convertToDto)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping("/{userId}")
    @Operation(summary = "Update User Financial Profile",
            description = "Updates or creates a user's financial profile")
    public ResponseEntity<FinancialProfileDto> updateUserFinancialProfile(
            @PathVariable String userId,
            @Valid @RequestBody FinancialProfileDto profileDto) {

        // Find existing profile or create new one
        UserFinancialProfile profile = userFinancialProfileRepository.findByUserId(userId)
                .orElse(new UserFinancialProfile());

        // Update profile fields
        profile.setUserId(userId);
        profile.setTotalAnnualIncome(profileDto.getTotalAnnualIncome());
        profile.setCreditScore(profileDto.getCreditScore());
        profile.setEmploymentStatus(convertEmploymentStatus(profileDto.getEmploymentStatus()));
        profile.setDebtToIncomeRatio(profileDto.getDebtToIncomeRatio());
        profile.setEmploymentStartDate(profileDto.getEmploymentStartDate());
        profile.setLastAssessmentDate(LocalDate.now());

        // Handle monthly expenses
        if (profileDto.getMonthlyExpenses() != null) {
            profile.setMonthlyExpenses(profileDto.getMonthlyExpenses().stream()
                    .map(this::convertToEntityExpense)
                    .collect(Collectors.toList()));
        }

        // Calculate financial stability score
        double stabilityScore = calculateFinancialStabilityScore(profileDto);
        profile.setFinancialStabilityScore(stabilityScore);

        // Save and return updated profile
        UserFinancialProfile savedProfile = userFinancialProfileRepository.save(profile);
        return ResponseEntity.ok(convertToDto(savedProfile));
    }

    @GetMapping("/{userId}/health-report")
    @Operation(summary = "Get Financial Health Report",
            description = "Generates a comprehensive financial health report for a user")
    public ResponseEntity<FinancialHealthReportDto> getFinancialHealthReport(@PathVariable String userId) {
        // Check if user has a financial profile
        if (!userFinancialProfileRepository.findByUserId(userId).isPresent()) {
            return ResponseEntity.notFound().build();
        }

        // Get AI-powered financial insights
        Map<String, Object> insights = aiFinancialAnalysisService.generateFinancialHealthInsights(userId);
        double paymentDifficulty = aiFinancialAnalysisService.predictPaymentDifficulty(userId);

        // Build health report
        FinancialHealthReportDto report = FinancialHealthReportDto.builder()
                .userId(userId)
                .reportDate(LocalDate.now())
                .financialHealthScore(calculateOverallHealthScore(userId, insights, paymentDifficulty))
                .debtToIncomeRatio((BigDecimal) insights.get("debtToIncomeRatio"))
                .creditScore((Integer) insights.get("creditScore"))
                .financialStabilityScore((Double) insights.get("financialStabilityScore"))
                .paymentDifficultyProbability(paymentDifficulty)
                .mortgageAdjustmentEligible(paymentDifficulty > 0.3)
                .financialPressureAreas(aiFinancialAnalysisService.identifyFinancialPressureTypes(userId))
                .recommendations(generateFinancialRecommendations(userId, insights, paymentDifficulty))
                .build();

        return ResponseEntity.ok(report);
    }

    @GetMapping("/{userId}/expenses")
    @Operation(summary = "Get User Monthly Expenses",
            description = "Retrieves the monthly expenses of a specific user")
    public ResponseEntity<List<MonthlyExpenseDto>> getUserMonthlyExpenses(@PathVariable String userId) {
        return userFinancialProfileRepository.findByUserId(userId)
                .map(profile -> profile.getMonthlyExpenses() != null ?
                        profile.getMonthlyExpenses().stream()
                                .map(this::convertToExpenseDto)
                                .collect(Collectors.toList()) :
                        List.<MonthlyExpenseDto>of())
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping("/{userId}/expenses")
    @Operation(summary = "Add Monthly Expense",
            description = "Adds a new monthly expense to the user's financial profile")
    public ResponseEntity<FinancialProfileDto> addMonthlyExpense(
            @PathVariable String userId,
            @Valid @RequestBody MonthlyExpenseDto expenseDto) {

        UserFinancialProfile profile = userFinancialProfileRepository.findByUserId(userId)
                .orElseThrow(() -> new RuntimeException("User financial profile not found"));

        // Convert and add the new expense
        UserFinancialProfile.MonthlyExpense newExpense = convertToEntityExpense(expenseDto);

        if (profile.getMonthlyExpenses() == null) {
            profile.setMonthlyExpenses(List.of(newExpense));
        } else {
            profile.getMonthlyExpenses().add(newExpense);
        }

        // Recalculate debt-to-income ratio
        updateDebtToIncomeRatio(profile);

        // Save and return updated profile
        UserFinancialProfile savedProfile = userFinancialProfileRepository.save(profile);
        return ResponseEntity.ok(convertToDto(savedProfile));
    }

    @DeleteMapping("/{userId}/expenses/{expenseId}")
    @Operation(summary = "Delete Monthly Expense",
            description = "Removes a monthly expense from the user's financial profile")
    public ResponseEntity<Void> deleteMonthlyExpense(
            @PathVariable String userId,
            @PathVariable String expenseId) {

        UserFinancialProfile profile = userFinancialProfileRepository.findByUserId(userId)
                .orElseThrow(() -> new RuntimeException("User financial profile not found"));

        if (profile.getMonthlyExpenses() != null) {
            profile.setMonthlyExpenses(profile.getMonthlyExpenses().stream()
                    .filter(expense -> !expense.getId().equals(expenseId))
                    .collect(Collectors.toList()));

            // Recalculate debt-to-income ratio
            updateDebtToIncomeRatio(profile);

            userFinancialProfileRepository.save(profile);
        }

        return ResponseEntity.noContent().build();
    }

    // Helper methods
    private FinancialProfileDto convertToDto(UserFinancialProfile profile) {
        return FinancialProfileDto.builder()
                .userId(profile.getUserId())
                .totalAnnualIncome(profile.getTotalAnnualIncome())
                .creditScore(profile.getCreditScore())
                .employmentStatus(convertDtoEmploymentStatus(profile.getEmploymentStatus()))
                .debtToIncomeRatio(profile.getDebtToIncomeRatio())
                .employmentStartDate(profile.getEmploymentStartDate())
                .monthlyExpenses(profile.getMonthlyExpenses() != null ?
                        profile.getMonthlyExpenses().stream()
                                .map(this::convertToExpenseDto)
                                .collect(Collectors.toList()) :
                        null)
                .financialStabilityScore(profile.getFinancialStabilityScore())
                .lastAssessmentDate(profile.getLastAssessmentDate())
                .build();
    }

    private MonthlyExpenseDto convertToExpenseDto(UserFinancialProfile.MonthlyExpense expense) {
        return MonthlyExpenseDto.builder()
                .id(expense.getId())
                .expenseType(convertDtoExpenseType(expense.getExpenseType()))
                .amount(expense.getAmount())
                .description(expense.getDescription())
                .build();
    }

    private UserFinancialProfile.MonthlyExpense convertToEntityExpense(MonthlyExpenseDto dto) {
        return UserFinancialProfile.MonthlyExpense.builder()
                .id(dto.getId() != null ? dto.getId() : generateExpenseId())
                .expenseType(convertExpenseType(dto.getExpenseType()))
                .amount(dto.getAmount())
                .description(dto.getDescription())
                .build();
    }

    private String generateExpenseId() {
        return "EXP-" + java.util.UUID.randomUUID().toString().substring(0, 8);
    }

    private UserFinancialProfile.EmploymentStatus convertEmploymentStatus(
            FinancialProfileDto.EmploymentStatus status) {
        return switch (status) {
            case FULL_TIME -> UserFinancialProfile.EmploymentStatus.FULL_TIME;
            case PART_TIME -> UserFinancialProfile.EmploymentStatus.PART_TIME;
            case SELF_EMPLOYED -> UserFinancialProfile.EmploymentStatus.SELF_EMPLOYED;
            case CONTRACTOR -> UserFinancialProfile.EmploymentStatus.CONTRACTOR;
            case UNEMPLOYED -> UserFinancialProfile.EmploymentStatus.UNEMPLOYED;
            case RETIRED -> UserFinancialProfile.EmploymentStatus.RETIRED;
        };
    }

    private FinancialProfileDto.EmploymentStatus convertDtoEmploymentStatus(
            UserFinancialProfile.EmploymentStatus status) {
        return switch (status) {
            case FULL_TIME -> FinancialProfileDto.EmploymentStatus.FULL_TIME;
            case PART_TIME -> FinancialProfileDto.EmploymentStatus.PART_TIME;
            case SELF_EMPLOYED -> FinancialProfileDto.EmploymentStatus.SELF_EMPLOYED;
            case CONTRACTOR -> FinancialProfileDto.EmploymentStatus.CONTRACTOR;
            case UNEMPLOYED -> FinancialProfileDto.EmploymentStatus.UNEMPLOYED;
            case RETIRED -> FinancialProfileDto.EmploymentStatus.RETIRED;
        };
    }

    private UserFinancialProfile.MonthlyExpense.ExpenseType convertExpenseType(
            MonthlyExpenseDto.ExpenseType type) {
        return switch (type) {
            case RENT -> UserFinancialProfile.MonthlyExpense.ExpenseType.RENT;
            case UTILITIES -> UserFinancialProfile.MonthlyExpense.ExpenseType.UTILITIES;
            case TRANSPORTATION -> UserFinancialProfile.MonthlyExpense.ExpenseType.TRANSPORTATION;
            case FOOD -> UserFinancialProfile.MonthlyExpense.ExpenseType.FOOD;
            case INSURANCE -> UserFinancialProfile.MonthlyExpense.ExpenseType.INSURANCE;
            case EDUCATION -> UserFinancialProfile.MonthlyExpense.ExpenseType.EDUCATION;
            case ENTERTAINMENT -> UserFinancialProfile.MonthlyExpense.ExpenseType.ENTERTAINMENT;
            case OTHER -> UserFinancialProfile.MonthlyExpense.ExpenseType.OTHER;
        };
    }

    private MonthlyExpenseDto.ExpenseType convertDtoExpenseType(
            UserFinancialProfile.MonthlyExpense.ExpenseType type) {
        return switch (type) {
            case RENT -> MonthlyExpenseDto.ExpenseType.RENT;
            case UTILITIES -> MonthlyExpenseDto.ExpenseType.UTILITIES;
            case TRANSPORTATION -> MonthlyExpenseDto.ExpenseType.TRANSPORTATION;
            case FOOD -> MonthlyExpenseDto.ExpenseType.FOOD;
            case INSURANCE -> MonthlyExpenseDto.ExpenseType.INSURANCE;
            case EDUCATION -> MonthlyExpenseDto.ExpenseType.EDUCATION;
            case ENTERTAINMENT -> MonthlyExpenseDto.ExpenseType.ENTERTAINMENT;
            case OTHER -> MonthlyExpenseDto.ExpenseType.OTHER;
        };
    }

    private double calculateFinancialStabilityScore(FinancialProfileDto profile) {
        // Credit score contribution (0-30 points)
        double creditScoreComponent = profile.getCreditScore() != null ?
                Math.min(30, (profile.getCreditScore() - 300) * 30.0 / 550) : 0;

        // Employment stability (0-30 points)
        double employmentStability = 0;
        if (profile.getEmploymentStatus() == FinancialProfileDto.EmploymentStatus.FULL_TIME) {
            employmentStability = 30;
        } else if (profile.getEmploymentStatus() == FinancialProfileDto.EmploymentStatus.PART_TIME) {
            employmentStability = 20;
        } else if (profile.getEmploymentStatus() == FinancialProfileDto.EmploymentStatus.SELF_EMPLOYED) {
            employmentStability = 15;
        }

        // Debt-to-income ratio (0-40 points, lower is better)
        double dtiComponent = profile.getDebtToIncomeRatio() != null ?
                Math.max(0, 40 - (profile.getDebtToIncomeRatio().doubleValue() * 40.0 / 80)) : 0;

        // Calculate total score (0-100)
        return creditScoreComponent + employmentStability + dtiComponent;
    }

    private void updateDebtToIncomeRatio(UserFinancialProfile profile) {
        if (profile.getTotalAnnualIncome() == null ||
                profile.getTotalAnnualIncome().compareTo(BigDecimal.ZERO) <= 0 ||
                profile.getMonthlyExpenses() == null ||
                profile.getMonthlyExpenses().isEmpty()) {
            return;
        }

        // Calculate total monthly expenses
        BigDecimal totalMonthlyExpenses = profile.getMonthlyExpenses().stream()
                .map(UserFinancialProfile.MonthlyExpense::getAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        // Convert annual income to monthly
        BigDecimal monthlyIncome = profile.getTotalAnnualIncome().divide(BigDecimal.valueOf(12), 2, RoundingMode.HALF_UP);

        // Calculate DTI ratio (as a percentage)
        if (monthlyIncome.compareTo(BigDecimal.ZERO) > 0) {
            BigDecimal dtiRatio = totalMonthlyExpenses
                    .divide(monthlyIncome, 4, RoundingMode.HALF_UP)
                    .multiply(BigDecimal.valueOf(100))
                    .setScale(2, RoundingMode.HALF_UP);

            profile.setDebtToIncomeRatio(dtiRatio);
        }
    }

    private double calculateOverallHealthScore(String userId, Map<String, Object> insights, double paymentDifficulty) {
        // Get components from insights
        double stabilityScore = (double) insights.get("financialStabilityScore");
        BigDecimal dtiRatio = (BigDecimal) insights.get("debtToIncomeRatio");

        // DTI impact (lower is better)
        double dtiImpact = 40 - Math.min(40, dtiRatio.doubleValue() / 2);

        // Payment difficulty impact (lower difficulty is better)
        double paymentDifficultyImpact = 30 * (1 - paymentDifficulty);

        // Stability score impact
        double stabilityImpact = 30 * (stabilityScore / 100);

        // Calculate total health score (0-100)
        return dtiImpact + paymentDifficultyImpact + stabilityImpact;
    }

    private List<String> generateFinancialRecommendations(
            String userId, Map<String, Object> insights, double paymentDifficulty) {

        List<String> recommendations = new ArrayList<>();

        // Get components from insights
        double stabilityScore = (double) insights.get("financialStabilityScore");
        BigDecimal dtiRatio = (BigDecimal) insights.get("debtToIncomeRatio");

        // DTI-based recommendations
        if (dtiRatio.compareTo(BigDecimal.valueOf(40)) > 0) {
            recommendations.add("Consider debt consolidation to reduce your debt-to-income ratio");
        } else if (dtiRatio.compareTo(BigDecimal.valueOf(30)) > 0) {
            recommendations.add("Review monthly expenses to identify potential areas for reduction");
        }

        // Payment difficulty recommendations
        if (paymentDifficulty > 0.7) {
            recommendations.add("Consider applying for mortgage payment adjustment immediately");
            recommendations.add("Schedule a financial consultation for a comprehensive review");
        } else if (paymentDifficulty > 0.5) {
            recommendations.add("Review budget to identify areas where expenses can be reduced");
            recommendations.add("Consider a short-term mortgage adjustment to ease financial pressure");
        } else if (paymentDifficulty > 0.3) {
            recommendations.add("Build an emergency fund to prepare for potential financial pressures");
        }

        // Stability score recommendations
        if (stabilityScore < 60) {
            recommendations.add("Focus on improving credit score to enhance overall financial stability");
        }

        return recommendations;
    }
}