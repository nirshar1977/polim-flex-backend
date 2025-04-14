package com.poalimflex.service;

import com.poalimflex.dto.MortgageAdjustmentRequestDto;
import com.poalimflex.entity.UserFinancialProfile;
import com.poalimflex.repository.UserFinancialProfileRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@Slf4j
@RequiredArgsConstructor
public class AiFinancialAnalysisServiceImpl implements AiFinancialAnalysisService {
    private final UserFinancialProfileRepository userFinancialProfileRepository;

    @Override
    public boolean assessFinancialStress(String userId) {
        UserFinancialProfile profile = getUserFinancialProfile(userId);

        // Multiple stress indicators
        return calculateFinancialStressScore(profile) > 0.7;
    }

    @Override
    public double predictPaymentDifficulty(String userId) {
        UserFinancialProfile profile = getUserFinancialProfile(userId);

        // Complex calculation considering multiple factors
        double debtToIncomeImpact = profile.getDebtToIncomeRatio().doubleValue() / 100.0;
        double creditScoreImpact = (850 - profile.getCreditScore()) / 850.0;

        return Math.min(1.0, debtToIncomeImpact * 0.6 + creditScoreImpact * 0.4);
    }

    @Override
    public List<MortgageAdjustmentRequestDto.FinancialPressureType> identifyFinancialPressureTypes(String userId) {
        UserFinancialProfile profile = getUserFinancialProfile(userId);
        List<MortgageAdjustmentRequestDto.FinancialPressureType> pressureTypes = new ArrayList<>();

        // Logic to identify potential financial pressures
        if (profile.getDebtToIncomeRatio().compareTo(BigDecimal.valueOf(50)) > 0) {
            pressureTypes.add(MortgageAdjustmentRequestDto.FinancialPressureType.CAREER_TRANSITION);
        }

        // Add more detection logic for different pressure types
        return pressureTypes;
    }

    @Override
    public BigDecimal calculateRecommendedReductionAmount(String userId) {
        UserFinancialProfile profile = getUserFinancialProfile(userId);

        // Calculate recommended reduction based on financial health
        double paymentDifficulty = predictPaymentDifficulty(userId);
        BigDecimal annualIncome = profile.getTotalAnnualIncome();

        return annualIncome
                .multiply(BigDecimal.valueOf(paymentDifficulty * 0.1))
                .divide(BigDecimal.valueOf(12), 2, RoundingMode.HALF_UP);
    }

    @Override
    public Map<String, Object> generateFinancialHealthInsights(String userId) {
        UserFinancialProfile profile = getUserFinancialProfile(userId);
        Map<String, Object> insights = new HashMap<>();

        insights.put("debtToIncomeRatio", profile.getDebtToIncomeRatio());
        insights.put("creditScore", profile.getCreditScore());
        insights.put("financialStabilityScore", profile.getFinancialStabilityScore());
        insights.put("paymentDifficulty", predictPaymentDifficulty(userId));

        return insights;
    }

    @Override
    public double evaluateFlexibilityEligibility(UserFinancialProfile profile) {
        // Comprehensive eligibility calculation
        double creditScoreComponent = (profile.getCreditScore() - 300) / 550.0;
        double debtToIncomeComponent = 1 - Math.min(1, profile.getDebtToIncomeRatio().doubleValue() / 50);
        double stabilityScoreComponent = profile.getFinancialStabilityScore() / 100.0;

        return (creditScoreComponent * 0.4 +
                debtToIncomeComponent * 0.3 +
                stabilityScoreComponent * 0.3);
    }

    @Override
    public Map<String, Object> predictLongTermFinancialImpact(String userId, BigDecimal reductionAmount) {
        UserFinancialProfile profile = getUserFinancialProfile(userId);
        Map<String, Object> impact = new HashMap<>();

        double paymentDifficulty = predictPaymentDifficulty(userId);

        impact.put("additionalInterestProjection", calculateAdditionalInterest(reductionAmount));
        impact.put("extendedLoanTermMonths", calculateExtendedLoanTerm(reductionAmount));
        impact.put("riskMitigationScore", paymentDifficulty);

        return impact;
    }

    // Helper method to retrieve financial profile
    private UserFinancialProfile getUserFinancialProfile(String userId) {
        return userFinancialProfileRepository.findByUserId(userId)
                .orElseThrow(() -> new RuntimeException("User financial profile not found"));
    }

    // Private helper methods for calculations
    private double calculateFinancialStressScore(UserFinancialProfile profile) {
        double debtStress = profile.getDebtToIncomeRatio().doubleValue() / 100.0;
        double creditRisk = (850 - profile.getCreditScore()) / 850.0;

        return Math.min(1.0, debtStress * 0.6 + creditRisk * 0.4);
    }

    private BigDecimal calculateAdditionalInterest(BigDecimal reductionAmount) {
        // Simplified additional interest calculation
        return reductionAmount.multiply(BigDecimal.valueOf(0.05));
    }

    private int calculateExtendedLoanTerm(BigDecimal reductionAmount) {
        // Simplified loan term extension calculation
        return reductionAmount.divide(BigDecimal.valueOf(1000), 0, RoundingMode.UP).intValue();
    }
}