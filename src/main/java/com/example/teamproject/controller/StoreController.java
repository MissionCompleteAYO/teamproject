package com.example.teamproject.controller;

import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import com.example.teamproject.repository.BoardRepository;

@Controller
@RequestMapping("/store")
public class StoreController {
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

    @PostMapping("/write")
    public String writePost() {
        session.getAttribute("user_info");

        return "store/write";
    }
}
