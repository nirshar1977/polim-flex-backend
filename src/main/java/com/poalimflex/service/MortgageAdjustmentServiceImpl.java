package com.poalimflex.service;

import com.poalimflex.dto.mortage.adjustment.*;
import com.poalimflex.dto.mortage.adjustment.MortgageAdjustmentResponseDto.AdjustmentStatus;
import com.poalimflex.dto.mortage.adjustment.MortgageAdjustmentResponseDto.MonthlyProjection;
import com.poalimflex.entity.Mortgage;
import com.poalimflex.entity.MortgageAdjustment;
import com.poalimflex.repository.MortgageAdjustmentRepository;
import com.poalimflex.repository.MortgageRepository;
import com.poalimflex.repository.UserFinancialProfileRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
@Slf4j
@RequiredArgsConstructor
public class MortgageAdjustmentServiceImpl implements MortgageAdjustmentService {
    private final MortgageRepository mortgageRepository;
    private final MortgageAdjustmentRepository mortgageAdjustmentRepository;
    private final UserFinancialProfileRepository userFinancialProfileRepository;
    private final AiFinancialAnalysisService aiFinancialAnalysisService;

    @Override
    @Transactional
    public MortgageAdjustmentResponseDto processMortgageAdjustment(MortgageAdjustmentRequestDto request) {
        // Log the incoming request
        log.info("Processing mortgage adjustment request for user: {}", request.getUserId());

        // Check eligibility
        if (!checkEligibility(request.getUserId())) {
            return MortgageAdjustmentResponseDto.builder()
                    .status(AdjustmentStatus.REJECTED)
                    .statusDescription("User not eligible for mortgage adjustment")
                    .build();
        }

        // Find the mortgage
        Mortgage mortgage = mortgageRepository.findByAccountNumber(request.getMortgageAccountNumber())
                .orElseThrow(() -> new RuntimeException("Mortgage not found"));

        // Perform AI-powered financial stress analysis
        boolean isHighRisk = aiFinancialAnalysisService.assessFinancialStress(request.getUserId());

        // Validate and process the adjustment
        BigDecimal approvedReduction = validateAndCalculateReduction(request, isHighRisk);

        // Calculate additional interest
        BigDecimal additionalInterest = calculateAdditionalInterestAmount(request, approvedReduction);

        // Generate unique adjustment ID
        String adjustmentId = generateAdjustmentId();

        // Calculate adjusted monthly payment
        BigDecimal originalMonthlyPayment = mortgage.getMonthlyPayment();
        BigDecimal adjustedMonthlyPayment = originalMonthlyPayment.subtract(approvedReduction);

        // Calculate projected additional cost
        BigDecimal projectedAdditionalCost = additionalInterest.multiply(
                BigDecimal.valueOf(mortgage.getRemainingTermMonths()));

        // Determine risk assessment score
        double riskAssessmentScore = aiFinancialAnalysisService.predictPaymentDifficulty(request.getUserId());

        // Save the adjustment entity
        MortgageAdjustment adjustment = MortgageAdjustment.builder()
                .id(adjustmentId)
                .mortgageId(mortgage.getId())
                .adjustmentDate(LocalDateTime.now())
                .originalMonthlyPayment(originalMonthlyPayment)
                .reducedPayment(adjustedMonthlyPayment)
                .additionalInterest(additionalInterest)
                .status(determineEntityAdjustmentStatus(approvedReduction, request))
                .adjustmentMonth(request.getAdjustmentMonth())
                .repaymentStartDate(request.getAdjustmentMonth().plusMonths(1))
                .reason(request.getAdjustmentReason())
                .financialPressureType(convertFinancialPressureType(request.getFinancialPressureType()))
                .riskAssessmentScore(riskAssessmentScore)
                .build();

        mortgageAdjustmentRepository.save(adjustment);

        // Build recommended actions
        List<String> recommendedActions = generateRecommendedActions(request, riskAssessmentScore);

        // Calculate repayment breakdown
        MortgageAdjustmentResponseDto.RepaymentBreakdown repaymentBreakdown = calculateRepaymentBreakdown(
                mortgage, approvedReduction, additionalInterest);

        // Build and return response
        return MortgageAdjustmentResponseDto.builder()
                .adjustmentId(adjustmentId)
                .userId(request.getUserId())
                .mortgageAccountNumber(request.getMortgageAccountNumber())
                .originalMonthlyPayment(originalMonthlyPayment)
                .approvedReductionAmount(approvedReduction)
                .adjustedMonthlyPayment(adjustedMonthlyPayment)
                .additionalInterest(additionalInterest)
                .status(determineAdjustmentStatus(approvedReduction, request))
                .statusDescription(generateStatusDescription(approvedReduction, isHighRisk))
                .requestTimestamp(LocalDateTime.now())
                .adjustmentMonth(request.getAdjustmentMonth())
                .repaymentScheduleStart(request.getAdjustmentMonth().plusMonths(1))
                .projectedAdditionalCost(projectedAdditionalCost)
                .riskAssessmentScore(riskAssessmentScore)
                .recommendedActions(recommendedActions)
                .repaymentBreakdown(repaymentBreakdown)
                .build();
    }

