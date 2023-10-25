package com.example.teamproject.controller;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.example.teamproject.model.Feedback;
import com.example.teamproject.model.User;
import com.example.teamproject.repository.FeedbackRepository;
import com.example.teamproject.repository.UserRepository;
import com.example.teamproject.util.Encrypt;

@Controller
public class AuthController {
    @Autowired
    UserRepository userRepository;

    @Autowired
    Encrypt encrypt;

    @Autowired
    PasswordEncoder passwordEncoder;

    @Autowired
    HttpSession session;

    @Autowired
    HttpServletResponse response;

    @Autowired
    FeedbackRepository feedbackRepository;

    @GetMapping("/login")
    public String login() {
        return "auth/login";
    }

    @Transactional
    @PostMapping("/login")
    public String loginPost(
            @RequestParam("email") String email,
            @RequestParam("pw") String pw,
            Model model) {
        Optional<User> user = userRepository.findByEmail(email);
        if (user.isPresent()) {
            String id = user.get().getEmail();
            String userPw = user.get().getPw();
            boolean isMach = passwordEncoder.matches(pw, userPw);
            if (id.equals(email) && isMach) {
                session.setAttribute("user_info", email);
                return "redirect:/";
            } else {
                model.addAttribute("error", "회원님이 입력한 정보가 올바르지 않습니다.");
            }
        } else {
            model.addAttribute("error", "이메일 아이디가 존재하지 않습니다.");
        }
        return "auth/login";
    }

    @GetMapping("/logout")
    public String logout() {
        session.removeAttribute("user_info");
        return "auth/logout";
    }

    @GetMapping("/signup")
    public String signup() {
        return "auth/signup";
    }

    @Transactional
    @PostMapping("/signup")
    public String signupPost(
            @RequestParam("name") String name,
            @RequestParam("email") String email,
            @RequestParam("pw") String pw,
            @RequestParam("phoneNum") String phoneNum,
            @RequestParam("address") String address) {
        String emailCheckResult = emailCheck(email);

        if (emailCheckResult.equals("사용가능한 이메일입니다.")) {
            String encryptedPw = passwordEncoder.encode(pw);
            User user = new User();
            user.setName(name);
            user.setEmail(email);
            user.setPw(encryptedPw);
            user.setPhoneNum(phoneNum);
            user.setAddress(address);
            userRepository.save(user);
            return "redirect:/login";
        } else {
            return "auth/signup";
        }
    }

    @GetMapping("/emailCheck")
    @ResponseBody
    public String emailCheck(
            @RequestParam("email") String email) {

        String result = "";

        if (email == null || email.trim().isEmpty()) {
            result = "유효하지 않은 이메일입니다.";
        }

        Optional<User> opt = userRepository.findByEmail(email);
        if (opt.isPresent()) {
            result = "유효하지 않은 이메일입니다.";
        } else {
            result = "사용가능한 이메일입니다.";
        }
        return result;
    };

    @GetMapping("/phoneNumCheck")
    @ResponseBody
    public String phoneNumCheck(
            @RequestParam("phoneNum") String phoneNum) {

        String result = "";

        if (phoneNum == null || phoneNum.trim().isEmpty()) {
            result = "유효하지 않은 아이디입니다.";
        }

        Optional<User> opt = userRepository.findByEmail(phoneNum);
        if (opt.isPresent()) {
            result = "유효하지 않은 휴대폰 번호입니다.";
        } else {
            result = "사용가능한 휴대폰 번호입니다.";
        }
        return result;
    };

    @GetMapping("/manage")
    public String manage() {
        return "auth/manage";
    }

    @GetMapping("/manage/changeMyInfo")
    public String changeMyInfo() {
        if (session.getAttribute("user_info") != null) {
            return "auth/changeMyInfo";
        } else {
            return "redirect:/";
        }
    }

    @Transactional
    @PostMapping("/manage/changeMyInfo")
    @ResponseBody
    public String changeMyInfoPost(
            @ModelAttribute User user) {
        String userEmailId = (String) session.getAttribute("user_info");
        String encodePw = passwordEncoder.encode(user.getPw());
        if (user != null && user.getEmail().equals(userEmailId)) {
            User userInfo = new User();
            userInfo.setEmail(user.getEmail());
            userInfo.setPw(encodePw);
            userInfo.setPhoneNum(user.getPhoneNum());
            userInfo.setAddress(user.getAddress());
            userRepository.save(user);
            return "redirect:/manage";
        } else {
            return "auth/changeMyInfo";
        }
    }

    @PostMapping("/pwCheck")
    @ResponseBody
    public Map<String, Object> pwCheckPost(
            @ModelAttribute User user) {
        Map<String, Object> map = new HashMap<>();

        String userInfo = (String) session.getAttribute("user_info");
        Optional<User> emailDb = userRepository.findByEmail(userInfo);
        if (emailDb.isPresent() && passwordEncoder.matches(user.getPw(), emailDb.get().getPw())) {
            map.put("result", true);
        } else {
            map.put("result", false);
            map.put("msg", "입력하신 비밀번호가 올바르지 않습니다.");
        }
        return map;
    }

    @GetMapping("/manage/deleteMyAccount")
    public String deleteMyAccount(
            @ModelAttribute User user) {
        if (session.getAttribute("user_info") != null) {
            return "auth/deleteMyAccount";
        } else {
            return "redirect:/";
        }
    }

    @PostMapping("/manage/deleteMyAccount")
    @ResponseBody
    public Map<String, Object> deleteMyAccountPost(
            @ModelAttribute User user,
            @RequestParam("cancelReason") String cancelReason) {
        Map<String, Object> map = new HashMap<>();

        LocalDateTime time = LocalDateTime.now();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd, HH:mm:ss");
        String formattedDate = time.format(formatter);
        LocalDateTime parsDateTime = LocalDateTime.parse(formattedDate, formatter);

        String loggedUser = (String) session.getAttribute("user_info");
        Optional<User> userDb = userRepository.findByEmail(loggedUser);
        if (userDb.isPresent()) {
            Feedback feedback = new Feedback();
            feedback.setCancelDate(parsDateTime);
            feedback.setCancelReason(cancelReason);
            feedbackRepository.save(feedback);
            System.out.println(feedback);
            System.out.println(feedbackRepository);

            userRepository.delete(userDb.get());
            session.invalidate();
            map.put("result", true);
            map.put("msg", "남겨주신 사유에 대해 적극 개선하겠습니다. 그동안 이용해 주셔서 감사합니다!");
        } else {
            map.put("result", false);
        }

        return map;
    }
}123