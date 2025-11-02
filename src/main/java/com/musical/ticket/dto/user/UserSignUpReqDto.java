package com.musical.ticket.dto.user;

//회원가입 요청 dto
import com.musical.ticket.domain.entity.User;
import com.musical.ticket.domain.enums.UserRole;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter // Controller에서 RequestBody로 받을 때 기본 생성자와 Setter가 필요할 수 있음
@NoArgsConstructor
public class UserSignUpReqDto {

    @NotBlank(message = "이메일은 필수 입력 항목입니다.")
    @Email(message = "유효한 이메일 형식이 아닙니다.")
    private String email;

    @NotBlank(message = "비밀번호는 필수 입력 항목입니다.")
    @Size(min = 8, message = "비밀번호는 8자 이상이어야 합니다.")
    private String password;

    @NotBlank(message = "사용자 이름은 필수 입력 항목입니다.")
    private String username;

    @Builder
    public UserSignUpReqDto(String email, String password, String username) {
        this.email = email;
        this.password = password;
        this.username = username;
    }

    // DTO를 Entity로 변환하는 메서드 (Service에서 사용)
    // 비밀번호 암호화는 Service 레이어에서 처리할 것
    public User toEntity() {
        return User.builder()
                .email(this.email)
                .password(this.password) // 암호화 전 비밀번호
                .username(this.username)
                .role(UserRole.ROLE_USER) // 기본값 USER
                .build();
    }
}