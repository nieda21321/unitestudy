package com.example.login_demo.security;

import com.example.login_demo.mapper.auth.AuthMapper;
import com.example.login_demo.mapper.member.MemberMapper;
import com.example.login_demo.vo.AuthVO;
import com.example.login_demo.vo.MemberVO;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.List;

// 사용자 인증을 위해 DB에서 사용자 정보를 조회하는 역할
// UserDetailsService 인터페이스를 구현한 클래스
// 로그인 아이디로 회원 정보를 찾아내고, 없으면 에러를 던지는 역할
// 스프링 시큐리티가 CustomUserDetailsService를 호출하고, 반환된 CustomerUserDetails를 사용한다.
@Service
@RequiredArgsConstructor
public class CustomUserDetailsService implements UserDetailsService {

    private final MemberMapper memberMapper;
    private final AuthMapper authMapper;


    // 스프링 시큐리티 로그인 연동
    @Override
    public UserDetails loadUserByUsername(String userId) throws UsernameNotFoundException {

        MemberVO member = memberMapper.selectMemberById(userId);

        if ( member == null ) {

            // 시큐리티 전용 exception 같음
            throw new UsernameNotFoundException("해당 사용자가 존재하지 않습니다.");
        }
        
        // 권한 리스트
        List<AuthVO> authList = authMapper.selectMemeberAuthById(userId);


        // Required type: UserDetails
        // Provided: String
        // 사용자 정보 조회 시 있을 경우
        return new CustomerUserDetails(member, authList);
    }
}
