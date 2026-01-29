package com.example.auth.repository;

import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import com.example.auth.entity.PasswordResetOtp;
import com.example.auth.entity.User;

public interface PasswordResetOtpRepository extends JpaRepository<PasswordResetOtp,Long> {
    Optional<PasswordResetOtp> findByOtp(String otp);
    List<PasswordResetOtp>findAllByUser(User user);
//    Optional<PasswordResetOtp>findfirstByUserAndusedFalseOrderByCreatedByDesc(User user);
    Optional<PasswordResetOtp> findFirstByUserAndUsedFalseOrderByCreatedAtDesc(User user);
    Optional<PasswordResetOtp> findFirstByUserAndUsedFalseOrderByExpiryTimeDesc(User user);
    Optional<PasswordResetOtp> findTopByUserAndUsedFalseOrderByExpiryTimeDesc(User user);
    
}
