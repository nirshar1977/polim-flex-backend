package com.poalimflex.dto.notification;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO for notification preferences
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "User notification preferences")
public class NotificationPreferenceDto {
    @Schema(description = "User ID", example = "USER12345")
    @NotBlank(message = "User ID is required")
    private String userId;

    @Schema(description = "Email notifications enabled", example = "true")
    private Boolean emailNotificationsEnabled;

    @Schema(description = "SMS notifications enabled", example = "true")
    private Boolean smsNotificationsEnabled;

    @Schema(description = "Push notifications enabled", example = "false")
    private Boolean pushNotificationsEnabled;

    @Schema(description = "Payment reminders enabled", example = "true")
    private Boolean paymentRemindersEnabled;

    @Schema(description = "Adjustment updates enabled", example = "true")
    private Boolean adjustmentUpdatesEnabled;

    @Schema(description = "Financial insights enabled", example = "true")
    private Boolean financialInsightsEnabled;

    @Schema(description = "Days before payment for reminder", example = "5")
    @Min(value = 1, message = "Reminder days must be at least 1")
    @Max(value = 30, message = "Reminder days cannot exceed 30")
    private Integer reminderDaysBeforePayment;
}
