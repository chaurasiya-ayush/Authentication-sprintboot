# 🔐 Spring Boot Authentication Backend

A complete **Authentication & Authorization backend** built using **Java + Spring Boot**, following a **clean layered architecture**.  
This project provides secure user authentication using **JWT (Access & Refresh Tokens)**, **OTP verification via Gmail (SMTP)**, and **MySQL** as the database.

---

## 🚀 Features

- ✅ User Registration
- ✅ Login & Logout
- ✅ JWT Authentication
  - Access Token
  - Refresh Token
- ✅ OTP Verification via Email (Gmail SMTP)
- ✅ Forgot Password & Reset Password
- ✅ Secure Password Handling
- ✅ Token Validation & Expiry Handling
- ✅ Global Exception Handling
- ✅ Clean Layered Architecture

---

## 🧱 Tech Stack

- **Java 17**
- **Spring Boot**
- **Spring Security**
- **JWT (JSON Web Token)**
- **Spring Data JPA**
- **MySQL**
- **Maven**
- **Gmail SMTP**

---

## 📂 Project Structure

```
src/main/java/com/example/auth
│
├── config
│   ├── SecurityConfig.java
│   └── SecurityBeansConfig.java
│
├── controller
│   └── AuthController.java
│
├── dto
│   ├── LoginRequest.java
│   ├── RegisterRequest.java
│   ├── ForgotPasswordRequest.java
│   ├── ResetPasswordRequest.java
│   └── VerifyOtpRequest.java
│
├── entity
│   ├── User.java
│   ├── Gender.java
│   ├── RefreshToken.java
│   ├── VerificationToken.java
│   ├── PasswordResetOtp.java
│   └── PasswordResetValidation.java
│
├── exception
│   ├── GlobalExceptionHandler.java
│   ├── EmailAlreadyExistsException.java
│   ├── EmailNotFoundException.java
│   ├── InvalidCredentialsException.java
│   ├── InvalidOtpException.java
│   ├── OtpExpiredException.java
│   ├── TokenInvalidException.java
│   └── SamePasswordException.java
│
├── repository
│   ├── UserRepository.java
│   ├── RefreshTokenRepository.java
│   ├── VerificationTokenRepository.java
│   └── PasswordResetOtpRepository.java
│
├── security
│   └── JwtService.java
│
├── service
│   ├── AuthService.java
│   └── MailService.java
│
├── util
│   └── OtpGenerator.java
│
└── AuthApplication.java
```

## 🔑 Authentication Flow

### 1️⃣ Registration
- User registers with email & password
- OTP is sent to email via Gmail SMTP
- User verifies OTP
- Account is activated

### 2️⃣ Login
- User logs in with email & password
- Generates:
  - **Access Token (JWT)**
  - **Refresh Token**

### 3️⃣ Access Token
- Used to access secured APIs
- Short-lived

### 4️⃣ Refresh Token
- Used to generate new Access Token
- Stored securely in database

### 5️⃣ Forgot / Reset Password
- OTP sent to registered email
- OTP verification required
- Password reset allowed only after validation

---

## 🔐 Security

- Passwords are **encrypted**
- JWT tokens are **signed & validated**
- Custom exceptions for:
  - Invalid credentials
  - Expired OTP
  - Invalid token
- Centralized **Global Exception Handling**

---

## ⚙️ Configuration

### Database (MySQL)
```properties
spring.datasource.url=jdbc:mysql://localhost:3306/auth_db
spring.datasource.username=YOUR_DB_USERNAME
spring.datasource.password=YOUR_DB_PASSWORD

JWT
jwt.secret=YOUR_SECRET_KEY
jwt.access-token.expiration=900000
jwt.refresh-token.expiration=604800000

Gmail SMTP
spring.mail.host=smtp.gmail.com
spring.mail.port=587
spring.mail.username=YOUR_EMAIL
spring.mail.password=YOUR_APP_PASSWORD
spring.mail.properties.mail.smtp.auth=true
spring.mail.properties.mail.smtp.starttls.enable=true


⚠️ Use App Password, not your Gmail password.

▶️ How to Run
git clone https://github.com/your-username/your-repo-name.git
cd your-repo-name
mvn clean install
mvn spring-boot:run


Server will start at:

http://localhost:8080
