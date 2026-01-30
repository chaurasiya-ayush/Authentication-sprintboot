package com.auth.service;

import java.time.LocalDateTime;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.auth.entity.RefreshToken;
import com.auth.entity.User;
import com.auth.exception.InvalidRefreshTokenException;
import com.auth.exception.RefreshTokenExpiredException;
import com.auth.exception.RefreshTokenRevokedException;
import com.auth.repository.RefreshTokenRepository;

@Service
public class RefreshTokenService {

    @Value("${app.jwt.refresh-expiry-minutes}")
    private Long refreshExpiryMinutes;

    private final RefreshTokenRepository refreshTokenRepository;

    public RefreshTokenService(RefreshTokenRepository refreshTokenRepository) {
        this.refreshTokenRepository = refreshTokenRepository;
    }

    // 🔐 LOGIN TIME
    public RefreshToken createRefreshToken(User user) {

        refreshTokenRepository.deleteByUser(user);

        RefreshToken refreshToken = new RefreshToken();
        refreshToken.setUser(user);
        refreshToken.setToken(UUID.randomUUID().toString());
        refreshToken.setExpiryDate(
                LocalDateTime.now().plusMinutes(refreshExpiryMinutes)
        );

        return refreshTokenRepository.save(refreshToken);
    }

    // 🔁 REFRESH TIME
    public RefreshToken verifyRefreshToken(String token) {

        RefreshToken refreshToken = refreshTokenRepository
                .findByToken(token)
                .orElseThrow(() ->
                        new InvalidRefreshTokenException("Refresh token is invalid"));

        if (refreshToken.isRevoked()) {
            throw new RefreshTokenRevokedException(
                    "You are logged out. Please login again.");
        }

        if (refreshToken.getExpiryDate().isBefore(LocalDateTime.now())) {
            throw new RefreshTokenExpiredException(
                    "Refresh token expired. Please login again.");
        }

        return refreshToken;
    }

    // 🚪 LOGOUT TIME
    public void revokeToken(String token) {
        refreshTokenRepository.findByToken(token)
                .ifPresent(rt -> {
                    rt.setRevoked(true);
                    refreshTokenRepository.save(rt);
                });
    }
}
