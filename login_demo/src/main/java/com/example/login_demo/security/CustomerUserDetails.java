package com.example.login_demo.security;

import com.example.login_demo.vo.AuthVO;
import com.example.login_demo.vo.MemberVO;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

// record는 자바 14에서 나온 것으로 데이터만 담는 불변 클래스
// UserDetails 는 security에서 제공하므로 따로 인터페이스를 생성할 필요 없다
// UserDetails 인터페이스를 직접 구현한 커스텀 사용자 정보 클래스
// 인증에 필요한 사용자 정보를 스프링 시큐리티에 맞게 포장하는 역할
// 한마디로 로그인한 사용자의 계정 상태 + 권한 정보 등을 검증 확인
@RequiredArgsConstructor
public class CustomerUserDetails implements UserDetails {

    private final MemberVO member;
    private final List<AuthVO> authList;

    // 사용자 권한 검증?
    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
       
        // ROLE 테이블 붙이는 곳
        List<GrantedAuthority> authorities = new ArrayList<>();

        // EX) 일반권한 == ROLE 권한 -- ADMIN, USER ...
        authList.stream()
                .map(AuthVO::getAuthCd)
                .distinct()
                .forEach(authCd -> authorities.add(
                        new SimpleGrantedAuthority("ROLE_" + authCd)
                ));

        // EX) 세부권한 == authority
        authList.stream()
                .map(AuthVO::getAuthDetailCd)
                .filter(authDetailCd -> authDetailCd != null)
                .forEach(authDetailCd -> authorities.add(
                        new SimpleGrantedAuthority(authDetailCd)
                ));

        // return List.of(); 권한 스킵
        return authorities;
    }

    // 사용자 비밀번호 검증?
    @Override
    public String getPassword() {

        return member.getUserPw();
    }

    // 여기선 Username 이라고 되어 있지만 실제론 userId로 검증
    // return 값은 userId로 체크해야함
    // 사용자 ID 검증?
    @Override
    public String getUsername() {

        return member.getUserId();
    }

    // 사용자 계정 사용 가능여부 검증
    // 10은 사용중
    @Override
    public boolean isEnabled() {

        return "10".equals(member.getUserStatus());
    }

    // 계정 만료 여부 검증 ( 계정 사용 기간 )
    @Override
    public boolean isAccountNonExpired() {

        // 이 값은 실제론 boolen 타입으로  true 값이 default로 검증 패스하겠다는 의미
        return UserDetails.super.isAccountNonExpired();
    }

    // 계정 잠김 여부 검증
    @Override
    public boolean isAccountNonLocked() {

        // 이 값은 실제론 boolen 타입으로  true 값이 default로 검증 패스하겠다는 의미
        // return UserDetails.super.isAccountNonLocked();
        
        // 계정 로그인 실패 횟수 누적 5회 이상시 잠금 처리 접속불가
        return member.getUserLoginFailCnt() < 5;
    }

    // 비밀번호 만료 여부 검증 ( 비밀번호 사용 기간 )
    @Override
    public boolean isCredentialsNonExpired() {

        // 이 값은 실제론 boolen 타입으로  true 값이 default로 검증 패스하겠다는 의미
        return UserDetails.super.isCredentialsNonExpired();
    }
}
