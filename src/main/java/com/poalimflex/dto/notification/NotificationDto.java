package com.poalimflex.dto.notification;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * DTO for user notifications
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "User notification information")
public class NotificationDto {
    @Schema(description = "Notification ID", example = "5f0e8c87-7b5a-4a69-b96d-f0f1d47fe4ba")
    private String id;

    @Schema(description = "User ID", example = "USER12345")
    @NotBlank(message = "User ID is required")
    private String userId;

    @Schema(description = "Notification type")
    @NotNull(message = "Notification type is required")
    private NotificationType type;

    @Schema(description = "Notification title", example = "Mortgage Adjustment Approved")
    @NotBlank(message = "Title is required")
    @Size(max = 100, message = "Title cannot exceed 100 characters")
    private String title;

    @Schema(description = "Notification message", example = "Your request for mortgage payment adjustment has been approved.")
    @NotBlank(message = "Message is required")
    @Size(max = 500, message = "Message cannot exceed 500 characters")
    private String message;

    @Schema(description = "Notification timestamp")
    private LocalDateTime timestamp;

    @Schema(description = "Whether the notification has been read", example = "false")
    private Boolean isRead;

    @Schema(description = "Action URL (optional)", example = "/mortgage/adjustment/ADJ-A1B2C3D4")
    private String actionUrl;

    /**
     * Enum for notification types
     */
    public enum NotificationType {
        MORTGAGE_ADJUSTMENT_APPROVED,
        MORTGAGE_ADJUSTMENT_REJECTED,
        MORTGAGE_ADJUSTMENT_PENDING,
        PAYMENT_REMINDER,
        PAYMENT_CONFIRMATION,
        FINANCIAL_INSIGHT,
        SECURITY_ALERT,
        ACCOUNT_UPDATE,
        SYSTEM_NOTIFICATION
    }
}
