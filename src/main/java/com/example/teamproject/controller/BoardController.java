package com.example.teamproject.controller;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Date;
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
        newstores.addAll(storeRepository.findByName("123"));
        newstores.addAll(storeRepository.findByName("비비큐"));
        newstores.addAll(storeRepository.findByName("교촌치킨"));
        newstores.addAll(storeRepository.findByName("소촌옥 숯불갈비"));
        newstores.addAll(storeRepository.findByName("맘스터치"));
        newstores.addAll(storeRepository.findByName("굽탄"));
        newstores.addAll(storeRepository.findByName("뼈다귀천국 소촌점"));
        newstores.addAll(storeRepository.findByName("삼대족발"));
        newstores.addAll(storeRepository.findByName("교촌치킨"));
        newstores.addAll(storeRepository.findByName("광주인력개발원11"));
        newstores.addAll(storeRepository.findByName("벌크커피"));

        model.addAttribute("newstores", newstores);

        List<Store> adstores = new ArrayList<>();
        adstores.addAll(storeRepository.findByName("광주인력개발원"));
        adstores.addAll(storeRepository.findByName("카페 스태리"));
        adstores.addAll(storeRepository.findByName("전라도이야기"));
        adstores.addAll(storeRepository.findByName("토토로"));
        adstores.addAll(storeRepository.findByName("힐링초밥"));
        model.addAttribute("adstores", adstores);

        return "store/list";
    }

    @GetMapping("/write/{id}")
    public String write(Model model, @PathVariable("id") Long id) {
        Optional<Store> dbStore = storeRepository.findById(id);
        Store store = dbStore.get();
        model.addAttribute("store", store);

        return "store/write";

    }

    @Transactional
    @PostMapping("/write/{id}")
    public String writePost(
            @RequestParam(value = "title") String title,
            @RequestParam(value = "content") String content,
            @RequestParam(value = "costEffectiveness") Integer costEffectiveness,
            @RequestParam(value = "quality") Integer quality,
            @RequestParam(value = "unique") Integer unique,
            @RequestParam(value = "waitingTime") Integer waitingTime,
            @RequestParam(value = "service") Integer service,
            @PathVariable("id") Long id,
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
        Board board = new Board();
        board.setTitle(title);
        board.setContent(content);
        board.setCostEffectiveness(costEffectiveness);
        board.setQuality(quality);
        board.setUnique(unique);
        board.setWaitingTime(waitingTime);
        board.setService(service);
        Date date = new Date();
        board.setRegistrationDateBoard(date);
        Store dbstore = storeRepository.findById(id).get();
        board.setStore(dbstore);
        board.setUser(dbuser);
        Board savedBoard = boardRepository.save(board);

        if (mFiles != null && !mFiles.isEmpty()) {
            for (int i = 0; i < mFiles.size(); i++) {
                MultipartFile mFile = mFiles.get(i);
                if (!mFile.isEmpty()) {
                    try {

                        String originalFileName = mFile.getOriginalFilename();
                        byte[] fileBytes = mFile.getBytes();

                        String storagePath = "C:/Users/user/springboot/teamproject/src/main/resources/static/storageImage";

                        Path filePath = Paths.get(storagePath, originalFileName);

                        Path directoryPath = filePath.getParent();
                        if (!Files.exists(directoryPath)) {
                            Files.createDirectories(directoryPath);

                        }
                        int slash = filePath.toString().lastIndexOf("\\");
                        String relPath = filePath.toString().substring(slash - 13);

                        Files.write(filePath, fileBytes);

                        FileAttach fileAttach = new FileAttach();
                        fileAttach.setBoard(savedBoard);
                        fileAttach.setOriginName(originalFileName);
                        String savedFileName = UUID.randomUUID().toString();
                        fileAttach.setSavedName(savedFileName);
                        fileAttach.setFilePath(relPath);
                        fileAttachRepository.save(fileAttach);
                    } catch (IOException e) {
                        return "redirect:/store/write/" + id;
                    }
                }
            }
        }
        return "redirect:/store/detail/" + id;
    }
    // @Transactional
    // @PostMapping("/write/{id}")
    // public String writePost(@ModelAttribute Board board,
    // @PathVariable("id") Long id,
    // @RequestParam(value = "originName", required = false) List<MultipartFile>
    // mFiles) {
    // String loggedUser = (String) session.getAttribute("user_info");

    // if (loggedUser == null || loggedUser.isEmpty()) {
    // return "redirect:/login";
    // }
    // Optional<User> optionalUser = userRepository.findByEmail(loggedUser);

    // if (!optionalUser.isPresent()) {
    // return "redirect:/login";
    // }
    // User dbuser = optionalUser.get();
    // if (board.getTitle().isEmpty() ||
    // board.getContent().isEmpty() ||
    // board.getCostEffectiveness() == null ||
    // board.getQuality() == null ||
    // board.getService() == null ||
    // board.getUnique() == null ||
    // board.getWaitingTime() == null) {
    // return "redirect:/store/write/"+id;
    // }

    // Optional<Store> store = storeRepository.findById(id);
    // Store dbstore = store.get();
    // board.setStore(dbstore);
    // board.setUser(dbuser);
    // Date date = new Date();
    // board.setRegistrationDateBoard(date);
    // Board savedBoard = boardRepository.save(board);

    // if (mFiles != null && !mFiles.isEmpty()) {
    // for (int i = 0; i < mFiles.size(); i++) {
    // MultipartFile mFile = mFiles.get(i);
    // if (!mFile.isEmpty()) {
    // try {

    // String originalFileName = mFile.getOriginalFilename();
    // byte[] fileBytes = mFile.getBytes();

    // String storagePath =
    // "C:/Users/user/springboot/teamproject/src/main/resources/static/storageImage";

    // Path filePath = Paths.get(storagePath, originalFileName);

    // Path directoryPath = filePath.getParent();
    // if (!Files.exists(directoryPath)) {
    // Files.createDirectories(directoryPath);

    // }
    // int slash = filePath.toString().lastIndexOf("\\");
    // String relPath = filePath.toString().substring(slash - 13);

    // Files.write(filePath, fileBytes);

    // FileAttach fileAttach = new FileAttach();
    // fileAttach.setBoard(savedBoard);
    // fileAttach.setOriginName(originalFileName);
    // String savedFileName = UUID.randomUUID().toString();
    // fileAttach.setSavedName(savedFileName);
    // fileAttach.setFilePath(relPath);
    // fileAttachRepository.save(fileAttach);
    // } catch (IOException e) {
    // return "redirect:/store/write/"+id;
    // }
    // }
    // }
    // }
    // return "redirect:/store/detail/"+id;
    // }

    @GetMapping("/change/{id}")
    public String change(Model model, @PathVariable("id") Long id) {
        String loggedUser = (String) session.getAttribute("user_info");
        Optional<User> dbUser = userRepository.findByEmail(loggedUser);
        Optional<Board> dbBoard = boardRepository.findById(id);
        String savedEmail = dbBoard.get().getUser().getEmail();

        if (!savedEmail.equals(dbUser.get().getEmail())) {
            Long storeId = boardRepository.findById(id).get().getStore().getId();
            return "redirect:/store/detail/" + storeId;
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
                    try {
                        String originalFileName = mFile.getOriginalFilename();
                        byte[] fileBytes = mFile.getBytes();

                        String storagePath = "C:/Users/user/springboot/teamproject/src/main/resources/static/storageImage";

                        Path filePath = Paths.get(storagePath, originalFileName);
                        int slash = filePath.toString().lastIndexOf("\\");
                        String relPath = filePath.toString().substring(slash - 13);

                        Files.write(filePath, fileBytes);

                        FileAttach fileAttach = new FileAttach();
                        fileAttach.setOriginName(originalFileName);
                        String savedFileName = UUID.randomUUID().toString();
                        fileAttach.setSavedName(savedFileName);
                        fileAttach.setFilePath(relPath);
                        fileAttach.setBoard(board);
                        fileAttachRepository.save(fileAttach);
                    } catch (IOException e) {
                        return "redirect:/store/write";
                    }

                }
            }
        }
        boardRepository.save(existingBoard);
        Long storeId = boardRepository.findById(id).get().getStore().getId();
        return "redirect:/store/detail/" + storeId;
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