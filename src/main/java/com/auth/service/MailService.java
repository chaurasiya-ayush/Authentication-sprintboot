package com.auth.service;

import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
public class MailService {

    private JavaMailSender mailSender;

    public MailService(JavaMailSender mailSender) {
        this.mailSender = mailSender;
    }

    // 🔹 COMMON METHOD (REUSABLE)
    private void sendMail(String to, String subject, String body) {

        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(to);
        message.setSubject(subject);
        message.setText(body);

        mailSender.send(message);
    }

    // 🔹 EMAIL VERIFICATION
    public void sendVerificationEmail(String toEmail, String token) {

        String verifyLink =
            "http://localhost:8080/api/auth/verify?token=" + token;

        String body =
            "Click the link to verify your account:\n" + verifyLink;

        sendMail(toEmail, "Verify your email", body);
    }

    // 🔹 PASSWORD RESET OTP
    public void sendPasswordResetOtp(String email, String otp) {

        String body =
            "Your OTP is: " + otp +
            "\nThis OTP is valid for 10 minutes.";

        sendMail(email, "Reset your password", body);
    }
}
