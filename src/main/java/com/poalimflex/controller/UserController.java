package com.poalimflex.controller;

import com.poalimflex.dto.user.UserCredentialsDto;
import com.poalimflex.dto.user.UserProfileDto;
import com.poalimflex.dto.user.UserRegistrationDto;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

/**
 * Controller for user management operations
 */
@RestController
@RequestMapping("/api/user")
@RequiredArgsConstructor
@Slf4j
@Tag(name = "User Management", description = "APIs for user registration and profile management")
public class UserController {

    // Mock implementation - in a real application, you would inject proper service layers
    private final PasswordEncoder passwordEncoder;

    @PostMapping("/register")
    @Operation(summary = "Register New User",
            description = "Registers a new user in the system")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "User successfully registered"),
            @ApiResponse(responseCode = "400", description = "Invalid registration data"),
            @ApiResponse(responseCode = "409", description = "User already exists")
    })
    public ResponseEntity<UserProfileDto> registerUser(@Valid @RequestBody UserRegistrationDto registrationDto) {
        log.info("Registering new user with email: {}", registrationDto.getEmail());

        // In a real implementation, check if user already exists
        // For demo purposes, assume registration is successful

        // Create user profile
        UserProfileDto profile = UserProfileDto.builder()
                .userId("USER" + (10000 + (int)(Math.random() * 90000)))
                .firstName(registrationDto.getFirstName())
                .lastName(registrationDto.getLastName())
                .email(registrationDto.getEmail())
                .phoneNumber(registrationDto.getPhoneNumber())
                .registrationDate(java.time.LocalDate.now())
                .build();

        return ResponseEntity.status(HttpStatus.CREATED).body(profile);
    }

    @GetMapping("/{userId}")
    @Operation(summary = "Get User Profile",
            description = "Retrieves the profile of a specific user")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully retrieved user profile"),
            @ApiResponse(responseCode = "404", description = "User not found")
    })
    public ResponseEntity<UserProfileDto> getUserProfile(@PathVariable String userId) {
        // Mock implementation - in a real application, you'd fetch from a repository
        if (userId.startsWith("USER")) {
            UserProfileDto profile = UserProfileDto.builder()
                    .userId(userId)
                    .firstName("John")
                    .lastName("Doe")
                    .email("john.doe@example.com")
                    .phoneNumber("555-123-4567")
                    .registrationDate(java.time.LocalDate.now().minusMonths(3))
                    .build();

            return ResponseEntity.ok(profile);
        }

        return ResponseEntity.notFound().build();
    }

    @PutMapping("/{userId}")
    @Operation(summary = "Update User Profile",
            description = "Updates the profile of an existing user")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "User profile successfully updated"),
            @ApiResponse(responseCode = "404", description = "User not found")
    })
    public ResponseEntity<UserProfileDto> updateUserProfile(
            @PathVariable String userId,
            @Valid @RequestBody UserProfileDto profileDto) {

        log.info("Updating profile for user: {}", userId);

        // Validate userId
        if (!userId.equals(profileDto.getUserId())) {
            return ResponseEntity.badRequest().build();
        }

        // Mock implementation - in a real application, update in repository
        if (userId.startsWith("USER")) {
            // Return the updated profile
            return ResponseEntity.ok(profileDto);
        }

        return ResponseEntity.notFound().build();
    }

    @PostMapping("/authenticate")
    @Operation(summary = "Authenticate User",
            description = "Authenticates a user with credentials")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Authentication successful"),
            @ApiResponse(responseCode = "401", description = "Authentication failed")
    })
    public ResponseEntity<UserProfileDto> authenticateUser(@Valid @RequestBody UserCredentialsDto credentials) {
        log.info("Authentication attempt for email: {}", credentials.getEmail());

        // Mock authentication - in a real implementation, this would verify against stored credentials
        if ("john.doe@example.com".equals(credentials.getEmail()) &&
                "password123".equals(credentials.getPassword())) {

            UserProfileDto profile = UserProfileDto.builder()
                    .userId("USER12345")
                    .firstName("John")
                    .lastName("Doe")
                    .email(credentials.getEmail())
                    .phoneNumber("555-123-4567")
                    .registrationDate(java.time.LocalDate.now().minusMonths(3))
                    .build();

            return ResponseEntity.ok(profile);
        }

        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
    }

    @DeleteMapping("/{userId}")
    @Operation(summary = "Delete User",
            description = "Deletes a user account")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "User successfully deleted"),
            @ApiResponse(responseCode = "404", description = "User not found")
    })
    public ResponseEntity<Void> deleteUser(@PathVariable String userId) {
        log.info("Deleting user: {}", userId);

        // Mock implementation - in a real application, delete from repository
        if (userId.startsWith("USER")) {
            return ResponseEntity.noContent().build();
        }

        return ResponseEntity.notFound().build();
    }
}
