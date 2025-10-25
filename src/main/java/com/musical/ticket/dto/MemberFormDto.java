package com.musical.ticket.dto;

import jakarta.validation.constraints.NotEmpty;
import lombok.Getter;
import lombok.Setter;

//회원가입 Data Transfer Object 생성
//사용자가 입력한 회원가입 폼 데이터를 컨트롤러로 전달함.
@Getter
@Setter
public class MemberFormDto {

   @NotEmpty(message = "아이디는 필수 항목입니다.")
    private String username;

    @NotEmpty(message = "비밀번호는 필수 항목입니다.")
    private String password;

    @NotEmpty(message = "이름은 필수 항목입니다.")
    private String name;
    
}