    @Override
    public boolean checkEligibility(String userId) {
        // Check if the user has an active mortgage
        long activeMortgageCount = mortgageRepository.countByUserIdAndIsActiveTrue(userId);
        if (activeMortgageCount == 0) {
            return false;
        }

        // Check if the user is eligible based on mortgage criteria
        boolean isEligible = mortgageRepository.isUserEligibleForAdjustment(userId);

        // Get all mortgages for the user
        List<Mortgage> mortgages = mortgageRepository.findByUserId(userId);

        // Check previous adjustments (max 4 per year)
        boolean tooManyAdjustments = false;
        for (Mortgage mortgage : mortgages) {
            long adjustmentsInLastYear = mortgageAdjustmentRepository.countAdjustmentsInLastYear(
                    mortgage.getId(), LocalDateTime.now().minusYears(1));
            if (adjustmentsInLastYear >= 4) {
                tooManyAdjustments = true;
                break;
            }
        }

        return isEligible && !tooManyAdjustments;
    }

    @Override
    public List<MortgageAdjustmentResponseDto> getAdjustmentHistory(String userId, LocalDate fromDate, LocalDate toDate) {
        log.info("Retrieving adjustment history for user: {}", userId);

        // Get all mortgages for the user
        List<Mortgage> userMortgages = mortgageRepository.findByUserId(userId);

        if (userMortgages.isEmpty()) {
            log.warn("No mortgages found for user: {}", userId);
            return Collections.emptyList();
        }

        // Get all mortgage IDs
        List<String> mortgageIds = userMortgages.stream()
                .map(Mortgage::getId)
                .collect(Collectors.toList());

        // Retrieve adjustments for these mortgages
        List<MortgageAdjustment> adjustments = new ArrayList<>();
        for (String mortgageId : mortgageIds) {
            adjustments.addAll(mortgageAdjustmentRepository.findByMortgageId(mortgageId));
        }

        // Apply date filtering if applicable
        if (fromDate != null) {
            LocalDateTime fromDateTime = fromDate.atStartOfDay();
            adjustments = adjustments.stream()
                    .filter(adj -> adj.getAdjustmentDate().isAfter(fromDateTime) ||
                            adj.getAdjustmentDate().isEqual(fromDateTime))
                    .collect(Collectors.toList());
        }

        if (toDate != null) {
            LocalDateTime toDateTime = toDate.plusDays(1).atStartOfDay();
            adjustments = adjustments.stream()
                    .filter(adj -> adj.getAdjustmentDate().isBefore(toDateTime))
                    .collect(Collectors.toList());
        }

        // Sort by date (newest first)
        adjustments.sort(Comparator.comparing(MortgageAdjustment::getAdjustmentDate).reversed());

        // Convert to DTOs
        return adjustments.stream()
                .map(this::convertToResponseDto)
                .collect(Collectors.toList());
    }

