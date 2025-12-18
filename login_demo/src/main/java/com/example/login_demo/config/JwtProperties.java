package com.example.login_demo.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;


@Component
@ConfigurationProperties(prefix = "jwt")
public class JwtProperties {

    // 암호화 및 복호화에 사용할 비밀키
    // 토큰 서명/검증에 필요하며 외부 노출 X
    private String secret;

    // 토큰의 만료시간 (밀리초 단위)
    private long expiration;

    // getter & setter
    public String getSecret() { return secret; }
    public void setSecret(String secret) { this.secret = secret; }
    public long getExpiration() { return expiration; }
    public void setExpiration(long expiration) { this.expiration = expiration; }

    // 고정값
    // JWT 토큰 앞에 붙는 접두사
    // 일반적으로 HTTP Authorization 헤더에 사용
    public static final String TOKEN_PREFIX = "Bearer ";

    // JWT 토큰이 전달되는 HTTP 헤더의 이름
    public static final String HEADER_STRING = "Authorization";
}
