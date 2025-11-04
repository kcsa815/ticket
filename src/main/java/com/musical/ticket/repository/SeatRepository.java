package com.musical.ticket.repository;
// Seat엔티티(DB의 seat테이블)에 대한 CRUD작업을 수행한다
// VenueService에서 좌석 탬플릿을 저장할 때(saveAll) 사용되는 레파지토리

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.musical.ticket.domain.entity.Seat;

@Repository
public interface SeatRepository extends JpaRepository<Seat, Long>{
    
}