    @Override
    public MortgageAdjustmentRecommendationDto generateAdjustmentRecommendation(String userId) {
        log.info("Generating adjustment recommendations for user: {}", userId);

        // Find mortgage details
        List<Mortgage> userMortgages = mortgageRepository.findByUserId(userId);
        if (userMortgages.isEmpty()) {
            log.warn("No mortgages found for user: {}", userId);
            throw new RuntimeException("No mortgages found for user");
        }

        // Use the first active mortgage for recommendation
        Mortgage activeMortgage = userMortgages.stream()
                .filter(m -> m.getIsActive())
                .findFirst()
                .orElseThrow(() -> new RuntimeException("No active mortgages found"));

        // Use AI service to get recommendations
        BigDecimal recommendedAmount = aiFinancialAnalysisService.calculateRecommendedReductionAmount(userId);
        double paymentDifficulty = aiFinancialAnalysisService.predictPaymentDifficulty(userId);
        List<MortgageAdjustmentRequestDto.FinancialPressureType> pressureTypes =
                aiFinancialAnalysisService.identifyFinancialPressureTypes(userId);

        // Calculate recommended duration based on payment difficulty
        int recommendedDuration = calculateRecommendedDuration(paymentDifficulty);

        // Determine recommended repayment strategy
        MortgageAdjustmentRequestDto.RepaymentStrategy strategy = determineOptimalRepaymentStrategy(userId);

        // Project loan term impact
        int loanTermImpact = calculateLoanTermImpact(recommendedAmount, recommendedDuration);

        return MortgageAdjustmentRecommendationDto.builder()
                .userId(userId)
                .mortgageAccountNumber(activeMortgage.getAccountNumber())
                .recommendedReductionAmount(recommendedAmount)
                .recommendedDurationMonths(recommendedDuration)
                .detectedPressureTypes(pressureTypes)
                .financialRiskScore(paymentDifficulty)
                .recommendedRepaymentStrategy(strategy)
                .projectedLoanTermImpact(loanTermImpact)
                .build();
    }

    @Override
    public AdjustmentSimulationResultDto simulateAdjustment(AdjustmentSimulationRequestDto request) {
        log.info("Simulating adjustment for user: {}", request.getUserId());

        // Find the mortgage
        Mortgage mortgage = mortgageRepository.findByAccountNumber(request.getMortgageAccountNumber())
                .orElseThrow(() -> new RuntimeException("Mortgage not found"));

        // Eligibility check
        boolean isEligible = checkEligibility(request.getUserId());

        // Calculate adjusted payment
        BigDecimal currentMonthlyPayment = mortgage.getMonthlyPayment();
        BigDecimal adjustedPayment = currentMonthlyPayment.subtract(request.getProposedReductionAmount());

        // Calculate additional interest
        BigDecimal interestRate = mortgage.getInterestRate();
        BigDecimal deferredAmount = request.getProposedReductionAmount()
                .multiply(BigDecimal.valueOf(request.getDurationMonths()));
        BigDecimal additionalInterest = calculateAdditionalInterestForDeferral(
                deferredAmount, interestRate, mortgage.getRemainingTermMonths());

        // Calculate post-adjustment payment
        BigDecimal postAdjustmentPayment = calculatePostAdjustmentPayment(
                mortgage, request.getProposedReductionAmount(), request.getDurationMonths());

        // Risk assessment
        double riskScore = aiFinancialAnalysisService.predictPaymentDifficulty(request.getUserId());

        // Generate monthly projections
        List<MonthlyProjection> projections = generateMonthlyProjections(
                mortgage, request.getProposedStartDate(), request.getDurationMonths(),
                adjustedPayment, postAdjustmentPayment);

        // Loan term impact
        int loanTermImpact = calculateLoanTermImpact(
                request.getProposedReductionAmount(), request.getDurationMonths());

        // Improvement suggestions
        List<String> suggestions = generateImprovementSuggestions(request, riskScore);

        return AdjustmentSimulationResultDto.builder()
                .projectedAdjustedPayment(adjustedPayment)
                .totalAdditionalInterest(additionalInterest)
                .projectedLoanTermImpact(loanTermImpact)
                .postAdjustmentMonthlyPayment(postAdjustmentPayment)
                .monthlyProjections(projections)
                .riskAssessmentScore(riskScore)
                .eligibleForAdjustment(isEligible)
                .improvementSuggestions(suggestions)
                .build();
    }

