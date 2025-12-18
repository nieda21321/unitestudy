// src/main/java/com/example/login_demo/jwt/JwtAuthenticationFilter.java
package com.example.login_demo.jwt;

import com.example.login_demo.config.JwtProperties;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    @Autowired private JwtTokenProvider jwtTokenProvider;
    @Autowired private UserDetailsService userDetailsService;

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {

        // 요청 헤더에서 JWT 토큰을 꺼내 유효성 검사 시작
        String header = request.getHeader(JwtProperties.HEADER_STRING);

        // 헤더가 존재하고 "Bearer "로 시작하는지 확인하여 JWT 토큰이 포함되었는지 체크합니다.
        if (header != null && header.startsWith(JwtProperties.TOKEN_PREFIX)) {

            String token = header.substring(7);

            if (jwtTokenProvider.validateToken(token)) {

                String username = jwtTokenProvider.getUsernameFromToken(token);
                UserDetails userDetails = userDetailsService.loadUserByUsername(username);

                // 조회한 사용자 정보를 이용해 Spring Security가 인식하는 인증 객체를 생성합니다. 비밀번호 대신 null을 넣고, 권한 정보도 함께 포함합니다.
                UsernamePasswordAuthenticationToken authentication =
                        new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());

                // 인증 객체에 요청 관련 추가 정보를 세팅합니다(예: 접속 아이피).
                authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));

                // 인증 객체를 현재 스레드의 보안 컨텍스트에 저장하여 인증 상태임을 명확히 합니다. 이후 인증된 사용자로 요청 처리가 됩니다.
                SecurityContextHolder.getContext().setAuthentication(authentication);
            }
        }

        filterChain.doFilter(request, response);
    }
}