package com.harvesttracker.features.auth.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.harvesttracker.common.validation.StrongPassword;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import java.time.Instant;
import java.util.List;

public class AuthDTOs {

    public record LoginRequest(
            @NotBlank(message = "Email is required")
            @Email(message = "Invalid email format")
            String email,

            @NotBlank(message = "Password is required")
            String password
    ) {}

    public record RefreshTokenRequest(
            @NotBlank(message = "Refresh token is required")
            String refreshToken
    ) {}

    public record ChangePasswordRequest(
            @NotBlank(message = "Current password is required")
            String currentPassword,

            @NotBlank(message = "New password is required")
            @StrongPassword
            String newPassword,

            @NotBlank(message = "Confirm password is required")
            String confirmPassword
    ) {}

    @JsonInclude(JsonInclude.Include.NON_NULL)
    public record AuthResponse(
            String accessToken,
            String refreshToken,
            String tokenType,
            long expiresIn,
            UserSummaryDto user
    ) {
        public static AuthResponse of(String accessToken, String refreshToken, long expiresIn, UserSummaryDto user) {
            return new AuthResponse(accessToken, refreshToken, "Bearer", expiresIn, user);
        }
    }

    @JsonInclude(JsonInclude.Include.NON_NULL)
    public record UserSummaryDto(
            Long id,
            String name,
            String email,
            String role,
            List<String> permissions
    ) {}

    @JsonInclude(JsonInclude.Include.NON_NULL)
    public record UserProfileDto(
            Long id,
            String name,
            String email,
            String phone,
            String role,
            String status,
            List<String> permissions,
            Instant lastLoginAt,
            Instant createdAt
    ) {}
}
