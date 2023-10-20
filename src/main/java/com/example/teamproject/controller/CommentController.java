package com.example.teamproject.controller;

import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.example.teamproject.repository.CommentRepository;

@Controller
@RequestMapping("/store")
public class CommentController {

    @Autowired
    CommentRepository commentRepository;

    @Autowired
    HttpSession session;

    @GetMapping("/comment")
    @ResponseBody
    public String comment(@ModelAttribute User user) {
        Optional<User> dbUser = session.getAttribute("user_info");
        commentRepository.findByUserId()
        
    }
    
    @GetMapping("/detail")
    public String detail() {
        return "/store/detail";
    }

    @PostMapping("/detail")
    public String detailPost() {


        return "redirect:store/detail";
    }
}