    @Override
    @Transactional
    public void cancelAdjustment(String adjustmentId) {
        MortgageAdjustment adjustment = mortgageAdjustmentRepository.findById(adjustmentId)
                .orElseThrow(() -> new RuntimeException("Adjustment not found"));

        // Check if adjustment can be cancelled
        if (adjustment.getStatus() != MortgageAdjustment.AdjustmentStatus.PENDING_REVIEW) {
            throw new RuntimeException("Only pending adjustments can be cancelled");
        }

        // Delete or mark as cancelled
        mortgageAdjustmentRepository.delete(adjustment);

        log.info("Cancelled adjustment: {}", adjustmentId);
    }

    // Helper methods
    private BigDecimal validateAndCalculateReduction(
            MortgageAdjustmentRequestDto request,
            boolean isHighRisk
    ) {
        BigDecimal maxAllowedReduction = calculateMaxReduction(request.getUserId());

        // Adjust reduction based on risk assessment
        if (isHighRisk) {
            return request.getReductionAmount()
                    .min(maxAllowedReduction.multiply(BigDecimal.valueOf(0.5)));
        }

        return request.getReductionAmount()
                .min(maxAllowedReduction);
    }

    private BigDecimal calculateAdditionalInterestAmount(
            MortgageAdjustmentRequestDto request,
            BigDecimal approvedReduction
    ) {
        // Get mortgage details
        Mortgage mortgage = mortgageRepository.findByAccountNumber(request.getMortgageAccountNumber())
                .orElseThrow(() -> new RuntimeException("Mortgage not found"));

        // Simple interest calculation for demonstration
        BigDecimal monthlyInterestRate = mortgage.getInterestRate()
                .divide(BigDecimal.valueOf(100), 6, RoundingMode.HALF_UP)
                .divide(BigDecimal.valueOf(12), 6, RoundingMode.HALF_UP);

        return approvedReduction.multiply(monthlyInterestRate)
                .setScale(2, RoundingMode.HALF_UP);
    }

    private String generateAdjustmentId() {
        return "ADJ-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase();
    }

    private AdjustmentStatus determineAdjustmentStatus(
            BigDecimal approvedReduction,
            MortgageAdjustmentRequestDto request
    ) {
        if (approvedReduction.compareTo(request.getReductionAmount()) == 0) {
            return AdjustmentStatus.APPROVED;
        } else if (approvedReduction.signum() > 0) {
            return AdjustmentStatus.PARTIALLY_APPROVED;
        }

        return AdjustmentStatus.REJECTED;
    }

    private MortgageAdjustment.AdjustmentStatus determineEntityAdjustmentStatus(
            BigDecimal approvedReduction,
            MortgageAdjustmentRequestDto request
    ) {
        if (approvedReduction.compareTo(request.getReductionAmount()) == 0) {
            return MortgageAdjustment.AdjustmentStatus.APPROVED;
        } else if (approvedReduction.signum() > 0) {
            return MortgageAdjustment.AdjustmentStatus.PARTIALLY_APPROVED;
        }

        return MortgageAdjustment.AdjustmentStatus.REJECTED;
    }

    private String generateStatusDescription(
            BigDecimal approvedReduction,
            boolean isHighRisk
    ) {
        if (isHighRisk) {
            return "Reduced adjustment due to high financial risk";
        } else if (approvedReduction.signum() == 0) {
            return "Adjustment request rejected due to eligibility criteria";
        } else if (approvedReduction.compareTo(BigDecimal.ZERO) > 0) {
            return "Mortgage adjustment processed successfully";
        }

        return "Adjustment request could not be processed";
    }

    private BigDecimal calculateMaxReduction(String userId) {
        // Get mortgage details
        List<Mortgage> userMortgages = mortgageRepository.findByUserId(userId);
        if (userMortgages.isEmpty()) {
            return BigDecimal.ZERO;
        }

        // Use first active mortgage for calculation
        Mortgage activeMortgage = userMortgages.stream()
                .filter(m -> m.getIsActive())
                .findFirst()
                .orElseThrow(() -> new RuntimeException("No active mortgages found"));

        // Calculate maximum reduction (e.g., 30% of current payment)
        return activeMortgage.getMonthlyPayment().multiply(BigDecimal.valueOf(0.3))
                .setScale(2, RoundingMode.HALF_UP);
    }

