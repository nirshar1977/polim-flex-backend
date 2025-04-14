package com.poalimflex.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

/**
 * DTO for risk band in financial risk report
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Risk band information")
public class RiskBandDto {
    @Schema(description = "Risk band name", example = "Moderate Risk (0.3-0.6)")
    private String bandName;

    @Schema(description = "Number of users in band", example = "380")
    private Integer userCount;

    @Schema(description = "Total outstanding balance in band", example = "3750000.00")
    private BigDecimal outstandingBalance;
}
