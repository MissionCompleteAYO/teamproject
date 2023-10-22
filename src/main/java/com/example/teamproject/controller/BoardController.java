package com.example.teamproject.controller;

import javax.servlet.http.HttpSession;
import javax.transaction.Transactional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import com.example.teamproject.model.Board;
import com.example.teamproject.model.User;
import com.example.teamproject.repository.BoardRepository;

@Controller
@RequestMapping("/store")
public class BoardController {
    @Autowired
    BoardRepository boardRepository;

    @Autowired
    HttpSession session;

    @GetMapping("/list")
    public String list() {
        return "store/list";
    }

    @GetMapping("/write")
    public String write() {
        return "store/write";
    }

    @Transactional
    @PostMapping("/write")
    public String writePost(@ModelAttribute Board board) {
        User user = (User) session.getAttribute("user_info");
        if (user == null) {
            return "redirect:/login";
        }

        String userId = user.getName();
        board.setUserId(userId);
        boardRepository.save(board);

        return "redirect:/store/detail";
    }
}
