package com.example.login_demo.service.member;

import com.example.login_demo.vo.MemberVO;

public interface MemberService {

    // 1. 회원가입
    boolean insertMember(MemberVO memberVO);

    // 3. 비밀번호 수정
    boolean updateMemberPassword(MemberVO memberVO);
}
