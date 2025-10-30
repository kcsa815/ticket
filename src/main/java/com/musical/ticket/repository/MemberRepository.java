package com.musical.ticket.repository;

import com.musical.ticket.domain.Member;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

//DB CRUD담당 파일
// JpaRepository<[관리할 엔티티], [엔티티의 PK타입]>
public interface MemberRepository extends JpaRepository<Member, Long>{
    
    //spring data Jpa가 매서드 이름을 분석해서 쿼리를 자동 생성함.
    //"SELECT * FROM member WHERE username = ?"
    Optional<Member> findByUsername(String username);
}
