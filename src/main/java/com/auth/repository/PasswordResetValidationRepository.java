package com.auth.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.auth.entity.PasswordResetValidation;
import com.auth.entity.User;

public interface PasswordResetValidationRepository
        extends JpaRepository<PasswordResetValidation, Long> {

    Optional<PasswordResetValidation>
    findByUserAndActiveTrueAndUsedFalse(User user);

}
