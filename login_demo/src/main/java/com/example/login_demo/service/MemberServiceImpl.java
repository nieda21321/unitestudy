package com.example.login_demo.service;

import com.example.login_demo.dao.MemberMapper;
import com.example.login_demo.vo.MemberVO;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class MemberServiceImpl implements MemberService {

    private final MemberMapper memberMapper;
    private final PasswordEncoder passwordEncoder;

    public MemberServiceImpl(MemberMapper memberMapper, PasswordEncoder passwordEncoder) {

        this.memberMapper = memberMapper;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public boolean register(MemberVO member) {

        System.err.println("register() 호출됨! ID: " + member.getMemberId());
        System.out.println("=== register() 진입 ===");
        System.out.println("memberId: " + member.getMemberId());
        System.out.println("memberPw: " + member.getMemberPw());
        System.out.println("memberName: " + member.getMemberName());
        System.out.println("email: " + member.getEmail());

        if (member.getMemberId() == null || member.getMemberId().isEmpty()) {
            System.out.println("아이디가 null입니다!");
            return false;
        }

        if (memberMapper.existsById(member.getMemberId()) > 0) {

            return false;
        }

        String encoded = passwordEncoder.encode(member.getMemberPw());
        member.setMemberPw(encoded);
        member.setEnabled(true);

        int result = memberMapper.insertMember(member);
        System.out.println("삽입 결과 : " + result);

        return  result > 0;
    }

    @Override
    public MemberVO selectById(String memberId) {
        return memberMapper.selectById(memberId);
    }

    @Override
    public boolean existById(String memberId) {
        return memberMapper.existsById(memberId) > 0;
    }
}
