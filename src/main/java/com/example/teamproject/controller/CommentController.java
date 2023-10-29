package com.example.teamproject.controller;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.example.teamproject.model.Board;
import com.example.teamproject.model.Comment;
import com.example.teamproject.model.FileAttach;
import com.example.teamproject.model.Store;
import com.example.teamproject.model.User;
import com.example.teamproject.repository.BoardRepository;
import com.example.teamproject.repository.CommentRepository;
import com.example.teamproject.repository.FileAttachRepository;
import com.example.teamproject.repository.StoreRepository;
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

    @Autowired
    StoreRepository storeRepository;

   @GetMapping("/comment/{id}")
    public String comment(Model modelBoard, Model modelComment, Model modelFile, @PathVariable("id") Long id) {;
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
    public String likeCheck(@RequestParam("content") String content,
            @RequestParam("like") Integer like,
            @ModelAttribute Comment comment,
            @ModelAttribute Board board,
            @ModelAttribute User user,
            @RequestParam("boardId") Long boardId) {

        String email = (String) session.getAttribute("user_info");
        Optional<User> dbUser = userRepository.findByEmail(email);

        if (dbUser.isPresent()){
            Long userId = dbUser.get().getId();

            System.out.println(content);
            System.out.println(like);
            System.out.println(boardId);
            System.out.println(userId);

            board.setId(boardId);
            user.setId(userId);

            System.out.println(board);
            System.out.println(user);

            if (like == 1) {
                System.out.println("like==1 진입");
                Integer sumLike = comment.getLike();
                if (sumLike == null) {
                    sumLike = 0;
                }

                Integer sumUnlike = comment.getUnlike();
                if (sumUnlike == null) {
                    sumUnlike = 0;
                }

                comment.setLike(sumLike++);
                comment.setUnlike(sumUnlike);

            } else {
                System.out.println("like==0 진입");
                Integer sumUnlike = comment.getUnlike();
                if (sumUnlike == null) {
                    sumUnlike = 0;
                }

                Integer sumLike = comment.getLike();
                if (sumLike == null) {
                    sumLike = 0;
                }
                comment.setUnlike(sumUnlike++);
                comment.setLike(sumLike);

            }

                comment.setContent(content);
                comment.setBoard(board);
                comment.setUser(user);
                LocalDateTime wrtDtTm = LocalDateTime.now();
                comment.setWriteDateTime(wrtDtTm);
        
            commentRepository.save(comment);

            System.out.println("저장완료");

            return "redirect:/store/comment/" + boardId;
        }
        
        return "/store/guide2";
        

    }

    @GetMapping("/detail/{id}")
    public String detail(Model model, @PathVariable Long id) {
        List<Board> list = boardRepository.findAll(Sort.by(Sort.Direction.DESC, "registrationDateBoard"));
        model.addAttribute("list", list);
        if (storeRepository.existsById(id)) {
            Store store = storeRepository.findById(id).get();
            model.addAttribute("store", store);
            return "store/detail";
        }

        return "redirect:/store/list";
    }

    @PostMapping("/detail")
    public String detailPost() {

        return "redirect:store/detail";
    }
}