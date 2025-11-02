package com.musical.ticket.service;
// 사용자 비지니스 로직 담당
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.musical.ticket.config.jwt.JwtTokenProvider;
import com.musical.ticket.domain.entity.User;
import com.musical.ticket.domain.enums.UserRole;
import com.musical.ticket.dto.security.TokenDto;
import com.musical.ticket.dto.user.UserLoginReqDto;
import com.musical.ticket.dto.user.UserResDto;
import com.musical.ticket.dto.user.UserSignUpReqDto;
import com.musical.ticket.handler.exception.CustomException;
import com.musical.ticket.handler.exception.ErrorCode;
import com.musical.ticket.repository.UserRepository;
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
    @Transactional // 쓰기 작업(C,U,D)에는 별도로 @Transactional을 붙여 읽기 전용을 덮어씀
    public UserResDto signUp(UserSignUpReqDto reqDto) {
        // 1. 이메일 중복 체크
        if (userRepository.existsByEmail(reqDto.getEmail())) {
            throw new CustomException(ErrorCode.EMAIL_ALREADY_EXISTS);
        }

        // 2. 비밀번호 암호화
        String encodedPassword = passwordEncoder.encode(reqDto.getPassword());

        // 3. Entity 생성 (암호화된 비밀번호 사용)
        User user = User.builder()
                .email(reqDto.getEmail())
                .password(encodedPassword) // 암호화된 비밀번호
                .username(reqDto.getUsername())
                .role(UserRole.ROLE_USER) // 기본값 USER
                .build();

        // 4. DB에 저장
        User savedUser = userRepository.save(user);

        // 5. Entity -> DTO로 변환하여 Controller에 반환
        return new UserResDto(savedUser);
    }

    /**
     * 로그인 (R)
     * JWT 토큰을 발급하여 반환
     */
    @Transactional
    public TokenDto login(UserLoginReqDto reqDto) {
        try {
            // 1. email, password 기반으로 AuthenticationToken 객체 생성
            UsernamePasswordAuthenticationToken authenticationToken =
                    new UsernamePasswordAuthenticationToken(reqDto.getEmail(), reqDto.getPassword());

            // 2. 실제 검증 (비밀번호 대조)
            //    CustomUserDetailsService의 loadUserByUsername 메서드가 실행됨
            //    (내부적으로 passwordEncoder.matches()가 실행됨)
            Authentication authentication = authenticationManager.authenticate(authenticationToken);

            // 3. 인증 정보를 기반으로 JWT 토큰 생성
            TokenDto tokenDto = jwtTokenProvider.generateToken(authentication);

            // 4. 토큰 발급
            return tokenDto;

        } catch (AuthenticationException e) {
            // Spring Security의 기본 인증 실패 예외
            // 이메일이 없거나 비밀번호가 틀리면 발생
            
            // 이메일 존재 여부 확인
            if (!userRepository.existsByEmail(reqDto.getEmail())) {
                throw new CustomException(ErrorCode.USER_NOT_FOUND);
            }
            
            throw new CustomException(ErrorCode.PASSWORD_NOT_MATCH);
        }
    }

    /**
     * 회원 정보 단건 조회 (R)
     */
    public UserResDto getUserInfo(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new CustomException(ErrorCode.USER_NOT_FOUND));

        return new UserResDto(user);
    }
}
