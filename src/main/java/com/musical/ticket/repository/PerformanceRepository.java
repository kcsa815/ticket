package com.musical.ticket.repository;
// performance테이블에 접근하며, musicalId로 회차목록을 찾는 커스텀 쿼리를 포함함

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.musical.ticket.domain.entity.Performance;

@Repository
public interface PerformanceRepository extends JpaRepository<Performance, Long>{
    
    // (커스텀 쿼리) 특정 뮤지컬 ID로 모든 공연 회차 목록 조회
    // 예 : "SELECT * FROM performance WHERE musical_id = ?"
    List<Performance> findByMusicalId(Long musicalId);
}
