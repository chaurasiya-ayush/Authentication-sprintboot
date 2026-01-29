package com.example.auth.entity;

import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;

@Entity
@Table(name = "password_reset_otps")
public class PasswordResetOtp {
   @Id
   @GeneratedValue(strategy = GenerationType.IDENTITY)
   private Long id; 
   @ManyToOne
   @JoinColumn(name = "user_id",nullable = false)
   private User user;
   
   @Column(nullable = false)
   private String otp;
   @Column(nullable = false)
   private LocalDateTime expiryTime;
   @Column(nullable = false)
   private boolean used = false;
   @Column(updatable = false)
   private LocalDateTime createdAt;
   @PrePersist
   private void onCreate() {
	   this.createdAt = LocalDateTime.now();
	   
   }
   public Long getId() {
	return id;
   }
   public void setId(Long id) {
	this.id = id;
   }
   public User getUser() {
	return user;
   }
   public void setUser(User user) {
	this.user = user;
   }
   public String getOtp() {
	return otp;
   }
   public void setOtp(String otp) {
	this.otp = otp;
   }
   public LocalDateTime getExpiryTime() {
	return expiryTime;
   }
   public void setExpiryTime(LocalDateTime expiryTime) {
	this.expiryTime = expiryTime;
   }
   public boolean isUsed() {
	return used;
   }
   public void setUsed(boolean used) {
	this.used = used;
   }
   public LocalDateTime getCreatedAt() {
	return createdAt;
   }
   public void setCreatedAt(LocalDateTime createdAt) {
	this.createdAt = createdAt;
   }
}