    private MortgageAdjustmentResponseDto convertToResponseDto(MortgageAdjustment adjustment) {
        // Get the mortgage
        Mortgage mortgage = mortgageRepository.findById(adjustment.getMortgageId())
                .orElseThrow(() -> new RuntimeException("Mortgage not found"));

        // Convert status
        AdjustmentStatus status = switch (adjustment.getStatus()) {
            case APPROVED -> AdjustmentStatus.APPROVED;
            case PARTIALLY_APPROVED -> AdjustmentStatus.PARTIALLY_APPROVED;
            case REJECTED -> AdjustmentStatus.REJECTED;
            case PENDING_REVIEW -> AdjustmentStatus.PENDING_REVIEW;
        };

        // Calculate total additional cost
        BigDecimal projectedAdditionalCost = adjustment.getAdditionalInterest()
                .multiply(BigDecimal.valueOf(mortgage.getRemainingTermMonths()));

        return MortgageAdjustmentResponseDto.builder()
                .adjustmentId(adjustment.getId())
                .userId(mortgage.getUserId())
                .mortgageAccountNumber(mortgage.getAccountNumber())
                .originalMonthlyPayment(adjustment.getOriginalMonthlyPayment())
                .approvedReductionAmount(adjustment.getOriginalMonthlyPayment().subtract(adjustment.getReducedPayment()))
                .adjustedMonthlyPayment(adjustment.getReducedPayment())
                .additionalInterest(adjustment.getAdditionalInterest())
                .status(status)
                .requestTimestamp(adjustment.getAdjustmentDate())
                .adjustmentMonth(adjustment.getAdjustmentMonth())
                .repaymentScheduleStart(adjustment.getRepaymentStartDate())
                .projectedAdditionalCost(projectedAdditionalCost)
                .riskAssessmentScore(adjustment.getRiskAssessmentScore())
                .build();
    }

    private MortgageAdjustment.FinancialPressureType convertFinancialPressureType(
            MortgageAdjustmentRequestDto.FinancialPressureType type) {
        if (type == null) {
            return null;
        }

        return switch (type) {
            case EDUCATION -> MortgageAdjustment.FinancialPressureType.EDUCATION;
            case MEDICAL_EXPENSES -> MortgageAdjustment.FinancialPressureType.MEDICAL_EXPENSES;
            case HOME_REPAIRS -> MortgageAdjustment.FinancialPressureType.HOME_REPAIRS;
            case FAMILY_EMERGENCY -> MortgageAdjustment.FinancialPressureType.FAMILY_EMERGENCY;
            case CAREER_TRANSITION -> MortgageAdjustment.FinancialPressureType.CAREER_TRANSITION;
            case OTHER -> MortgageAdjustment.FinancialPressureType.OTHER;
        };
    }

    private int calculateRecommendedDuration(double paymentDifficulty) {
        // Higher difficulty suggests longer duration
        if (paymentDifficulty > 0.8) {
            return 6; // Maximum 6 months
        } else if (paymentDifficulty > 0.5) {
            return 4;
        } else if (paymentDifficulty > 0.3) {
            return 3;
        } else {
            return 2;
        }
    }

    private MortgageAdjustmentRequestDto.RepaymentStrategy determineOptimalRepaymentStrategy(String userId) {
        // Get financial profile to determine optimal strategy
        double stabilityScore = userFinancialProfileRepository.findByUserId(userId)
                .map(profile -> profile.getFinancialStabilityScore())
                .orElse(0.5);

        // Higher stability suggests more aggressive repayment
        if (stabilityScore > 0.7) {
            return MortgageAdjustmentRequestDto.RepaymentStrategy.FRONT_LOADED;
        } else if (stabilityScore > 0.4) {
            return MortgageAdjustmentRequestDto.RepaymentStrategy.SPREAD_EVENLY;
        } else {
            return MortgageAdjustmentRequestDto.RepaymentStrategy.BACK_LOADED;
        }
    }

