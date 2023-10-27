package com.example.teamproject.controller;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.Random;

import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.thymeleaf.context.Context;
import org.thymeleaf.spring5.SpringTemplateEngine;

import com.example.teamproject.email.Mailer;
import com.example.teamproject.email.SMTPAuthenticator;
import com.example.teamproject.model.Feedback;
import com.example.teamproject.model.User;
import com.example.teamproject.model.VerificationCode;
import com.example.teamproject.repository.FeedbackRepository;
import com.example.teamproject.repository.UserRepository;
import com.example.teamproject.repository.VerificationCodeRepository;
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

    @Autowired
    SpringTemplateEngine templateEngine;

    @Autowired
    VerificationCodeRepository verificationCodeRepository;

    @GetMapping("/login")
    public String login() {
        return "auth/login";
    }

    @Transactional
    @PostMapping("/login")
    @ResponseBody
    public Map<String, Object> loginPost(
            @RequestParam("email") String email,
            @RequestParam("pw") String pw) {
        Map<String, Object> map = new HashMap<>();

        Optional<User> user = userRepository.findByEmail(email);
        if (user.isPresent()) {
            String emailId = user.get().getEmail();
            String userPw = user.get().getPw();
            boolean isMach = passwordEncoder.matches(pw, userPw);
            if (emailId.equals(email) && isMach) {
                map.put("result", true);
                session.setAttribute("user_info", email);
            } else {
                map.put("result", false);
                map.put("msg", "입력하신 정보가 올바르지 않습니다.");
            }
        }
        return map;
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

        Map<String, Object> emailCheckResult = emailCheckPost(email);

        if (emailCheckResult.get("msg").equals("사용 가능한 이메일 아이디입니다.")) {
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

    @PostMapping("/emailCheck")
    @ResponseBody
    public Map<String, Object> emailCheckPost(
            @RequestParam("email") String email) {

        Map<String, Object> map = new HashMap<>();

        if (email == null || email.trim().isEmpty()) {
            map.put("result", false);
            map.put("msg", "이메일 형식이 올바르지 않습니다.");
        } else {
            Optional<User> user = userRepository.findByEmail(email);
            if (user.isPresent()) {
                map.put("result", false);
                map.put("msg", "이미 등록된 이메일 아이디입니다.");
            } else {
                map.put("result", true);
                map.put("msg", "사용 가능한 이메일 아이디입니다.");
            }
        }
        return map;
    }

    @PostMapping("/phoneNumCheck")
    @ResponseBody
    public Map<String, Object> phoneNumCheck(
            @RequestParam("phoneNum") String phoneNum) {
        Map<String, Object> map = new HashMap<>();

        if (phoneNum == null || phoneNum.trim().isEmpty()) {
            map.put("result", false);
            map.put("msg", "유효하지 않은 휴대폰 번호입니다.");
        } else {
            Optional<User> user = userRepository.findByPhoneNum(phoneNum);
            if (user.isPresent()) {
                map.put("result", false);
                map.put("msg", "이미 등록된 휴대폰 번호입니다.");
            } else {
                map.put("result", true);
                map.put("msg", "사용 가능한 휴대폰 번호입니다.");
            }
        }
        return map;
    }

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
    public Map<String, Object> changeMyInfoPost(
            @RequestParam("pw") String pw
            // @RequestParam("phoneNum") String phoneNum,
            // @RequestParam("address") String address
            ) {
        Map<String, Object> map = new HashMap<>();

        String userEmailId = (String) session.getAttribute("user_info");
        Optional<User> userId = userRepository.findByEmail(userEmailId);
        String pwDb = userId.get().getPw();
        if (userId.isPresent() && passwordEncoder.matches(pw, pwDb)) {
            User user = userId.get();
            String encodedPw = passwordEncoder.encode(pw);
            user.setPw(encodedPw);
            user.setPhoneNum(user.getPhoneNum());
            user.setAddress(user.getAddress());
            User updateUser = userRepository.save(user);
            String emailId = updateUser.getEmail();
            map.put("result", true);
            map.put("msg", "입력하신 두 비밀번호가 일치합니다.");
            session.setAttribute("user_info", emailId);
        } else {
            map.put("result", false);
            map.put("msg", "비밀번호가 일치하지 않습니다.");
        }
        return map;
    }

    @Transactional
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

    @Transactional
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

    @Transactional
    @GetMapping("/mail")
    @ResponseBody
    public Map<String, Object> mail(
            @RequestParam("email") String email,
            @RequestParam("verificationCode") String verificationCode) {
        Map<String, Object> map = new HashMap<>();

        if (verificationCode == null || verificationCode.trim().isEmpty()) {
            map.put("result", false);
            map.put("msg", "메일로 발송된 인증번호를 입력해 주세요.");
        } else {
            Optional<VerificationCode> mail = verificationCodeRepository.findByEmail(email);
            if (mail.isPresent()) {
                Integer code = mail.get().getVerificationCode();
                if (code.equals(Integer.valueOf(verificationCode))) {
                    map.put("result", true);
                    map.put("msg", "인증번호가 일치합니다.");
                } else {
                    map.put("result", false);
                    map.put("msg", "인증번호가 일치하지 않습니다.");
                }
            } else {
                map.put("result", false);
                map.put("msg", "해당 이메일에 대한 인증번호가 존재하지 않습니다.");
            }
        }
        return map;
    }

    @Transactional
    @PostMapping("/mail")
    @ResponseBody
    public Map<String, Object> mailPost(
            @RequestParam("email") String email) {
        Map<String, Object> map = new HashMap<>();

        LocalDateTime time = LocalDateTime.now();
        LocalDateTime expirationTime = time.plusMinutes(10);

        Random random = new Random();
        int verificationCode = 100000 + random.nextInt(900000); // 100000에서 999999까지의 랜덤 숫자 생성

        Optional<VerificationCode> mail = verificationCodeRepository.findByEmail(email);
        if (mail.isPresent()) {
            VerificationCode oldMAil = mail.get();
            oldMAil.setExpirationTime(expirationTime);
            oldMAil.setVerificationCode(verificationCode);
            verificationCodeRepository.save(oldMAil);
        } else {
            VerificationCode newMail = new VerificationCode();
            newMail.setEmail(email);
            newMail.setExpirationTime(expirationTime);
            newMail.setVerificationCode(verificationCode);
            verificationCodeRepository.save(newMail);
        }

        Context context = new Context();
        context.setVariable("verificationCode", verificationCode);
        String content = templateEngine.process("auth/emailnum", context);

        Mailer mailer = new Mailer();
        mailer.sendMail(
                email, // 수신 이메일(관리자)
                "[TeamProject] 이메일 인증번호가 도착하였습니다.", // [작성자 이메일]제목
                content, // 본문
                new SMTPAuthenticator()); // 인증
        map.put("result", true);
        map.put("msg", "인증번호가 발송되었습니다.");

        return map;
    }
}