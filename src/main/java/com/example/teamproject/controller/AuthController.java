package com.example.teamproject.controller;

import java.util.Optional;

import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.example.teamproject.model.User;
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

    @GetMapping("/login")
    public String login() {
        return "auth/login";
    }

    @PostMapping("/login")
    public String loginPost(
            @RequestParam("email") String email,
            @RequestParam("pw") String pw) {
        Optional<User> user = userRepository.findByEmail(email);
        if (user.isPresent()) {
            String id = user.get().getEmail();
            String userPw = user.get().getPw();
            boolean isMach = passwordEncoder.matches(pw, userPw);
            if (id.equals(email) && isMach) {
                session.setAttribute("user_info", email);
                return "redirect:/";
            }
        }
        return "/auth/login";
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
}

// 회원탈퇴
// @GetMapping("/exit")
// public String exit() {
// User user = (User) session.getAttribute("user_info");
// userRepository.delete(user);
// return "redirect:/";
// }