package com.example.login_demo.security;


import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class PasswordEncryptor {

    private final PasswordEncoder passwordEncoder;

    // 패스워드 인코딩
    public String encrypt(String rawPw) {

        return passwordEncoder.encode(rawPw);
    }

    // 패스워드 일치 여부 체크 ( 평문 비밀번호, DB에 저장된 암호화 비밀번호 )
    public boolean matches(String rawPw, String encodePw) {

        return passwordEncoder.matches(rawPw, encodePw);
    }
}
