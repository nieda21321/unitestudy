package com.example.login_demo.vo;

import lombok.Data;

@Data
public class AuthVO {

    private String userId; // 연계키 값
    private String authDetailCd;
    private String authDetailNm;
    private String authCd;
    private String authNm;
}
