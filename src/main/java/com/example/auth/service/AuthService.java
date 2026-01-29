package com.example.auth.service;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.example.auth.dto.LoginRequest;
import com.example.auth.dto.RegisterRequest;
import com.example.auth.entity.PasswordResetOtp;
import com.example.auth.entity.PasswordResetValidation;
import com.example.auth.entity.RefreshToken;
import com.example.auth.entity.User;
import com.example.auth.entity.VerificationToken;
import com.example.auth.exception.EmailAlreadyExistsException;
import com.example.auth.exception.InvalidCredentialsException;
import com.example.auth.exception.OtpInvalidException;
import com.example.auth.exception.ResetNotAllowedException;
import com.example.auth.exception.SamePasswordException;
import com.example.auth.exception.TokenInvalidException;
import com.example.auth.repository.PasswordResetOtpRepository;
import com.example.auth.repository.PasswordResetValidationRepository;
import com.example.auth.repository.RefreshTokenRepository;
import com.example.auth.repository.UserRepository;
import com.example.auth.repository.VerificationTokenRepository;
import com.example.auth.security.JwtService;
import com.example.auth.util.OtpGenerator;

import jakarta.transaction.Transactional;

@Service 
public class AuthService {

	private final UserRepository userRepository;
	private final PasswordEncoder passwordEncoder;
	private final VerificationTokenRepository tokenRepository;
	private final MailService mailService;
	private final RefreshTokenRepository refreshTokenRepository;
	private final JwtService jwtService;
	private final PasswordResetOtpRepository passwordResetOtpRepository;
	private final PasswordResetValidationRepository passwordResetValidationRepository;
	
	// 👇 Constructor Injection (MOST IMPORTANT)

	public void register(RegisterRequest request) {

		// 1️⃣ Email already exist check
		// Reason: same email se multiple accounts nahi hone chahiye
		if (userRepository.existsByEmail(request.getEmail())) {
			throw new EmailAlreadyExistsException("Email already registered");
		}

		// 2️⃣ User create
		// Reason: DB me user ka main record banana
		User user = new User();

		// ---- AUTH DATA ----
		user.setEmail(request.getEmail());
		user.setPassword(passwordEncoder.encode(request.getPassword())); // password encrypt karna mandatory hai
		user.setEnabled(false); // email verify hone ke baad true hoga

		// ---- PROFILE DATA ----
		user.setFirstName(request.getFirstName());
		user.setLastName(request.getLastName());
		user.setPhoneNumber(request.getPhoneNumber());
		user.setDateOfBirth(request.getDateOfBirth());
		user.setGender(request.getGender());

	
		userRepository.save(user);

		// 4️⃣ Create verification token
		// Reason: email verify karne ke liye unique token
		VerificationToken token = new VerificationToken();
		token.setToken(UUID.randomUUID().toString()); // random secure token
		token.setUser(user); // kis user ka token
		token.setExpiryDate(LocalDateTime.now().plusHours(24)); // 24 hours valid

		tokenRepository.save(token);

		// 5️⃣ Send verification email
		// Reason: user ko link mile jisse wo account enable kare
		mailService.sendVerificationEmail(user.getEmail(), token.getToken());
	}

	public AuthService(UserRepository userRepository, PasswordEncoder passwordEncoder,
			VerificationTokenRepository tokenRepository, MailService mailService,
			RefreshTokenRepository refreshTokenRepository, JwtService jwtService,
			PasswordResetOtpRepository passwordResetOtpRepository,
			PasswordResetValidationRepository passwordResetValidationRepository) {
		super();
		this.userRepository = userRepository;
		this.passwordEncoder = passwordEncoder;
		this.tokenRepository = tokenRepository;
		this.mailService = mailService;
		this.refreshTokenRepository = refreshTokenRepository;
		this.jwtService = jwtService;
		this.passwordResetOtpRepository = passwordResetOtpRepository;
		this.passwordResetValidationRepository = passwordResetValidationRepository;
	}

	public boolean verifyToken(String token) {

		VerificationToken verificationToken = tokenRepository.findByToken(token).orElse(null);

		if (verificationToken == null) {
			throw new TokenInvalidException("Invalid verification token");
		}

		if (verificationToken.getExpiryDate().isBefore(LocalDateTime.now())) {
			throw new TokenInvalidException("Verification token expired");
		}

		User user = verificationToken.getUser();
		user.setEnabled(true);

		userRepository.save(user);//

		tokenRepository.delete(verificationToken);

		return true;
	}

