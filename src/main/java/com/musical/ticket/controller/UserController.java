package com.musical.ticket.controller;
//클라이언트의 요청을 받는 사용자 컨트롤러

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import com.musical.ticket.dto.security.TokenDto;
import com.musical.ticket.dto.user.UserLoginReqDto;
import com.musical.ticket.dto.user.UserResDto;
import com.musical.ticket.dto.user.UserSignUpReqDto;
import com.musical.ticket.service.UserService;
import org.springframework.web.bind.annotation.GetMapping;

@RestController // "이 클래스는 HTTP 응답(JSON)을 반환하는 Controller입니다"
@RequestMapping("/api/users") // 이 컨트롤러의 모든 메서드는 /api/users 경로로 시작
@RequiredArgsConstructor
public class UserController {

    private final UserService userService; // Controller는 Service에만 의존

    /**
     * 회원가입 (C)
     * [POST] /api/users/signup
     */
    @PostMapping("/signup")
    public ResponseEntity<UserResDto> signup(@Valid @RequestBody UserSignUpReqDto userSignUpReqDto) {
        // @Valid: DTO에 설정한 Validation(@NotBlank 등)을 검사
        // @RequestBody: HTTP 요청의 Body(JSON)를 DTO 객체로 변환
        
        UserResDto responseDto = userService.signUp(userSignUpReqDto);

        // ResponseEntity: HTTP 상태 코드(예: 201 Created)와 응답 데이터를 함께 반환
        return ResponseEntity.status(HttpStatus.CREATED).body(responseDto);
    }

   //로그인(R)
    @PostMapping("/login")
    public ResponseEntity<TokenDto> login(@Valid @RequestBody UserLoginReqDto userLoginReqDto) {
        TokenDto tokenDto = userService.login(userLoginReqDto);
        return ResponseEntity.ok(tokenDto);
    }

    // 내 정보 조회 (R)
    @GetMapping("/{userId}")
    public ResponseEntity<UserResDto> getUserInfo(@PathVariable Long userId) {
        // @PathVariable: URL 경로의 {userId} 값을 Long userId 변수에 바인딩

        UserResDto responseDto = userService.getUserInfoById(userId);
        
        return ResponseEntity.ok(responseDto);
    }

    //내 정보 조회(토큰 기반)
    @GetMapping("/me")
    public ResponseEntity<UserResDto> getMyInfo(){
        //SecurityConfig에서 /api/users/me 경로는 인증이 필요하도록 설정 돼 있어야 함
        UserResDto responseDto = userService.getMyInfo();
        return ResponseEntity.ok(responseDto);
    }
    
}
