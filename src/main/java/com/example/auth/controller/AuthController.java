package com.example.auth.controller;

import java.util.Map;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import com.example.auth.dto.ForgotPasswordRequest;
import com.example.auth.dto.LoginRequest;
import com.example.auth.dto.RegisterRequest;
import com.example.auth.dto.ResetPasswordRequest;
import com.example.auth.dto.VerifyOtpRequest;
import com.example.auth.service.AuthService;
import jakarta.validation.Valid;


@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }
    // 👇 Register API
    @PostMapping("/register")
    public ResponseEntity<String> register(@Valid @RequestBody RegisterRequest request) {

        authService.register(request);

        return ResponseEntity.ok("Registered successfully. Verify your email.");
    }
    
    @GetMapping("/verify")
    public String verifyEmail(@RequestParam String token) {

        boolean verified = authService.verifyToken(token);

        if (verified) {
            return "Email verified successfully!";
        } else {
            return "Invalid or expired token";
        }
    }
    
    @PostMapping("/login")
    public ResponseEntity<Map<String, String>> login(
            @RequestBody LoginRequest request) {

        Map<String, String> tokens = authService.login(request);

        return ResponseEntity.ok(tokens);
    }

    @PostMapping("/refresh")
    public ResponseEntity<Map<String, String>> refreshToken(
            @RequestParam String refreshToken) {

        Map<String, String> response =
                authService.refreshAccessToken(refreshToken);

        return ResponseEntity.ok(response);
    }
    @PostMapping("/forgot-password")
    public ResponseEntity<String>forgotpassword(
    		@Valid @RequestBody ForgotPasswordRequest request
    		){
    		authService.forgotPassword(request.getEmail());
    		return ResponseEntity.ok("Otp sent to your email");
    }
   
    @PostMapping("/verify-reset-otp")
    public ResponseEntity<String> verifyResetOtp(
            @RequestBody VerifyOtpRequest request) {

        authService.verifyResetOtp(
            request.getEmail(),
            request.getOtp()
        );

        return ResponseEntity.ok(
            "OTP verified. You may reset password.");
    }
    @PostMapping("/reset-password")
    public ResponseEntity<String> resetPassword(
            @RequestBody ResetPasswordRequest request) {

        authService.resetPassword(
            request.getEmail(),
            request.getNewPassword()
        );

        return ResponseEntity.ok(
            "Password reset successfully");
    }
    
}
