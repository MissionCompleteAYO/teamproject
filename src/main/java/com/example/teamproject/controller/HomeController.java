package com.example.teamproject.controller;

import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import com.example.teamproject.model.Visits;
import com.example.teamproject.repository.BoardRepository;
import com.example.teamproject.repository.UserRepository;
import com.example.teamproject.repository.VisitsRepository;

@Controller
public class HomeController {
    @Autowired
    VisitsRepository visitsRepository;

    @Autowired
    UserRepository userRepository;
    
    @Autowired
    BoardRepository boardRepository;

    @GetMapping("/")
    public String home(Model model) {
        List<Visits> visitsList = visitsRepository.findAll();
        if (visitsList.size() > 0) {
            Visits visit = visitsList.get(0);
            visit.setNumberOfVisitors(visit.getNumberOfVisitors() + 1);
            visitsRepository.save(visit);
        } else {
            Visits newVisit = new Visits();
            newVisit.setNumberOfVisitors(1L);
            visitsRepository.save(newVisit);
        }
        Long userIdCount = userRepository.count();
        Long boardIdCount = boardRepository.count();
        Visits visit = visitsRepository.findAll().get(0);
        Long visitIdCount = visit.getNumberOfVisitors();
        model.addAttribute("userIdCount", userIdCount);
        model.addAttribute("boardIdCount", boardIdCount);
        model.addAttribute("visitIdCount", visitIdCount);
        return "index";
    }
}
