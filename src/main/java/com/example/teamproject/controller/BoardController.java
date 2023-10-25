package com.example.teamproject.controller;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import javax.servlet.http.HttpSession;
import javax.transaction.Transactional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import com.example.teamproject.model.Board;
import com.example.teamproject.model.FileAttach;
import com.example.teamproject.model.User;
import com.example.teamproject.repository.BoardRepository;
import com.example.teamproject.repository.FileAttachRepository;
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

    @Autowired
    FileAttachRepository fileAttachRepository;

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
    public String writePost(@ModelAttribute Board board,
            @RequestParam(value = "originName", required = false) List<MultipartFile> mFiles) {
                System.out.println(board);
                System.out.println(mFiles);

        String loggedUser = (String) session.getAttribute("user_info");

        if (loggedUser == null || loggedUser.isEmpty()) {
            return "redirect:/login";
        }
        Optional<User> optionalUser = userRepository.findByEmail(loggedUser);

        if (!optionalUser.isPresent()) {
            return "redirect:/login";
        }
        User dbuser = optionalUser.get();

        if (board.getTitle().isEmpty() ||
                board.getContent().isEmpty() ||
                board.getCostEffectiveness() == null ||
                board.getQuality() == null ||
                board.getService() == null ||
                board.getUnique() == null ||
                board.getWaitingTime() == null) {
            return "redirect:/store/write";
        }
        if (mFiles != null && !mFiles.isEmpty()) {
            for (int i = 0; i < mFiles.size(); i++) {
                MultipartFile mFile = mFiles.get(i);
                if (!mFile.isEmpty()) {
                    try {
                        String originalFileName = mFile.getOriginalFilename();
                        byte[] fileBytes = mFile.getBytes();

                        String storagePath = "/storageImage";
                        Path filePath = Paths.get(storagePath, originalFileName);
                        Files.write(filePath, fileBytes);

                        FileAttach fileAttach = new FileAttach();
                        fileAttach.setOriginName(originalFileName);
                        String savedFileName = UUID.randomUUID().toString();
                        fileAttach.setSavedName(savedFileName);
                        fileAttach.setFilePath(filePath.toString());
                        fileAttach.setBoard(board);
                        fileAttachRepository.save(fileAttach);
                    } catch (IOException e) {
                        return "redirect:/store/write";
                    }

                }
            }
        }
        board.setUser(dbuser);
        boardRepository.save(board);

        return "redirect:/store/detail";
    }

    @GetMapping("/change/{id}")
    public String change(Model model, @PathVariable("id") Long id) {
        String loggedUser = (String) session.getAttribute("user_info");
        Optional<User> dbUser = userRepository.findByEmail(loggedUser);
        Optional<Board> dbBoard = boardRepository.findById(id);
        String savedEmail = dbBoard.get().getUser().getEmail();

        if (!savedEmail.equals(dbUser.get().getEmail())) {
            return "redirect:/store/detail";
        }

        List<FileAttach> savedFiles = fileAttachRepository.findByBoard(dbBoard.get());

        Board board = dbBoard.get();
        model.addAttribute("costEffectiveness", board.getCostEffectiveness());
        model.addAttribute("quality", board.getQuality());
        model.addAttribute("service", board.getService());
        model.addAttribute("unique", board.getUnique());
        model.addAttribute("waitingTime", board.getWaitingTime());
        model.addAttribute("board", board);
        model.addAttribute("fileAttachs", savedFiles);
        return "store/change";
    }

    @PostMapping("/change/{id}")
    public String changePost(@ModelAttribute Board board, @PathVariable("id") Long id,
            @RequestParam(value = "originName", required = false) List<MultipartFile> mFiles) {
        Optional<Board> optionalBoard = boardRepository.findById(id);
        Board existingBoard = optionalBoard.get();
        existingBoard.setContent(board.getContent());
        existingBoard.setTitle(board.getTitle());
        existingBoard.setCostEffectiveness(board.getCostEffectiveness());
        existingBoard.setQuality(board.getQuality());
        existingBoard.setService(board.getService());
        existingBoard.setUnique(board.getUnique());
        existingBoard.setWaitingTime(board.getWaitingTime());

        if (mFiles != null && !mFiles.isEmpty()) {
            for (int i = 0; i < mFiles.size(); i++) {
                MultipartFile mFile = mFiles.get(i);
                if (!mFile.isEmpty()) {
                    String originalFileName = mFile.getOriginalFilename();
                    FileAttach fileAttach = new FileAttach();
                    fileAttach.setOriginName(originalFileName);
                    fileAttach.setSavedName(originalFileName);
                    fileAttach.setBoard(board);
                    fileAttachRepository.save(fileAttach);
                }
            }
        }
        boardRepository.save(existingBoard);
        return "redirect:/store/detail";
    }

    @GetMapping("/delete/{id}")
    public String deletePost(@PathVariable("id") Long id) {
        if (fileAttachRepository.existsById(id)) {
            Long boardId = fileAttachRepository.findById(id).get().getBoard().getId();
            fileAttachRepository.deleteById(id);
            return "redirect:/store/change/" + boardId;
        }
        return "redirect:/store/detail";
    }
}
