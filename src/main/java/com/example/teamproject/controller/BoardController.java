package com.example.teamproject.controller;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

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
import com.example.teamproject.model.Store;
import com.example.teamproject.model.User;
import com.example.teamproject.model.Visits;
import com.example.teamproject.repository.BoardRepository;
import com.example.teamproject.repository.FileAttachRepository;
import com.example.teamproject.repository.StoreRepository;
import com.example.teamproject.repository.UserRepository;
import com.example.teamproject.repository.VisitsRepository;

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

    @Autowired
    StoreRepository storeRepository;

    @Autowired
    VisitsRepository visitsRepository;

    @GetMapping("/list")
    public String list(Model model) {
        List<Store> newstores = new ArrayList<>();
        newstores.addAll(storeRepository.findByName("빵빵아"));
        newstores.addAll(storeRepository.findByName("벌크커피"));
        newstores.addAll(storeRepository.findByName("벌크커피"));
        model.addAttribute("newstores", newstores);

        List<Store> adstores = new ArrayList<>();
        adstores.addAll(storeRepository.findByName("예시"));
        adstores.addAll(storeRepository.findByName("예시"));
        adstores.addAll(storeRepository.findByName("예시"));
        adstores.addAll(storeRepository.findByName("예시"));
        adstores.addAll(storeRepository.findByName("교촌"));
        model.addAttribute("adstores", adstores);
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
                    String originalFileName = mFile.getOriginalFilename();
                    FileAttach fileAttach = new FileAttach();
                    fileAttach.setOriginName(originalFileName);
                    fileAttach.setSavedName(originalFileName);
                    fileAttach.setBoard(board);
                    fileAttachRepository.save(fileAttach);
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

    @GetMapping("/")
    public String store() {
        return "store/store";
    }

    @PostMapping("/")
    public String storePost(@ModelAttribute Store store) {
        store.getName();
        store.getCatchphrase();
        storeRepository.save(store);
        return "redirect:/store/";
    }

    @GetMapping("/map/{id}")
    public String map(@PathVariable("id") Long id, Model model) {
        Optional<Store> road = storeRepository.findById(id);
        model.addAttribute("store", road.get());
        return "store/map";
    }

    @GetMapping("/balloon")
    public String balloon(Model model) {
        Long userIdCount = userRepository.count();
        Long boardIdCount = boardRepository.count();
        Visits visit = visitsRepository.findAll().get(0);
        Long visitIdCount = visit.getNumberOfVisitors();
        model.addAttribute("userIdCount", userIdCount);
        model.addAttribute("boardIdCount", boardIdCount);
        model.addAttribute("visitIdCount", visitIdCount);
        return "store/balloon";
    }

   @GetMapping("/weather")
    public String weather() {
     
        return "store/weather";
    }
}