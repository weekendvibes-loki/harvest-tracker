package com.harvesttracker.features.auth.service;

import com.harvesttracker.common.security.CustomUserDetails;
import com.harvesttracker.common.security.JwtTokenProvider;
import com.harvesttracker.features.auth.domain.Permission;
import com.harvesttracker.features.auth.domain.User;
import com.harvesttracker.features.auth.dto.AuthDTOs.*;
import com.harvesttracker.features.auth.repository.UserRepository;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

@Service
public class AuthService {

    private final AuthenticationManager authenticationManager;
    private final UserRepository userRepository;
    private final JwtTokenProvider tokenProvider;
    private final PasswordEncoder passwordEncoder;

    public AuthService(AuthenticationManager authenticationManager,
                       UserRepository userRepository,
                       JwtTokenProvider tokenProvider,
                       PasswordEncoder passwordEncoder) {
        this.authenticationManager = authenticationManager;
        this.userRepository = userRepository;
        this.tokenProvider = tokenProvider;
        this.passwordEncoder = passwordEncoder;
    }

    @Transactional
    public AuthResponse login(LoginRequest request) {
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.email(), request.password())
        );

        CustomUserDetails userDetails = (CustomUserDetails) authentication.getPrincipal();
        User user = userDetails.getUser();

        // Update last login timestamp
        user.setLastLoginAt(Instant.now());
        userRepository.save(user);

        String roleCode = user.getRole() != null ? user.getRole().getCode() : "USER";
        List<String> permissions = extractPermissionCodes(user);

        String accessToken = tokenProvider.generateAccessToken(user.getId(), user.getEmail(), roleCode, permissions);
        String refreshToken = tokenProvider.generateRefreshToken(user.getId(), user.getEmail());

        UserSummaryDto userSummary = new UserSummaryDto(
                user.getId(),
                user.getName(),
                user.getEmail(),
                roleCode,
                permissions
        );

        return AuthResponse.of(accessToken, refreshToken, tokenProvider.getAccessTokenExpirationMs(), userSummary);
    }

    @Transactional(readOnly = true)
    public AuthResponse refreshToken(RefreshTokenRequest request) {
        String refreshToken = request.refreshToken();

        if (!tokenProvider.isRefreshToken(refreshToken)) {
            throw new BadCredentialsException("Invalid or expired refresh token");
        }

        String email = tokenProvider.getUsernameFromToken(refreshToken);
        User user = userRepository.findByEmailAndDeletedAtIsNull(email)
                .orElseThrow(() -> new UsernameNotFoundException("User not found: " + email));

        if (!user.isActive() || !"ACTIVE".equalsIgnoreCase(user.getStatus())) {
            throw new BadCredentialsException("User account is inactive or suspended");
        }

        String roleCode = user.getRole() != null ? user.getRole().getCode() : "USER";
        List<String> permissions = extractPermissionCodes(user);

        String newAccessToken = tokenProvider.generateAccessToken(user.getId(), user.getEmail(), roleCode, permissions);
        String newRefreshToken = tokenProvider.generateRefreshToken(user.getId(), user.getEmail());

        UserSummaryDto userSummary = new UserSummaryDto(
                user.getId(),
                user.getName(),
                user.getEmail(),
                roleCode,
                permissions
        );

        return AuthResponse.of(newAccessToken, newRefreshToken, tokenProvider.getAccessTokenExpirationMs(), userSummary);
    }

    @Transactional(readOnly = true)
    public UserProfileDto getCurrentUserProfile(String email) {
        User user = userRepository.findByEmailAndDeletedAtIsNull(email)
                .orElseThrow(() -> new UsernameNotFoundException("User not found: " + email));

        String roleCode = user.getRole() != null ? user.getRole().getCode() : "USER";
        List<String> permissions = extractPermissionCodes(user);

        return new UserProfileDto(
                user.getId(),
                user.getName(),
                user.getEmail(),
                user.getPhone(),
                roleCode,
                user.getStatus(),
                permissions,
                user.getLastLoginAt(),
                user.getCreatedAt()
        );
    }

    @Transactional
    public void changePassword(String email, ChangePasswordRequest request) {
        if (!request.newPassword().equals(request.confirmPassword())) {
            throw new IllegalArgumentException("New password and confirm password do not match");
        }

        User user = userRepository.findByEmailAndDeletedAtIsNull(email)
                .orElseThrow(() -> new UsernameNotFoundException("User not found: " + email));

        if (!passwordEncoder.matches(request.currentPassword(), user.getPasswordHash())) {
            throw new BadCredentialsException("Current password is incorrect");
        }

        user.setPasswordHash(passwordEncoder.encode(request.newPassword()));
        user.setUpdatedAt(Instant.now());
        userRepository.save(user);
    }

    private List<String> extractPermissionCodes(User user) {
        List<String> permissions = new ArrayList<>();
        if (user.getRole() != null && user.getRole().getPermissions() != null) {
            for (Permission p : user.getRole().getPermissions()) {
                if (p.isActive() && p.getDeletedAt() == null) {
                    permissions.add(p.getCode());
                }
            }
        }
        return permissions;
    }
}
