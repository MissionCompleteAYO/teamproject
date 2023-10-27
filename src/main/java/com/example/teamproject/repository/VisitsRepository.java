package com.example.teamproject.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.teamproject.model.Visits;

public interface VisitsRepository extends JpaRepository<Visits, Long> {
    
}