	public Map<String, String> login(LoginRequest request) {

		User user = userRepository.findByEmail(request.getEmail())
				.orElseThrow(() -> new InvalidCredentialsException("Invalid credentials"));

		if (!user.isEnabled()) {
			throw new InvalidCredentialsException("Email not verified");
		}

		if (!passwordEncoder.matches(request.getPassword(), user.getPassword())) {
			throw new InvalidCredentialsException("Invalid credentials");
		}

		String accessToken = jwtService.generateAccessToken(user.getEmail());

		String refreshToken = jwtService.generateRefreshToken(user.getEmail());

		RefreshToken refresh = new RefreshToken();
		refresh.setToken(refreshToken);
		refresh.setUser(user);
		refresh.setExpiryDate(LocalDateTime.now().plusDays(7));

		refreshTokenRepository.save(refresh);

		Map<String, String> response = new HashMap<>();
		response.put("accessToken", accessToken);
		response.put("refreshToken", refreshToken);

		return response;
	}

	public Map<String, String> refreshAccessToken(String refreshToken) {

		RefreshToken token = refreshTokenRepository.findByToken(refreshToken)
				.orElseThrow(() -> new RuntimeException("Invalid refresh token"));

		if (token.getExpiryDate().isBefore(LocalDateTime.now())) {
			throw new RuntimeException("Refresh token expired");
		}

		String newAccessToken = jwtService.generateAccessToken(token.getUser().getEmail());

		return Map.of("accessToken", newAccessToken);
	}

	public void forgotPassword(String email) {

		User user = userRepository.findByEmail(email).orElseThrow(() -> new RuntimeException("Email not registered"));

		// invalidate old OTPs
		passwordResetOtpRepository.findAllByUser(user).forEach(otp -> {
			otp.setUsed(true);
			passwordResetOtpRepository.save(otp);
		});

		String otp = OtpGenerator.generateOtp();

		PasswordResetOtp resetOtp = new PasswordResetOtp();
		resetOtp.setUser(user);
		resetOtp.setOtp(passwordEncoder.encode(otp));
		resetOtp.setExpiryTime(LocalDateTime.now().plusMinutes(10));
		resetOtp.setUsed(false);

		passwordResetOtpRepository.save(resetOtp);

		mailService.sendPasswordResetOtp(user.getEmail(), otp);
	}
	@Transactional
	public void verifyResetOtp(String email, String otp) {
		User user = userRepository.findByEmail(email).orElseThrow(() -> new OtpInvalidException("Invalid OTP"));

		PasswordResetOtp resetOtp = passwordResetOtpRepository.findTopByUserAndUsedFalseOrderByExpiryTimeDesc(user)
				.orElseThrow(() -> new OtpInvalidException("OTP not found"));

		if (resetOtp.getExpiryTime().isBefore(LocalDateTime.now())) {
			throw new OtpInvalidException("OTP expired");
		}

		if (!passwordEncoder.matches(otp, resetOtp.getOtp())) {
			throw new OtpInvalidException("Invalid OTP");
		}
		// OTP mark used
		resetOtp.setUsed(true);
		passwordResetOtpRepository.save(resetOtp);

		PasswordResetValidation validation = new PasswordResetValidation();
		validation.setUser(user);
		validation.setExpiryTime(LocalDateTime.now().plusMinutes(10));
		// ⭐ REQUIRED
		validation.setActive(true);
		validation.setUsed(false);
		passwordResetValidationRepository.save(validation);
	}
	@Transactional
	public void resetPassword(String email, String newPassword) {
		User user = userRepository.findByEmail(email)
				.orElseThrow(() -> new ResetNotAllowedException("Reset not allowed"));

//
	PasswordResetValidation validation = passwordResetValidationRepository.findByUserAndActiveTrueAndUsedFalse(user)
			.orElseThrow(() -> new ResetNotAllowedException("Otp verification required"));
		if (validation.getExpiryTime().isBefore(LocalDateTime.now())) {
			throw new ResetNotAllowedException("Reset session expired");
		}
		if(passwordEncoder.matches(newPassword, user.getPassword())) {
			throw new SamePasswordException(
					"New Password cannot be same as old"	);
		}
		user.setPassword(
				passwordEncoder.encode(newPassword));
		userRepository.save(user);
		validation.setUsed(true);
		validation.setActive(false);
		passwordResetValidationRepository.save(validation);
		
	}
}
