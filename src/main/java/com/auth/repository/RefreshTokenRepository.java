package com.auth.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.auth.entity.RefreshToken;
import com.auth.entity.User;

public interface RefreshTokenRepository
extends JpaRepository<RefreshToken, Long> {

Optional<RefreshToken> findByToken(String token);
void deleteByUser(User user);
}