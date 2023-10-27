package com.example.teamproject.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.teamproject.model.Feedback;

public interface FeedbackRepository extends JpaRepository<Feedback, Long> {
    
}