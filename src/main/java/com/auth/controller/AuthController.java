package com.auth.controller;

import java.util.Map;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import com.auth.dto.*;
import com.auth.entity.User;
import com.auth.exception.UnauthorizedException;
import com.auth.exception.UserNotFoundException;
import com.auth.repository.UserRepository;
import com.auth.service.AuthService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/auth")
@CrossOrigin(origins = "http://localhost:3000")
public class AuthController {

    private final AuthService authService;
    private final UserRepository userRepository;

    public AuthController(AuthService authService,
                          UserRepository userRepository) {
        this.authService = authService;
        this.userRepository = userRepository;
    }

    // ✅ SIMPLE PROFILE (email only)
    @GetMapping("/profile")
    public ResponseEntity<?> profile() {

        String email = SecurityContextHolder
                .getContext()
                .getAuthentication()
                .getName();

        if (email == null || email.equals("anonymousUser")) {
            throw new UnauthorizedException("User is not authenticated");
        }

        User user = userRepository
                .findByEmail(email)
                .orElseThrow(() ->
                        new UserNotFoundException(
                                "User not found with email: " + email)
                );

        return ResponseEntity.ok(user);
    }


    // ✅ FULL USER DATA (used by frontend getCurrentUser)
    @GetMapping("/data")
    public ResponseEntity<?> getCurrentUser(
            @AuthenticationPrincipal UserDetails userDetails
    ) {

        if (userDetails == null) {
            throw new UnauthorizedException("User is not authenticated");
        }

        User user = userRepository
                .findByEmail(userDetails.getUsername())
                .orElseThrow(() ->
                        new UserNotFoundException(
                                "User not found with email: "
                                        + userDetails.getUsername())
                );

        return ResponseEntity.ok(user);
    }

    @PostMapping("/register")
    public ResponseEntity<String> register(
            @Valid @RequestBody RegisterRequest request) {

        authService.register(request);

        return ResponseEntity.ok(
                "Registered successfully. Verify your email.");
    }

    @GetMapping("/verify")
    public String verifyEmail(@RequestParam String token) {

        boolean verified = authService.verifyToken(token);

        return verified
                ? "Email verified successfully!"
                : "Invalid or expired token";
    }

    @PostMapping("/login")
    public ResponseEntity<Map<String, String>> login(
            @RequestBody LoginRequest request) {

        return ResponseEntity.ok(
                authService.login(request));
    }

    @PostMapping("/refresh")
    public ResponseEntity<Map<String, String>> refresh(
            @RequestBody RefreshTokenRequest request) {

        return ResponseEntity.ok(
                authService.refreshAccessToken(
                        request.getRefreshToken()));
    }

    @PostMapping("/forgot-password")
    public ResponseEntity<String> forgotPassword(
            @Valid @RequestBody ForgotPasswordRequest request) {

        authService.forgotPassword(request.getEmail());

        return ResponseEntity.ok(
                "Otp sent to your email");
    }

    @PostMapping("/verify-reset-otp")
    public ResponseEntity<String> verifyResetOtp(
            @RequestBody VerifyOtpRequest request) {

        authService.verifyResetOtp(
                request.getEmail(),
                request.getOtp());

        return ResponseEntity.ok(
                "OTP verified. You may reset password.");
    }

    @PostMapping("/reset-password")
    public ResponseEntity<String> resetPassword(
            @RequestBody ResetPasswordRequest request) {

        authService.resetPassword(
                request.getEmail(),
                request.getNewPassword());

        return ResponseEntity.ok(
                "Password reset successfully");
    }

    @PostMapping("/logout")
    public ResponseEntity<?> logout(
            @RequestBody RefreshTokenRequest request) {

        authService.logout(
                request.getRefreshToken());

        return ResponseEntity.ok(
                Map.of("message", "Logged out successfully"));
    }
}
