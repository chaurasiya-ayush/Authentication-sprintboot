package com.example.auth.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.auth.entity.RefreshToken;

public interface RefreshTokenRepository
extends JpaRepository<RefreshToken, Long> {

Optional<RefreshToken> findByToken(String token);
}