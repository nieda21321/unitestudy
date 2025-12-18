package com.example.login_demo.controller;

import com.example.login_demo.service.MemberService;
import com.example.login_demo.vo.MemberVO;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class AccountController {

    @Autowired
    private MemberService memberService;

    // 회원가입 폼
    @GetMapping("/signup")
    public String signupForm() {

        // /WEB-INF/views/signup.jsp
        return "forward:/WEB-INF/views/signup.jsp";
    }

    // 회원가입 처리 ( 폼 POST )
    @PostMapping("/signupProc")
    public String signupProc(@ModelAttribute MemberVO member, BindingResult bindingResult, Model model) {

        System.err.println("signupProc 진입! ID: " + member.getMemberId()); // 빨간색 로그

        if (bindingResult.hasErrors()) {

            model.addAttribute("error", "입력 값이 올바르지 않습니다.");
            return "signup";
        }

        boolean ok = memberService.register(member);

        if ( !ok ) {

            model.addAttribute("error", "이미 사용중인 아이디입니다.");
            return "signup";
        } else {

            return "redirect:/login";
        }
    }

    // 로그인 폼 ( GET )
    @GetMapping("/login")
    public String loginForm(@RequestParam(required = false) String error, Model model) {

        if ( error != null ) {

            model.addAttribute("error", "로그인 실패 : 아이디 또는 비밀번호 확인");
        }
        // JSP 파일 이름 ( login.jsp )
        return "forward:/WEB-INF/views/login.jsp";
    }


    @GetMapping("/home")
    public String home() {

        // 인증된 사용자만 접근 ( 시큐리티가 보장 )
        return "forward:/WEB-INF/views/home.jsp";
    }

    @GetMapping("/")
    public String root() {

        return "redirect:/login";
    }

}
