package com.example.login_demo.dao;

import com.example.login_demo.vo.MemberVO;
import org.apache.ibatis.annotations.Mapper;

import java.util.Map;

@Mapper
public interface MemberMapper {

    int insertMember(MemberVO member);
    MemberVO selectById(String memberId);
    int existsById(String memberId);
}
