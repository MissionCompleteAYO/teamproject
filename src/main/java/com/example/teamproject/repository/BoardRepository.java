package com.example.teamproject.repository;

import java.util.List;

import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.example.teamproject.model.Board;
import com.example.teamproject.model.Store;

@Repository
public interface BoardRepository extends JpaRepository<Board, Long> {
       List<Board> findByStore(Store store, Sort sort);
}
