package com.poalimflex.controller;

import com.poalimflex.dto.MortgageDetailsDto;
import com.poalimflex.dto.MortgagePaymentHistoryDto;
import com.poalimflex.dto.MortgageStatisticsDto;
import com.poalimflex.dto.MortgageSummaryDto;
import com.poalimflex.entity.Mortgage;
import com.poalimflex.repository.MortgageRepository;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

@RestController
@RequestMapping("/api/mortgage")
@RequiredArgsConstructor
@Slf4j
@Tag(name = "Mortgage Management", description = "APIs for mortgage information and management")
public class MortgageController {

    private final MortgageRepository mortgageRepository;

    @GetMapping("/user/{userId}")
    @Operation(summary = "Get User's Mortgages",
            description = "Retrieves all mortgages associated with a specific user")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully retrieved mortgages"),
            @ApiResponse(responseCode = "404", description = "User has no mortgages")
    })
    public ResponseEntity<List<MortgageSummaryDto>> getUserMortgages(
            @PathVariable String userId
    ) {
        List<Mortgage> mortgages = mortgageRepository.findByUserId(userId);

        if (mortgages.isEmpty()) {
            return ResponseEntity.notFound().build();
        }

        List<MortgageSummaryDto> mortgageSummaries = mortgages.stream()
                .map(this::convertToSummaryDto)
                .collect(Collectors.toList());

        return ResponseEntity.ok(mortgageSummaries);
    }

    @GetMapping("/{accountNumber}")
    @Operation(summary = "Get Mortgage Details",
            description = "Retrieves detailed information about a specific mortgage")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully retrieved mortgage details"),
            @ApiResponse(responseCode = "404", description = "Mortgage not found")
    })
    public ResponseEntity<MortgageDetailsDto> getMortgageDetails(
            @PathVariable String accountNumber
    ) {
        return mortgageRepository.findByAccountNumber(accountNumber)
                .map(this::convertToDetailsDto)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/{accountNumber}/payment-schedule")
    @Operation(summary = "Get Mortgage Payment Schedule",
            description = "Retrieves the payment schedule for a specific mortgage")
    public ResponseEntity<List<MortgagePaymentHistoryDto>> getMortgagePaymentSchedule(
            @PathVariable String accountNumber,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fromDate,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate toDate
    ) {
        return mortgageRepository.findByAccountNumber(accountNumber)
                .map(mortgage -> generatePaymentSchedule(mortgage, fromDate, toDate))
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/statistics/user/{userId}")
    @Operation(summary = "Get Mortgage Statistics",
            description = "Retrieves mortgage statistics for a specific user")
    public ResponseEntity<MortgageStatisticsDto> getMortgageStatistics(
            @PathVariable String userId
    ) {
        List<Mortgage> mortgages = mortgageRepository.findByUserId(userId);

        if (mortgages.isEmpty()) {
            return ResponseEntity.notFound().build();
        }

        MortgageStatisticsDto statistics = calculateMortgageStatistics(mortgages);
        return ResponseEntity.ok(statistics);
    }

    // Helper methods
    private MortgageSummaryDto convertToSummaryDto(Mortgage mortgage) {
        return MortgageSummaryDto.builder()
                .accountNumber(mortgage.getAccountNumber())
                .originalLoanAmount(mortgage.getOriginalLoanAmount())
                .currentBalance(mortgage.getCurrentBalance())
                .monthlyPayment(mortgage.getMonthlyPayment())
                .interestRate(mortgage.getInterestRate())
                .remainingTermMonths(mortgage.getRemainingTermMonths())
                .nextPaymentDate(mortgage.getNextPaymentDate())
                .isActive(mortgage.getIsActive())
                .build();
    }

    private MortgageDetailsDto convertToDetailsDto(Mortgage mortgage) {
        return MortgageDetailsDto.builder()
                .accountNumber(mortgage.getAccountNumber())
                .userId(mortgage.getUserId())
                .originalLoanAmount(mortgage.getOriginalLoanAmount())
                .currentBalance(mortgage.getCurrentBalance())
                .interestRate(mortgage.getInterestRate())
                .loanStartDate(mortgage.getLoanStartDate())
                .originalLoanTermMonths(mortgage.getOriginalLoanTermMonths())
                .remainingTermMonths(mortgage.getRemainingTermMonths())
                .monthlyPayment(mortgage.getMonthlyPayment())
                .mortgageType(convertMortgageType(mortgage.getMortgageType()))
                .nextPaymentDate(mortgage.getNextPaymentDate())
                .isActive(mortgage.getIsActive())
                .build();
    }

    private MortgageDetailsDto.MortgageType convertMortgageType(Mortgage.MortgageType type) {
        return switch (type) {
            case FIXED_RATE -> MortgageDetailsDto.MortgageType.FIXED_RATE;
            case VARIABLE_RATE -> MortgageDetailsDto.MortgageType.VARIABLE_RATE;
            case HYBRID -> MortgageDetailsDto.MortgageType.HYBRID;
            case GOVERNMENT_BACKED -> MortgageDetailsDto.MortgageType.GOVERNMENT_BACKED;
        };
    }

    private List<MortgagePaymentHistoryDto> generatePaymentSchedule(
            Mortgage mortgage, LocalDate fromDate, LocalDate toDate) {

        LocalDate startDate = fromDate != null ? fromDate : mortgage.getNextPaymentDate();
        LocalDate endDate = toDate != null ? toDate : startDate.plusMonths(12);

        long monthsBetween = ChronoUnit.MONTHS.between(startDate, endDate);
        int monthsToShow = (int) Math.min(monthsBetween + 1, 24); // Limit to 24 months

        return IntStream.range(0, monthsToShow)
                .mapToObj(i -> {
                    LocalDate paymentDate = startDate.plusMonths(i);
                    return MortgagePaymentHistoryDto.builder()
                            .paymentDate(paymentDate)
                            .paymentAmount(mortgage.getMonthlyPayment())
                            .status(paymentDate.isBefore(LocalDate.now()) ? "PAID" : "SCHEDULED")
                            .build();
                })
                .collect(Collectors.toList());
    }

    private MortgageStatisticsDto calculateMortgageStatistics(List<Mortgage> mortgages) {
        // Calculate total original loan amount
        var totalOriginalLoan = mortgages.stream()
                .map(Mortgage::getOriginalLoanAmount)
                .reduce((a, b) -> a.add(b))
                .orElse(java.math.BigDecimal.ZERO);

        // Calculate total current balance
        var totalCurrentBalance = mortgages.stream()
                .map(Mortgage::getCurrentBalance)
                .reduce((a, b) -> a.add(b))
                .orElse(java.math.BigDecimal.ZERO);

        // Calculate average interest rate (weighted by current balance)
        var weightedInterestSum = mortgages.stream()
                .map(m -> m.getInterestRate().multiply(m.getCurrentBalance()))
                .reduce((a, b) -> a.add(b))
                .orElse(java.math.BigDecimal.ZERO);

        var averageInterestRate = totalCurrentBalance.signum() == 0 ?
                java.math.BigDecimal.ZERO :
                weightedInterestSum.divide(totalCurrentBalance, 2, java.math.RoundingMode.HALF_UP);

        // Calculate total monthly payment
        var totalMonthlyPayment = mortgages.stream()
                .map(Mortgage::getMonthlyPayment)
                .reduce((a, b) -> a.add(b))
                .orElse(java.math.BigDecimal.ZERO);

        // Calculate repayment progress
        var repaymentProgress = totalOriginalLoan.signum() == 0 ?
                java.math.BigDecimal.ZERO :
                java.math.BigDecimal.ONE.subtract(
                        totalCurrentBalance.divide(totalOriginalLoan, 4, java.math.RoundingMode.HALF_UP)
                ).multiply(java.math.BigDecimal.valueOf(100)).setScale(2, java.math.RoundingMode.HALF_UP);

        return MortgageStatisticsDto.builder()
                .totalOriginalLoanAmount(totalOriginalLoan)
                .totalCurrentBalance(totalCurrentBalance)
                .averageInterestRate(averageInterestRate)
                .totalMonthlyPayment(totalMonthlyPayment)
                .repaymentProgressPercentage(repaymentProgress)
                .totalActiveMortgages((int) mortgages.stream().filter(Mortgage::getIsActive).count())
                .build();
    }
}