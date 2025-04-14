package com.poalimflex.controller;

import com.poalimflex.dto.notification.NotificationDto;
import com.poalimflex.dto.notification.NotificationPreferenceDto;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * Controller for handling user notifications
 */
@RestController
@RequestMapping("/api/notifications")
@RequiredArgsConstructor
@Slf4j
@Tag(name = "Notifications", description = "APIs for user notification management")
public class NotificationController {

    @GetMapping("/user/{userId}")
    @Operation(summary = "Get User Notifications",
            description = "Retrieves all notifications for a specific user")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully retrieved notifications")
    })
    public ResponseEntity<List<NotificationDto>> getUserNotifications(@PathVariable String userId) {
        log.info("Retrieving notifications for user: {}", userId);

        // Mock implementation - in a real application, you'd fetch from a repository
        List<NotificationDto> notifications = new ArrayList<>();

        // Add some sample notifications
        notifications.add(NotificationDto.builder()
                .id(UUID.randomUUID().toString())
                .userId(userId)
                .type(NotificationDto.NotificationType.MORTGAGE_ADJUSTMENT_APPROVED)
                .title("Mortgage Adjustment Approved")
                .message("Your request for mortgage payment adjustment has been approved.")
                .timestamp(LocalDateTime.now().minusDays(2))
                .isRead(true)
                .build());

        notifications.add(NotificationDto.builder()
                .id(UUID.randomUUID().toString())
                .userId(userId)
                .type(NotificationDto.NotificationType.PAYMENT_REMINDER)
                .title("Upcoming Mortgage Payment")
                .message("Your mortgage payment is due in 5 days.")
                .timestamp(LocalDateTime.now().minusHours(8))
                .isRead(false)
                .build());

        notifications.add(NotificationDto.builder()
                .id(UUID.randomUUID().toString())
                .userId(userId)
                .type(NotificationDto.NotificationType.FINANCIAL_INSIGHT)
                .title("Financial Health Update")
                .message("Your financial health score has improved by 10 points.")
                .timestamp(LocalDateTime.now().minusDays(7))
                .isRead(false)
                .build());

        return ResponseEntity.ok(notifications);
    }

    @PostMapping("/mark-read/{notificationId}")
    @Operation(summary = "Mark Notification as Read",
            description = "Marks a specific notification as read")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Notification marked as read"),
            @ApiResponse(responseCode = "404", description = "Notification not found")
    })
    public ResponseEntity<Void> markNotificationAsRead(@PathVariable String notificationId) {
        log.info("Marking notification as read: {}", notificationId);

        // Mock implementation - in a real application, update in repository
        return ResponseEntity.ok().build();
    }

    @PostMapping("/mark-all-read/{userId}")
    @Operation(summary = "Mark All Notifications as Read",
            description = "Marks all notifications for a user as read")
    public ResponseEntity<Void> markAllNotificationsAsRead(@PathVariable String userId) {
        log.info("Marking all notifications as read for user: {}", userId);

        // Mock implementation - in a real application, update in repository
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/{notificationId}")
    @Operation(summary = "Delete Notification",
            description = "Deletes a specific notification")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Notification deleted"),
            @ApiResponse(responseCode = "404", description = "Notification not found")
    })
    public ResponseEntity<Void> deleteNotification(@PathVariable String notificationId) {
        log.info("Deleting notification: {}", notificationId);

        // Mock implementation - in a real application, delete from repository
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/preferences/{userId}")
    @Operation(summary = "Get Notification Preferences",
            description = "Retrieves notification preferences for a user")
    public ResponseEntity<NotificationPreferenceDto> getNotificationPreferences(@PathVariable String userId) {
        log.info("Retrieving notification preferences for user: {}", userId);

        // Mock implementation - in a real application, fetch from repository
        NotificationPreferenceDto preferences = NotificationPreferenceDto.builder()
                .userId(userId)
                .emailNotificationsEnabled(true)
                .smsNotificationsEnabled(true)
                .pushNotificationsEnabled(false)
                .paymentRemindersEnabled(true)
                .adjustmentUpdatesEnabled(true)
                .financialInsightsEnabled(true)
                .reminderDaysBeforePayment(5)
                .build();

        return ResponseEntity.ok(preferences);
    }

    @PutMapping("/preferences/{userId}")
    @Operation(summary = "Update Notification Preferences",
            description = "Updates notification preferences for a user")
    public ResponseEntity<NotificationPreferenceDto> updateNotificationPreferences(
            @PathVariable String userId,
            @Valid @RequestBody NotificationPreferenceDto preferencesDto) {

        log.info("Updating notification preferences for user: {}", userId);

        // Validate userId
        if (!userId.equals(preferencesDto.getUserId())) {
            return ResponseEntity.badRequest().build();
        }

        // Mock implementation - in a real application, update in repository
        return ResponseEntity.ok(preferencesDto);
    }

    @PostMapping("/send")
    @Operation(summary = "Send Notification",
            description = "Sends a notification to a user (admin use)")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Notification sent"),
            @ApiResponse(responseCode = "400", description = "Invalid notification data")
    })
    public ResponseEntity<NotificationDto> sendNotification(@Valid @RequestBody NotificationDto notificationDto) {
        log.info("Sending notification to user: {}", notificationDto.getUserId());

        // Generate ID and timestamp
        notificationDto.setId(UUID.randomUUID().toString());
        notificationDto.setTimestamp(LocalDateTime.now());
        notificationDto.setIsRead(false);

        // Mock implementation - in a real application, save to repository and send via appropriate channels
        return ResponseEntity.ok(notificationDto);
    }
}
