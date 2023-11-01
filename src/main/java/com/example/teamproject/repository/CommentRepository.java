package com.example.teamproject.repository;

import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;


import com.example.teamproject.model.Comment;


import java.util.List;

@Repository
public interface CommentRepository extends JpaRepository<Comment, Long> {
    public List<Comment> findByUserId(Long userId);
    public List<Comment> findByBoardId(Long boardId);
    List<Comment> findByBoardId(Long boardId, Sort sort);
    public List<Comment> findByBoardIdAndLike(Long boardId, Integer like);
   
}
