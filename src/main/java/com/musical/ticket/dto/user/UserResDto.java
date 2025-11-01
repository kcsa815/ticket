package com.musical.ticket.dto.user;
//회원정보 응답
import com.musical.ticket.domain.entity.User;
import com.musical.ticket.domain.enums.UserRole;
import lombok.Getter;

// 클라이언트에게 반환할 응답 DTO (절대 비밀번호 포함 X)
@Getter
public class UserResDto {
    private Long userId;
    private String email;
    private String username;
    private UserRole role;

    // Entity를 DTO로 변환하는 생성자 (Service에서 사용)
    public UserResDto(User user) {
        this.userId = user.getId();
        this.email = user.getEmail();
        this.username = user.getUsername();
        this.role = user.getRole();
    }
}
