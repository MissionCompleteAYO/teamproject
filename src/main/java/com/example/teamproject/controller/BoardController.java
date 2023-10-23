package com.example.teamproject.controller;

import java.util.Optional;

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
import com.example.teamproject.repository.UserRepository;

@Controller
@RequestMapping("/store")
public class BoardController {
    @Autowired
    BoardRepository boardRepository;

    @Autowired
    HttpSession session;

    @Autowired
    UserRepository userRepository;

    @GetMapping("/list")
    public String list() {
        return "store/list";
    }

    @GetMapping("/write")
    public String write() {
        return "store/write";
    }

    // @Transactional
    // @PostMapping("/write")
    // public String writePost(@ModelAttribute Board board) {

    // User user = (User) session.getAttribute("user_info");
    // if (user == null) {
    // return "redirect:/login";
    // }
    // if (board.getTitle().isEmpty() || board.getCostEffectiveness() == null ||
    // board.getQuality() == null || board.getContent().isEmpty() ||
    // board.getService() == null || board.getUnique() == null ||
    // board.getWaitingTime() == null) {
    // return "redirect:/store/write";
    // }
    // String userId = user.getName();
    // board.setUserId(userId);
    // boardRepository.save(board);

    // return "redirect:/store/detail";
    // }
    @Transactional
    @PostMapping("/write")
    public String writePost(@ModelAttribute Board board) {
        String userId = (String) session.getAttribute("user_info");

        if (userId == null || userId.isEmpty()) {
            return "redirect:/login";
        }
        Optional<User> optionalUser = userRepository.findByEmail(userId);

        if (!optionalUser.isPresent()) {
            return "redirect:/login";
        }
        User user = optionalUser.get();

        if (board.getTitle().isEmpty() ||
                board.getContent().isEmpty() ||
                board.getCostEffectiveness() == null ||
                board.getQuality() == null ||
                board.getService() == null ||
                board.getUnique() == null ||
                board.getWaitingTime() == null) {
            return "redirect:/store/write";
        }
        board.setUserId(user.getName());
        boardRepository.save(board);

        return "redirect:/store/detail";
    }
}
