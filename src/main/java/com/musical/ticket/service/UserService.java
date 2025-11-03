package com.musical.ticket.service;

import org.springframework.security.core.Authentication;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
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

    // --- 5단계에서 추가된 의존성 ---
    private final AuthenticationManager authenticationManager;
    private final JwtTokenProvider jwtTokenProvider;

    // 회원가입 (C)
    @Transactional
    public UserResDto signUp(UserSignUpReqDto reqDto) {
        // 1. 이메일 중복 체크
        if (userRepository.existsByEmail(reqDto.getEmail())) {
            throw new CustomException(ErrorCode.EMAIL_ALREADY_EXISTS);
        }

        // 2. 비밀번호 암호화 (중요!)
        String encodedPassword = passwordEncoder.encode(reqDto.getPassword());
        
        // 3. DTO -> Entity 변환 (암호화된 비밀번호 사용)
        // [수정] 불필요한 user 객체 생성(39행) 및 toEntity() 중복 호출(46행) 제거
        User encodedUser = User.builder()
                .email(reqDto.getEmail())
                .password(encodedPassword) // 암호화된 비밀번호
                .username(reqDto.getUsername())
                .role(reqDto.toEntity().getRole()) // DTO의 toEntity()에서 기본 Role을 가져오기 위해 1회 호출
                .build();

        // 4. DB에 저장
        // [수정] userRepository.save(user) -> userRepository.save(encodedUser)
        User savedUser = userRepository.save(encodedUser);

        // 5. Entity -> DTO로 변환하여 Controller에 반환
        return new UserResDto(savedUser);
    }
    


     //로그인 (R)
    @Transactional
    public TokenDto login(UserLoginReqDto reqDto) {
        // 1. email, password 기반으로 AuthenticationToken 객체 생성
        UsernamePasswordAuthenticationToken authenticationToken =
                new UsernamePasswordAuthenticationToken(reqDto.getEmail(), reqDto.getPassword());

        try {
            // 2. 실제 검증 (비밀번호 대조)
            // [수정] org.springframework.security.core.Authentication 사용
            Authentication authentication = authenticationManager.authenticate(authenticationToken); 

            // 3. 인증 정보를 기반으로 JWT 토큰 생성
            TokenDto tokenDto = jwtTokenProvider.generateToken(authentication);

            // (선택사항: Refresh Token을 DB에 저장)

            // 4. 토큰 발급
            return tokenDto;

        } catch (AuthenticationException e) {
            // 6단계에서 추가한 CustomException으로 분기 처리
            if (!userRepository.existsByEmail(reqDto.getEmail())) {
                 throw new CustomException(ErrorCode.USER_NOT_FOUND);
            }
            
            // 이메일은 있으나, 비밀번호가 틀린 경우
            throw new CustomException(ErrorCode.PASSWORD_NOT_MATCH);
        }
    }

    // 회원 정보 단건 조회 (R)
    public UserResDto getUserInfoById(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new CustomException(ErrorCode.USER_NOT_FOUND));

        return new UserResDto(user);
    }

    //내 정보 조회(토큰 이용) SecurityUtil사용 ->현재 로그인한 사용자의 정보 가져옴.
    public UserResDto getMyInfo(){
        //1. SecurityUtil을 통해 현재 사용자의 이메일을 가져옴
        String userEmail = SecurityUtil.getCurrentUserEmail();

        //2. 이메일을 기반으로 DB 에서 User를 찾음
        User user = userRepository.findByEmail(userEmail).orElseThrow(()-> new CustomException(ErrorCode.USER_NOT_FOUND));

        //3. Dto로 변환하여 반환
        return new UserResDto(user);
    }


}