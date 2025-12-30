// src/main/java/com/example/login_demo/jwt/JwtTokenProvider.java
package com.example.login_demo.jwt;

import com.example.login_demo.config.JwtProperties;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.util.Arrays;
import java.util.Collection;
import java.util.Date;
import java.util.stream.Collectors;

/**
 *
 * JWT를 실제로 만들고 해석하고 유효한지 뚫어보는 도구상자 ( 핵심 도구 )
 * @Component는 Bean으로 등록하여 의존성 주입 지원
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class JwtTokenProvider {

    // 설정값(비밀키, 만료시간 등)을 받아오는 구성요소
    private final JwtProperties jwtProperties;

    // 비밀키를 바이트 배열로 변환 후 HMAC 서명용 키 객체로 생성
    // 시큐리티 설정에서 Bean으로 등록한 키 주입
    private final SecretKey key;

    // 1. 토큰 생성 : 로그인 성공 시 권한 정보를 포함하여 생성
    public String createToken(String username, String role) {

        // 만료시간 설정 ( 현재시간 + 설정된 유효시간)
        Date now = new Date();
        Date expiryDate = new Date(now.getTime() + jwtProperties.getExpiration());


        // JWT 객체 생성기(토큰생성기)
        return Jwts.builder()
                // 토큰 제목( 사용자ID )
                .subject(username)
                // 사용자의 권한 정보 저장
                .claim("role", role)
                // 토큰 발행 시각
                .issuedAt(now)
                // 토큰 만료 시각
                .expiration(expiryDate)
                // 암호화키로 서명
                .signWith(key)
                // 최종 JWT 토큰 -> 문자열 반환
                .compact();
    }


    // 2. Authentication 객체 생성 : 시큐리티 컨텍스트에 담을 인증 객체 생성
    // 토큰을 해독해서 이 사용자는 누구고 어떤 권한이 있다는 것을 시큐리티에 알려줌
    public Authentication getAuthentication(String token) {

        // 토큰 내부의 PayLoad(데이터) 추출
        // Subject: 토큰의 "주인"을 식별하는 값 (UserId)
        // Claim: 그 외 부가 정보 (Role, Email 등)
        Claims claims = getClaims(token);

        //권한 정보가 있으면 List에 담기
        Collection<? extends GrantedAuthority> authorities =
                Arrays.stream(claims.get("role").toString().split(","))
                        .map(SimpleGrantedAuthority::new)
                        .collect(Collectors.toList());

        // 시큐리티 전용 User 객체 생성 ( 비밀번호는 인증이 끝났으므로 빈 값 )
        User principal = new User(claims.getSubject(),"",authorities);

        // UsernamePasswordAuthenticationToken 반환 (이후 시큐리티가 로그인 상태로 인식함)
        return new UsernamePasswordAuthenticationToken(principal, token, authorities);
    }


    // 공통 메서드 : 토큰에서 데이터(Claims) 추출
    private Claims getClaims(String token) {

        return Jwts.parser()
                // 서명 검증
                .verifyWith(key)
                .build()
                // 토큰 파싱
                .parseSignedClaims(token)
                // 내용(Payload) 반환 ( 토큰의 본문 데이터를 가져옴)
                .getPayload();
    }


    // 유효성 검사 ( 토큰의 변조 및 만료 여부 확인 )
    public boolean validateToken(String token) {
        try {

            // 서명, 만료, 형식등이 유효하면 정상 처리
            Jwts.parser().verifyWith(key).build().parseSignedClaims(token);
            return true;
        } catch (Exception e) {
            // ExpiredJwtException, MalformedJwtException 등을 한 번에 캐치
            return false;
        }
    }

    // username 추출
    // JwtAuthenticationFilter에서 사용
    public String getUsernameFromToken(String token) {
        try {
            Claims claims = Jwts.parser()
                    .verifyWith(key)
                    .build()
                    .parseSignedClaims(token)
                    .getPayload();

            return claims.getSubject();

        } catch (io.jsonwebtoken.ExpiredJwtException e) {
            // 토큰이 만료된 경우
            log.warn("만료된 JWT 토큰입니다.");
            throw e; // 혹은 null 반환 후 필터에서 처리
        } catch (io.jsonwebtoken.security.SignatureException e) {
            // 서명이 일치하지 않는 경우 (변조 가능성)
            log.error("잘못된 JWT 서명입니다.");
            throw e;
        } catch (Exception e) {
            // 그 외 포맷 오류 등
            log.error("지원되지 않는 JWT 토큰입니다.");
            throw e;
        }
    }
}