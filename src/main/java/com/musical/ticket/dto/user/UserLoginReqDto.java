package com.musical.ticket.dto.user;
//로그인 요청 dto
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class UserLoginReqDto {

    @NotBlank(message = "이메일은 필수 입력 항목입니다.")
    @Email
    private String email;

    @NotBlank(message = "비밀번호는 필수 입력 항목입니다.")
    private String password;
}
