package com.example.login_demo.config;

import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * JWT 설정정보
 * 키 값이나 만료시간 변경 시 해당 파일만 수정하면됨
 */
@Data
@Component
@ConfigurationProperties(prefix = "jwt")
public class JwtProperties {

    // 암호화 및 복호화에 사용할 비밀키
    // 토큰 서명/검증에 필요하며 외부 노출 X
    private String secret;

    // 토큰의 만료시간 (밀리초 단위)
    private long expiration;

    // JWT 토큰이 전달되는 HTTP 헤더의 이름
    private String HEADER_STRING = "Authorization";

    // 고정값
    // JWT 토큰 앞에 붙는 접두사
    // 일반적으로 HTTP Authorization 헤더에 사용
    private String TOKEN_PREFIX = "Bearer ";
}
