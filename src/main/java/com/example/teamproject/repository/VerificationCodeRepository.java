package com.example.teamproject.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.teamproject.model.VerificationCode;
import java.util.Optional;


public interface VerificationCodeRepository extends JpaRepository<VerificationCode, Long> {
    Optional<VerificationCode> findByEmail(String email);    
}