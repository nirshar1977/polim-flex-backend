package com.poalimflex.controller;

import com.poalimflex.dto.MortgageAdjustmentRequestDto;
import com.poalimflex.dto.MortgageAdjustmentResponseDto;
import com.poalimflex.service.MortgageAdjustmentService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/mortgage/adjustment")
@RequiredArgsConstructor
@Tag(name = "Mortgage Adjustment", description = "Flexible Mortgage Repayment Management")
public class MortgageAdjustmentController {

    private final MortgageAdjustmentService mortgageAdjustmentService;

    @PostMapping("/request")
    @Operation(summary = "Request Mortgage Payment Adjustment",
            description = "Allows customers to temporarily reduce mortgage payments")
    public ResponseEntity<MortgageAdjustmentResponseDto> requestAdjustment(
            @Valid @RequestBody MortgageAdjustmentRequestDto request
    ) {
        MortgageAdjustmentResponseDto response = mortgageAdjustmentService.processMortgageAdjustment(request);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/eligibility/{userId}")
    @Operation(summary = "Check Mortgage Adjustment Eligibility",
            description = "Determines if a user is eligible for mortgage payment flexibility")
    public ResponseEntity<Boolean> checkEligibility(@PathVariable String userId) {
        boolean isEligible = mortgageAdjustmentService.checkEligibility(userId);
        return ResponseEntity.ok(isEligible);
    }
}