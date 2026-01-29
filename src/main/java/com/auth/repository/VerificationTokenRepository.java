package com.auth.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.auth.entity.VerificationToken;

import java.util.Optional;

public interface VerificationTokenRepository
        extends JpaRepository<VerificationToken, Long> {

    Optional<VerificationToken> findByToken(String token);
}
