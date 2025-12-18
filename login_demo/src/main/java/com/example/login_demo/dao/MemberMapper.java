package com.example.login_demo.dao;

import com.example.login_demo.vo.MemberVO;
import org.apache.ibatis.annotations.Mapper;

import java.util.Optional;

@Mapper
public interface MemberMapper {

    // 1. 로그인 사용자 확인
    Optional<MemberVO> selectLoginUser(MemberVO memberVO);
}
