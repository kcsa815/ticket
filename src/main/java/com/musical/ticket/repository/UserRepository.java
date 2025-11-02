package com.musical.ticket.repository;
import java.util.Optional;
// DB에 접근할 사용자 repository
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import com.musical.ticket.domain.entity.User;

@Repository
public interface UserRepository extends JpaRepository<User, Long>{
    
    // 1. 이메일로 회원 찾기 (로그인 및 중복 체크 시 사용)
    // Optional<User> : User가 존재할 수도, 없을 수도 있음을 명시 (Null-safe)
    Optional<User> findByEmail(String email);

    // 2. 이메일 존재 여부 확인 (중복 체크용 최적화)
    boolean existsByEmail(String email);
}
