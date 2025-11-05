package com.musical.ticket.repository;
//performance_seat테이블에 대한 JpaRepository
// performance_seat테이블에 접근, saveAll(일괄저장)기능을 주로 사용함

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.musical.ticket.domain.entity.PerformanceSeat;

@Repository
public interface PerformanceSeatRepository extends JpaRepository<PerformanceSeat, Long>{
    //JpaRepository의 saveAll() 메서드를 사용하여 대량 삽입(Bulk Insert)을 처리
}