    private int calculateLoanTermImpact(BigDecimal reductionAmount, int durationMonths) {
        // Simple estimate - more sophisticated calculation would consider
        // current loan balance, interest rate, etc.
        BigDecimal totalReduction = reductionAmount.multiply(BigDecimal.valueOf(durationMonths));
        return totalReduction.divide(BigDecimal.valueOf(1000), 0, RoundingMode.UP).intValue();
    }

    private BigDecimal calculateAdditionalInterestForDeferral(
            BigDecimal deferredAmount, BigDecimal interestRate, int remainingTermMonths) {
        // Simple calculation for demonstration
        BigDecimal annualRate = interestRate.divide(BigDecimal.valueOf(100), 6, RoundingMode.HALF_UP);
        BigDecimal monthlyRate = annualRate.divide(BigDecimal.valueOf(12), 6, RoundingMode.HALF_UP);

        return deferredAmount.multiply(monthlyRate)
                .multiply(BigDecimal.valueOf(remainingTermMonths))
                .divide(BigDecimal.valueOf(2), 2, RoundingMode.HALF_UP);
    }

    private BigDecimal calculatePostAdjustmentPayment(
            Mortgage mortgage, BigDecimal reductionAmount, int durationMonths) {
        // Calculate total deferred amount
        BigDecimal totalDeferred = reductionAmount.multiply(BigDecimal.valueOf(durationMonths));

        // Calculate additional monthly payment needed to recover deferred amount
        // (simplified - spreading over 12 months after adjustment period)
        BigDecimal additionalMonthly = totalDeferred.divide(BigDecimal.valueOf(12), 2, RoundingMode.HALF_UP);

        // Post-adjustment payment is original payment plus additional amount
        return mortgage.getMonthlyPayment().add(additionalMonthly);
    }

    private List<MonthlyProjection> generateMonthlyProjections(
            Mortgage mortgage, LocalDate startDate, int durationMonths,
            BigDecimal adjustedPayment, BigDecimal postAdjustmentPayment) {

        List<MonthlyProjection> projections = new ArrayList<>();
        BigDecimal interestRate = mortgage.getInterestRate()
                .divide(BigDecimal.valueOf(100), 6, RoundingMode.HALF_UP)
                .divide(BigDecimal.valueOf(12), 6, RoundingMode.HALF_UP);

        // Current mortgage balance
        BigDecimal currentBalance = mortgage.getCurrentBalance();

        // Generate projections for reduced payment period
        for (int i = 0; i < durationMonths; i++) {
            LocalDate month = startDate.plusMonths(i);
            BigDecimal interestPortion = currentBalance.multiply(interestRate)
                    .setScale(2, RoundingMode.HALF_UP);
            BigDecimal principalPortion = adjustedPayment.subtract(interestPortion);

            // Ensure principal portion is not negative
            if (principalPortion.signum() < 0) {
                principalPortion = BigDecimal.ZERO;
            }

            projections.add(MonthlyProjection.builder()
                    .month(month)
                    .projectedPayment(adjustedPayment)
                    .principalPortion(principalPortion)
                    .interestPortion(interestPortion)
                    .build());

            // Update balance for next iteration
            currentBalance = currentBalance.subtract(principalPortion);
        }

        // Generate projections for post-adjustment period (3 months)
        for (int i = 0; i < 3; i++) {
            LocalDate month = startDate.plusMonths(durationMonths + i);
            BigDecimal interestPortion = currentBalance.multiply(interestRate)
                    .setScale(2, RoundingMode.HALF_UP);
            BigDecimal principalPortion = postAdjustmentPayment.subtract(interestPortion);

            projections.add(MonthlyProjection.builder()
                    .month(month)
                    .projectedPayment(postAdjustmentPayment)
                    .principalPortion(principalPortion)
                    .interestPortion(interestPortion)
                    .build());

            // Update balance for next iteration
            currentBalance = currentBalance.subtract(principalPortion);
        }

        return projections;
    }

