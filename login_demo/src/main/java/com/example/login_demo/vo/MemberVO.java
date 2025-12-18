package com.example.login_demo.vo;


import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class MemberVO {

    private String userId;
    private String userPw;
    private String userNm;
    private String userEmail;
    private String userStatus;
    private String userLoginFailCnt;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime userLogindate;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime userCreatedate;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime userUpdatedate;





    private boolean enabled;

}
