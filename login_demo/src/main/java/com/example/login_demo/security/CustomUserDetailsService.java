package com.example.login_demo.security;

import com.example.login_demo.service.MemberService;
import com.example.login_demo.vo.MemberVO;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

// 사용자 인증을 위해 DB에서 사용자 정보를 조회하는 역할
// UserDetailsService 인터페이스를 구현한 클래스
// 로그인 아이디로 회원 정보를 찾아내고, 없으면 에러를 던지는 역할
// 스프링 시큐리티가 CustomUserDetailsService를 호출하고, 반환된 CustomerUserDetails를 사용한다.
@Service
public class CustomUserDetailsService implements UserDetailsService {

    private final MemberService memberService;

    public CustomUserDetailsService(MemberService memberService) {

        this.memberService = memberService;
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {

        MemberVO member = memberService.selectById(username);

        System.err.println("=== loadUserByUsername ===");
        System.err.println("username: " + username);
        System.err.println("member 객체: " + member);
        System.err.println("memberPw: " + (member != null ? member.getMemberPw() : "NULL"));

        if (member == null) {

            throw new UsernameNotFoundException("User not found: " + username);
        }

        // 스프링 시큐리티 프레임워크가 자동으로 이 정보를 받아 처리한다
        // 해당 반환값을 통해 비밀번호 일치 검사 및 권한 체크 등을 수행한다
        return new CustomerUserDetails(member);
    }
}
