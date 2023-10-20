package com.example.teamproject.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/company")
public class CommentController {
    
    @GetMapping("/detail")
    public String detail() {
        return "/company/detail";
    }

    @PostMapping("/detail")
    public String detailPost() {
        return "redirect:company/detail";
    }
}
