// src/main/java/com/example/login_demo/jwt/JwtAuthenticationFilter.java
package com.example.login_demo.jwt;

import com.example.login_demo.config.JwtProperties;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

/**
 *
 * 모든 요청에서 토큰을 검사하는 관문
 * OncePerRequestFilter를 상속받아 모든 요청마다 딱 한 번씩만 체크하는 필터
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    // 설정
    private final JwtProperties jwtProperties;
    // 토큰 검증 도구
    private final JwtTokenProvider jwtTokenProvider;
    // DB 조회를 위한 서비스
    private final UserDetailsService userDetailsService;

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {

        // 1. 요청 헤더에서 Authorization값 추출 - JWT 토큰을 꺼내 유효성 검사 시작
        String header = request.getHeader(jwtProperties.getHEADER_STRING());


        // 2. 토큰이 존재하고 접두사(Bearer )가 올바른지 확인
        if (StringUtils.hasText(header) && header.startsWith(jwtProperties.getTOKEN_PREFIX())) {

            // "Bearer " 뒤의 실제 토큰 문자열만 추출
            String token = header.substring(jwtProperties.getTOKEN_PREFIX().length());

            try {

                // 3. 토큰 유효성 검사 (서명 일치 여부, 만료 여부 등)
                if (jwtTokenProvider.validateToken(token)) {

                    // 4. 보안 핵심: 토큰에서 사용자 아이디를 추출하여 DB 실시간 조회
                    // 이 과정을 통해 탈퇴한 회원이나 정지된 계정을 즉시 차단 가능
                    String username = jwtTokenProvider.getUsernameFromToken(token);
                    UserDetails userDetails = userDetailsService.loadUserByUsername(username);

                    // 5. 사용자가 정상적인 상태(활성화, 잠금안됨 등)인지 한 번 더 체크
                    if (userDetails != null && userDetails.isEnabled()) {

                        // 6. 시큐리티 전용 인증 객체 생성
                        UsernamePasswordAuthenticationToken authentication =
                                new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());

                        // 7. 요청의 부가 정보(IP, 세션 ID 등)를 인증 객체에 저장
                        authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));

                        // 8. SecurityContextHolder에 인증 객체 등록
                        // 이 시점부터 Controller 등에서 로그인된 사용자로 인식함
                        SecurityContextHolder.getContext().setAuthentication(authentication);

                        log.info("JWT 인증 성공: {}", username);
                    }
                }
            } catch (Exception e) {

                // 검증 중 에러 발생 시(변조된 토큰 등) 컨텍스트를 비워 보안 유지
                SecurityContextHolder.clearContext();
                log.error("JWT 인증 실패: {}", e.getMessage());
            }
        }

        // 9. 다음 필터 체인으로 요청을 전달
        // 토큰이 없어도 다음으로 넘어가야 permitAll() 된 경로로 접근 가능함
        filterChain.doFilter(request, response);
    }
}