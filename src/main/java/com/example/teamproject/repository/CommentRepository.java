package com.example.teamproject.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.example.teamproject.model.Comment;

@Repository
public interface CommentRepository extends JpaRepository<Comment, Long> {
    
}
