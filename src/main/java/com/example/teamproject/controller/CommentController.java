package com.example.teamproject.controller;

import java.util.List;
import java.util.Optional;

import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Sort.Order;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.example.teamproject.model.Board;
import com.example.teamproject.model.Comment;
import com.example.teamproject.model.User;
import com.example.teamproject.repository.BoardRepository;
import com.example.teamproject.repository.CommentRepository;

@Controller
@RequestMapping("/store")
public class CommentController {

    @Autowired
    BoardRepository boardRepository;

    @Autowired
    CommentRepository commentRepository;

    @Autowired
    HttpSession session;

    @GetMapping("/comment")
    @ResponseBody
    public String comment(Model modelBoard, Model modelComment, @PathVariable("id") Long id) {
        Optional<Board> boardData = boardRepository.findById(id);
        Board board = boardData.get();
        modelBoard.addAttribute("board", board);

        List<Comment> commentList = commentRepository.findByBoardId(id);
        modelComment.addAttribute("commentList", commentList);

        return "redirect:store/comment";
    }

    @GetMapping("/detail")
    public String detail(Model model) {

        Sort sort = Sort.by(Order.asc("id"));
        List<Board> list = boardRepository.findAll(sort);

        model.addAttribute("list", list);

        return "/store/detail";
    }

    @PostMapping("/detail")
    public String detailPost() {

        return "redirect:store/detail";
    }
}
