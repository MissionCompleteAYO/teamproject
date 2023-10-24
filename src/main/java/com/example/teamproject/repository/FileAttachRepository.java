package com.example.teamproject.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.example.teamproject.model.FileAttach;

@Repository
public interface FileAttachRepository extends JpaRepository<FileAttach, Long> {

}
