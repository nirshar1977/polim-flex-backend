package com.poalimflex.dto.user;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

/**
 * DTO for user profile
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "User profile information")
public class UserProfileDto {
    @Schema(description = "User ID", example = "USER12345")
    private String userId;

    @Schema(description = "First name", example = "John")
    @NotBlank(message = "First name is required")
    @Size(min = 2, max = 50, message = "First name must be between 2 and 50 characters")
    private String firstName;

    @Schema(description = "Last name", example = "Doe")
    @NotBlank(message = "Last name is required")
    @Size(min = 2, max = 50, message = "Last name must be between 2 and 50 characters")
    private String lastName;

    @Schema(description = "Email address", example = "john.doe@example.com")
    @NotBlank(message = "Email is required")
    @Email(message = "Invalid email format")
    private String email;

    @Schema(description = "Phone number", example = "555-123-4567")
    @Pattern(regexp = "^[0-9\\-\\+\\s]+$", message = "Invalid phone number format")
    private String phoneNumber;

    @Schema(description = "Registration date")
    private LocalDate registrationDate;

    @Schema(description = "Email verified", example = "true")
    private Boolean emailVerified;

    @Schema(description = "Two-factor authentication enabled", example = "false")
    private Boolean twoFactorEnabled;
}

