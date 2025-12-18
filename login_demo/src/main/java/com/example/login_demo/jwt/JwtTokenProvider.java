// src/main/java/com/example/login_demo/jwt/JwtTokenProvider.java
package com.example.login_demo.jwt;

import com.example.login_demo.config.JwtProperties;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.util.Date;

// @Component는 Bean으로 등록하여 의존성 주입 지원
@Component
public class JwtTokenProvider {

    // 비밀키를 바이트 배열로 변환 후 HMAC 서명용 키 객체로 생성
    private final SecretKey key;

    // 설정값(비밀키, 만료시간 등)을 받아오는 구성요소
    private final JwtProperties jwtProperties;

    @Autowired
    public JwtTokenProvider(JwtProperties jwtProperties) {

        this.jwtProperties = jwtProperties;
        this.key = Keys.hmacShaKeyFor(jwtProperties.getSecret().getBytes());
    }

    // 토큰 생성
    public String createToken(String username) {

        Date now = new Date();
        Date expiryDate = new Date(now.getTime() + jwtProperties.getExpiration());


        // JWT 객체 생성기
        return Jwts.builder()
                // 사용자명 (subject) 지정
                .subject(username)
                // 발급 시각 지정
                .issuedAt(now)
                // 만료 시각 지정
                .expiration(expiryDate)
                // 서명용 키로 암호화
                .signWith(key)
                // 최종 JWT 토큰 -> 문자열 반환
                .compact();
    }

    // username 추출
    public String getUsernameFromToken(String token) {

        // 전달받은 JWT 토큰에서 내부의 클레임을 파싱해 subject(사용자명)을 반환
        // 서명키로 토큰의 서명을 검증 파싱
        Claims claims = Jwts.parser()
                .verifyWith(key)
                .build()
                .parseSignedClaims(token)
                .getPayload();
        return claims.getSubject();
    }

    // 유효성 검사
    public boolean validateToken(String token) {
        try {

            // 서명, 만료, 형식등이 유효하면 정상 처리
            Jwts.parser().verifyWith(key).build().parseSignedClaims(token);
            return true;
        } catch (Exception e) {

            return false;
        }
    }
}