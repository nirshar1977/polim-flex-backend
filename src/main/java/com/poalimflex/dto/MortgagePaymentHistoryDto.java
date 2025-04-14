package com.poalimflex.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;

/**
 * DTO for mortgage payment history
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Mortgage payment history entry")
public class MortgagePaymentHistoryDto {
    @Schema(description = "Payment date")
    private LocalDate paymentDate;

    @Schema(description = "Payment amount", example = "2500.00")
    private BigDecimal paymentAmount;

    @Schema(description = "Payment status", example = "PAID")
    private String status;
}
