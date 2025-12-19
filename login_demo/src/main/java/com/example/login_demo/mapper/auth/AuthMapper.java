package com.example.login_demo.mapper.auth;

import com.example.login_demo.vo.AuthVO;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface AuthMapper {

    // 1. 사용자 권한 확인
    List<AuthVO> selectMemeberAuthById(String userId);

}
