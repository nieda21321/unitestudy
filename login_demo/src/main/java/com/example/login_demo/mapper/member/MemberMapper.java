package com.example.login_demo.mapper.member;

import com.example.login_demo.vo.MemberVO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

@Mapper
public interface MemberMapper {

    // 1. 회원가입
    boolean insertMember(MemberVO memberVO);

    // 2. 로그인 사용자 확인
    MemberVO selectMemberById(String userId);

    // 3. 비밀번호 수정
    boolean updateMemberPassword(MemberVO memberVO);

    // 4. 로그인 실패 시 횟수 증가
    boolean increaseLoginFailCnt(String userId);

    // 5. 로그인 실패 시 계정 잠금 처리
    boolean lockMemberId(String userId);

    // 6. 계정 잠금 해제 처리
    boolean unlockMemberId(String userId);

    // 7. 로그인 성공 시 마다 누적 실패 횟수 초기화
    boolean resetLoginFailCnt(String userId);

}
