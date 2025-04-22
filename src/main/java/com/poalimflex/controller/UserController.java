package com.poalimflex.controller;

import com.poalimflex.dto.user.UserCredentialsDto;
import com.poalimflex.dto.user.UserProfileDto;
import com.poalimflex.dto.user.UserRegistrationDto;
import com.poalimflex.entity.User;
import com.poalimflex.repository.UserRepository;
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

import java.time.LocalDate;
import java.util.Optional;
import java.util.Random;

/**
 * Controller for user management operations
 */
@RestController
@RequestMapping("/api/user")
@RequiredArgsConstructor
@Slf4j
@Tag(name = "User Management", description = "APIs for user registration and profile management")
public class UserController {

    private final PasswordEncoder passwordEncoder;
    private final UserRepository userRepository;

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

        // Check if user with this email already exists
        if (userRepository.existsByEmail(registrationDto.getEmail())) {
            log.warn("Registration failed: Email already exists: {}", registrationDto.getEmail());
            return ResponseEntity.status(HttpStatus.CONFLICT).build();
        }

        // Generate a unique user ID
        String userId = generateUniqueUserId();

        // Create and save the user entity
        User user = User.builder()
                .userId(userId)
                .firstName(registrationDto.getFirstName())
                .lastName(registrationDto.getLastName())
                .email(registrationDto.getEmail())
                .passwordHash(passwordEncoder.encode(registrationDto.getPassword()))
                .phoneNumber(registrationDto.getPhoneNumber())
                .registrationDate(LocalDate.now())
                .emailVerified(false)
                .twoFactorEnabled(false)
                .build();

        User savedUser = userRepository.save(user);
        log.info("User successfully registered with ID: {}", savedUser.getUserId());

        // Create and return the user profile DTO
        UserProfileDto profile = UserProfileDto.builder()
                .userId(savedUser.getUserId())
                .firstName(savedUser.getFirstName())
                .lastName(savedUser.getLastName())
                .email(savedUser.getEmail())
                .phoneNumber(savedUser.getPhoneNumber())
                .registrationDate(savedUser.getRegistrationDate())
                .emailVerified(savedUser.getEmailVerified())
                .twoFactorEnabled(savedUser.getTwoFactorEnabled())
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
        log.info("Fetching profile for user: {}", userId);

        return userRepository.findByUserId(userId)
                .map(this::convertToProfileDto)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
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

        // Validate userId matches the one in DTO
        if (!userId.equals(profileDto.getUserId())) {
            return ResponseEntity.badRequest().build();
        }

        // Check if user exists
        Optional<User> userOpt = userRepository.findByUserId(userId);
        if (userOpt.isEmpty()) {
            return ResponseEntity.notFound().build();
        }

        // Update user entity
        User user = userOpt.get();
        user.setFirstName(profileDto.getFirstName());
        user.setLastName(profileDto.getLastName());
        user.setPhoneNumber(profileDto.getPhoneNumber());

        // Save updated user
        User updatedUser = userRepository.save(user);

        // Return updated profile
        return ResponseEntity.ok(convertToProfileDto(updatedUser));
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

        // Enhanced logging for debugging
        Optional<User> userOpt = userRepository.findByEmail(credentials.getEmail());

        if (userOpt.isEmpty()) {
            log.warn("Authentication failed: User not found with email: {}", credentials.getEmail());
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        User user = userOpt.get();
        boolean passwordMatches = passwordEncoder.matches(credentials.getPassword(), user.getPasswordHash());
        log.info("Password match: {}", passwordMatches);

        if (!passwordMatches) {
            log.warn("Authentication failed: Invalid password for user: {}", credentials.getEmail());
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        log.info("Authentication successful for user: {}", credentials.getEmail());
        return ResponseEntity.ok(convertToProfileDto(user));
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

        // Check if user exists
        Optional<User> userOpt = userRepository.findByUserId(userId);
        if (userOpt.isEmpty()) {
            return ResponseEntity.notFound().build();
        }

        // Delete user
        userRepository.delete(userOpt.get());
        log.info("User deleted: {}", userId);

        return ResponseEntity.noContent().build();
    }

    // Helper methods
    private UserProfileDto convertToProfileDto(User user) {
        return UserProfileDto.builder()
                .userId(user.getUserId())
                .firstName(user.getFirstName())
                .lastName(user.getLastName())
                .email(user.getEmail())
                .phoneNumber(user.getPhoneNumber())
                .registrationDate(user.getRegistrationDate())
                .emailVerified(user.getEmailVerified())
                .twoFactorEnabled(user.getTwoFactorEnabled())
                .build();
    }

    private String generateUniqueUserId() {
        // Generate a random 5-digit number and prefix with "USER"
        Random random = new Random();
        String userId;
        do {
            int randomNum = 10000 + random.nextInt(90000); // 5-digit number between 10000 and 99999
            userId = "USER" + randomNum;
        } while (userRepository.findByUserId(userId).isPresent());

        return userId;
    }
}