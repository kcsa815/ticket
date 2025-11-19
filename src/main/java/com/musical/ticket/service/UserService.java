package com.musical.ticket.service;

import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.musical.ticket.config.jwt.JwtTokenProvider;
import com.musical.ticket.domain.entity.User;
import com.musical.ticket.dto.security.TokenDto;
import com.musical.ticket.dto.user.UserLoginReqDto;
import com.musical.ticket.dto.user.UserResDto;
import com.musical.ticket.dto.user.UserSignUpReqDto;
import com.musical.ticket.handler.exception.CustomException;
import com.musical.ticket.handler.exception.ErrorCode;
import com.musical.ticket.repository.UserRepository;
import com.musical.ticket.util.SecurityUtil;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;
    private final JwtTokenProvider jwtTokenProvider;

    // 회원가입
    @Transactional
    public UserResDto signUp(UserSignUpReqDto reqDto) {
        if (userRepository.existsByEmail(reqDto.getEmail())) {
            throw new CustomException(ErrorCode.EMAIL_ALREADY_EXISTS);
        }
        String encodedPassword = passwordEncoder.encode(reqDto.getPassword());
        
        User user = User.builder()
                .email(reqDto.getEmail())
                .password(encodedPassword)
                .username(reqDto.getUsername())
                .role(reqDto.toEntity().getRole())
                .build();

        return new UserResDto(userRepository.save(user));
    }

    // 로그인 (중복 제거됨, 오타 수정됨)
    @Transactional
    public TokenDto login(UserLoginReqDto reqDto) {
        UsernamePasswordAuthenticationToken authenticationToken =
                new UsernamePasswordAuthenticationToken(reqDto.getEmail(), reqDto.getPassword());

        try {
            // [수정!] AuthenticationManager에 전달되는 변수명 오타 수정
            Authentication authentication = authenticationManager.authenticate(authenticationToken); 
            return jwtTokenProvider.generateToken(authentication);
        } catch (AuthenticationException e) {
             if (!userRepository.existsByEmail(reqDto.getEmail())) {
                 throw new CustomException(ErrorCode.USER_NOT_FOUND);
             }
            throw new CustomException(ErrorCode.PASSWORD_NOT_MATCH);
        }
    }

    // 회원 정보 조회
    public UserResDto getUserInfoById(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new CustomException(ErrorCode.USER_NOT_FOUND));
        return new UserResDto(user);
    }

    // 내 정보 조회
    public UserResDto getMyInfo() {
        String userEmail = SecurityUtil.getCurrentUserEmail();
        User user = userRepository.findByEmail(userEmail)
                .orElseThrow(() -> new CustomException(ErrorCode.USER_NOT_FOUND));
        return new UserResDto(user);
    }
}