    private List<String> generateImprovementSuggestions(AdjustmentSimulationRequestDto request, double riskScore) {
        List<String> suggestions = new ArrayList<>();

        if (riskScore > 0.7) {
            suggestions.add("Consider a shorter adjustment period to reduce long-term interest costs");
            suggestions.add("Explore refinancing options for a more sustainable solution");
        }

        if (request.getDurationMonths() > 4) {
            suggestions.add("Reducing the adjustment duration from " + request.getDurationMonths() +
                    " to 3 months would lower your additional interest costs");
        }

        if (request.getProposedReductionAmount().compareTo(BigDecimal.valueOf(2000)) > 0) {
            suggestions.add("Consider a smaller reduction amount to minimize impact on loan term");
        }

        return suggestions;
    }

    private List<String> generateRecommendedActions(MortgageAdjustmentRequestDto request, double riskScore) {
        List<String> actions = new ArrayList<>();

        // Based on risk score
        if (riskScore > 0.7) {
            actions.add("Schedule a financial consultation for comprehensive review");
            actions.add("Consider debt consolidation to improve financial stability");
        } else if (riskScore > 0.5) {
            actions.add("Review monthly budget to identify potential savings");
            actions.add("Set up emergency fund to prepare for future financial pressures");
        } else {
            actions.add("Continue monitoring your financial health");
        }

        // Based on financial pressure type
        if (request.getFinancialPressureType() == MortgageAdjustmentRequestDto.FinancialPressureType.MEDICAL_EXPENSES) {
            actions.add("Review health insurance coverage for potential improvements");
        } else if (request.getFinancialPressureType() == MortgageAdjustmentRequestDto.FinancialPressureType.EDUCATION) {
            actions.add("Explore education financing alternatives");
        } else if (request.getFinancialPressureType() == MortgageAdjustmentRequestDto.FinancialPressureType.CAREER_TRANSITION) {
            actions.add("Consider career counseling services");
        }

        return actions;
    }

    private MortgageAdjustmentResponseDto.RepaymentBreakdown calculateRepaymentBreakdown(
            Mortgage mortgage, BigDecimal approvedReduction, BigDecimal additionalInterest) {

        // Calculate remaining principal
        BigDecimal remainingPrincipal = mortgage.getCurrentBalance();

        // Calculate total interest impact
        BigDecimal totalInterestImpact = additionalInterest.multiply(
                BigDecimal.valueOf(mortgage.getRemainingTermMonths()));

        // Estimate additional months
        int additionalMonths = approvedReduction.divide(
                mortgage.getMonthlyPayment(), 0, RoundingMode.UP).intValue();

        // Calculate projected loan completion date
        LocalDate projectedCompletionDate = LocalDate.now().plusMonths(
                mortgage.getRemainingTermMonths() + additionalMonths);

        // Generate monthly projections (simplified for demo)
        List<MonthlyProjection> monthlyProjections = new ArrayList<>();
        LocalDate currentMonth = LocalDate.now();
        BigDecimal interestRate = mortgage.getInterestRate()
                .divide(BigDecimal.valueOf(100), 6, RoundingMode.HALF_UP)
                .divide(BigDecimal.valueOf(12), 6, RoundingMode.HALF_UP);

        // Add a few sample projections
        for (int i = 0; i < 3; i++) {
            LocalDate month = currentMonth.plusMonths(i);
            BigDecimal monthlyInterest = remainingPrincipal.multiply(interestRate);
            BigDecimal principalPortion = mortgage.getMonthlyPayment().subtract(monthlyInterest);

            monthlyProjections.add(MonthlyProjection.builder()
                    .month(month)
                    .projectedPayment(mortgage.getMonthlyPayment())
                    .principalPortion(principalPortion)
                    .interestPortion(monthlyInterest)
                    .build());

            // Update remaining principal for next month
            remainingPrincipal = remainingPrincipal.subtract(principalPortion);
        }

        return MortgageAdjustmentResponseDto.RepaymentBreakdown.builder()
                .remainingPrincipal(mortgage.getCurrentBalance())
                .totalInterestImpact(totalInterestImpact)
                .projectedLoanCompletionDate(projectedCompletionDate)
                .additionalMonths(additionalMonths)
                .monthlyProjections(monthlyProjections)
                .build();
    }
}