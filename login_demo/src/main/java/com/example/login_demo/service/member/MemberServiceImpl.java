package com.example.login_demo.service.member;

import com.example.login_demo.mapper.member.MemberMapper;
import com.example.login_demo.security.PasswordEncryptor;
import com.example.login_demo.vo.MemberVO;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor // private final 생성자를 만들어준다
public class MemberServiceImpl implements MemberService {


    private final MemberMapper memberMapper;
    private final PasswordEncryptor passwordEncryptor;

    // DB 핸들링 같은건 boolean 타입으로 던지는게 깔끔할 거 같음.
    // 회원가입 시 비밀번호 암호화로 저장
    @Override
    public boolean insertMember(MemberVO memberVO) {

        memberVO.setUserPw(
                passwordEncryptor.encrypt(memberVO.getUserPw())
        );

        return memberMapper.insertMember(memberVO);
    }

    // memberVO = userId, oldPw, newPw
    // 비밀번호 변경
    @Override
    public boolean updateMemberPassword(MemberVO memberVO) {

        MemberVO member = memberMapper.selectMemberById(memberVO.getUserId());

        // 사용자 검증 IllegalArgumentException 은 메서드에 전달된 인자가 잘못된 경우 사용
        // (null, 빈문자열, 범위 벗어난 값 등등 )
        if ( member == null ) {

            throw new IllegalArgumentException("해당 사용자가 존재하지 않습니다.");
        }

        // 기존 비밀번호 검증
        if(passwordEncryptor.matches(memberVO.getUserOldPw(), memberVO.getUserPw())) {

            throw new IllegalArgumentException("해당 사용자의 비밀번호가 불일치합니다.");
        }

        MemberVO updateMemberVO = new MemberVO();
        updateMemberVO.setUserPw(passwordEncryptor.encrypt(memberVO.getUserNewPw()));

        return memberMapper.updateMemberPassword(updateMemberVO);
    }
}
