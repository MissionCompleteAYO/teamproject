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
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.example.teamproject.model.Board;
import com.example.teamproject.model.Comment;
import com.example.teamproject.model.FileAttach;
import com.example.teamproject.model.User;
import com.example.teamproject.repository.BoardRepository;
import com.example.teamproject.repository.CommentRepository;
import com.example.teamproject.repository.FileAttachRepository;
import com.example.teamproject.repository.UserRepository;

@Controller
@RequestMapping("/store")
public class CommentController {

    @Autowired
    UserRepository userRepository;

    @Autowired
    BoardRepository boardRepository;

    @Autowired
    CommentRepository commentRepository;

    @Autowired
    FileAttachRepository fileAttachRepository;

    @Autowired
    HttpSession session;

    @GetMapping("/comment/{id}")
    public String comment(Model modelBoard, Model modelComment, Model modelFile, @PathVariable("id") Long id) {
        Optional<Board> boardData = boardRepository.findById(id);
        Board board = boardData.get();
        modelBoard.addAttribute("board", board);

        List<Comment> commentList = commentRepository.findByBoardId(id);
        modelComment.addAttribute("commentList", commentList);

        List<FileAttach> fileList = fileAttachRepository.findByBoardId(id);
        modelFile.addAttribute("fileList", fileList);

        return "store/comment";
    }

    @GetMapping("/like-check")
    @ResponseBody
    public String likeCheck(@RequestParam("content") String content,
                            @RequestParam("like") Integer like,
                            @ModelAttribute Comment comment,
                            @ModelAttribute Board board) {

        String email = comment.getBoard().getUser().getEmail();
        Optional<User> dbUser = userRepository.findByEmail(email);

        if(dbUser.isPresent()) {
            return "alertComment";
        } else {
            if (like == 1) {
                Integer sum = comment.getLike();
                comment.setLike(sum++);
                comment.setContent(content);
                comment.setBoard(board);
            } else {
                Integer sum = comment.getUnlike();
                comment.setUnlike(sum++);
                comment.setContent(content);
                comment.setBoard(board);
            }
        }
        return "sucess";
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
