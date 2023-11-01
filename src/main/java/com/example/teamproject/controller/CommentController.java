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
    public String comment(Model modelBoard, Model modelComment, Model modelFile, @PathVariable("id") Long id) {
        ;
        Optional<Board> boardData = boardRepository.findById(id);
        Board board = boardData.get();

        modelBoard.addAttribute("board", board);

        List<Comment> commentList = commentRepository.findByBoardId(id, Sort.by(Sort.Direction.DESC, "writeDateTime"));
        modelComment.addAttribute("commentList", commentList);

        List<FileAttach> fileList = fileAttachRepository.findByBoardId(id);
        modelFile.addAttribute("fileList", fileList);

        return "store/comment";
    }

    @GetMapping("/like-check")
    public String likeCheck(@RequestParam("content") String content,
            @RequestParam("like") Integer like,
            @ModelAttribute Comment comment,
            @RequestParam("boardId") Long boardId,
            Model model) {
        String email = (String) session.getAttribute("user_info");
        Optional<User> dbUser = userRepository.findByEmail(email);
        Optional<Board> dbBoard = boardRepository.findById(boardId);

        User user = dbUser.get();
        Board board = dbBoard.get();

        if (dbUser.isPresent()) {

            if (like == 1) {
                comment.setLike(like);
            } else {
                comment.setLike(like);
            }

            comment.setContent(content);
            comment.setBoard(board);
            comment.setUser(user);
            LocalDateTime wrtDtTm = LocalDateTime.now();
            comment.setWriteDateTime(wrtDtTm);

            commentRepository.save(comment);

            List<Comment> sumLike = commentRepository.findByBoardIdAndLike(boardId, 1);
            List<Comment> sumUnlike = commentRepository.findByBoardIdAndLike(boardId, 0);
            Integer numLike = sumLike.size();
            Integer numUnlike = sumUnlike.size();
            System.out.println(numLike);

            board.setNumLike(numLike);
            board.setNumUnlike(numUnlike);
            boardRepository.save(board);

            return "redirect:/store/comment/" + boardId;
        }
        return "/store/guide2";
    }

    @GetMapping("/detail/{id}")
    public String detail(Model model, @PathVariable Long id) {
        if (storeRepository.existsById(id)) {
            Store store = storeRepository.findById(id).get();

            List<Board> list = boardRepository.findByStore(store,
                    Sort.by(Sort.Direction.DESC, "registrationDateBoard"));
            model.addAttribute("list", list);
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