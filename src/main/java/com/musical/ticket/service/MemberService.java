package com.musical.ticket.service;

import com.musical.ticket.domain.Member;

import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.musical.ticket.domain.Role;
import com.musical.ticket.repository.MemberRepository;

import lombok.RequiredArgsConstructor;

@Service
@Transactional //이 클래스의 매서드들은 트랜잭션 안에서 동작
@RequiredArgsConstructor //final 필드에 대한 생성자를 자동 생성
public class MemberService implements UserDetailsService{

    private final MemberRepository memberRepository;
    private final PasswordEncoder passwordEncoder; //SecurityConfig에서 등록한 Bean주입

    //회원가입 로직
    public Member join(String username, String password, String name){

        // 1. 중복 아이디 검서
        memberRepository.findByUsername(username).ifPresent(member ->{
            throw new IllegalArgumentException("이미 존재하는 아이디입니다.");
        });

        //2. 비밀번호 암호화
        String encodedPassword = passwordEncoder.encode(password);

        //3. 엔티티 생성(기본권한 : USER)
        Member member = Member.builder()
            .username(username)
            .password(encodedPassword)
            .name(name)
            .role(Role.USER)
            .build();

        //4. db에 저장(회원가입)
        return memberRepository.save(member);
    }

    //2. UserDetailsService의 핵심 메서드 구현
    /*
    *Spring Security 가 username을 기반으로 사용자의 정볼보를 DB에서 조회
    *@param username(로그인 시 입력한 아이디)
    *@return UserDetails (Spring Sequrity가 사용하는 사용자 정보 객체)
     */
    @Override
    @Transactional(readOnly = true) //읽기 전용 트랜잭션
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException{
        // 1. DB 에서 회원 정보 조회
        Member member = memberRepository.findByUsername(username)
            .orElseThrow(() -> new UsernameNotFoundException("아이디를 찾을 수 없습니다 : " +username));
        
        //2. Spring Security의 User 객체로 변환하여 반환
        return User.builder()
            .username(member.getUsername())
            .password(member.getPassword())
            .roles(member.getRole().name()) //USER, ADMIN 등
            .build();
        
    }
    
}
