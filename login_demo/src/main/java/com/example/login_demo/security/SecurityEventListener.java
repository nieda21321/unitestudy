package com.example.login_demo.security;


import lombok.RequiredArgsConstructor;
import org.springframework.context.event.EventListener;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class SecurityEventListener {

    private final CustomUserDetailsService userDetailsService;

    @EventListener
    public void handlePasswordChange(PasswordChangedEvent event) {

        // 1. 최신 유저 정보 조회
        UserDetails userDetails = userDetailsService.loadUserByUsername(event.userId());

        // SecurityContextHolder 업데이트
        // 2. 인증 정보 갱신
        Authentication newAuth = new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
        SecurityContextHolder.getContext().setAuthentication(newAuth);
    }
}
