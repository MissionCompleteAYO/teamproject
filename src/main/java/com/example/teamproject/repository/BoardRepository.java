package com.example.teamproject.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.example.teamproject.model.Board;

@Repository
public interface BoardRepository extends JpaRepository<Board, Long> {
    
}
