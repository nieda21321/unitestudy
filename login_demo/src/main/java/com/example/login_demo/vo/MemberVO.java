package com.example.login_demo.vo;


import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class MemberVO {

    private String userId;
    private String userPw; // 해시값 비밀번호
    private String userNm;
    private String userEmail;
    private String userStatus;
    private Integer userLoginFailCnt;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime userLogindate;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime userCreatedate;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime userUpdatedate;


    //================================================================
    // 시큐리티 사용 시 비밀번호 변경기능 시 평문용 비밀번호 체크 두 변수 필요
    //================================================================
    // 기존 평문 비밀번호
    private String userOldPw;
    // 새로운 평문 비밀번호
    private String userNewPw;
}
