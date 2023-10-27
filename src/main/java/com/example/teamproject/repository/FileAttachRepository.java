package com.example.teamproject.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.example.teamproject.model.Board;
import com.example.teamproject.model.FileAttach;

@Repository
public interface FileAttachRepository extends JpaRepository<FileAttach, Long> {
    public List<FileAttach> findByBoard(Board board);
    public List<FileAttach> findByBoardId(Long boardId);
}
