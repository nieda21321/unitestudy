package com.example.login_demo.service;

import com.example.login_demo.vo.MemberVO;

public interface MemberService {

    boolean register(MemberVO member);
    MemberVO selectById(String memberId);
    boolean existById(String memberId);
}
