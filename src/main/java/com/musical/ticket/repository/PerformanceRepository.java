package com.musical.ticket.repository;

import com.musical.ticket.domain.entity.Performance;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query; 
import org.springframework.data.repository.query.Param; 
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional; 

@Repository
public interface PerformanceRepository extends JpaRepository<Performance, Long> {

    // [수정!] "JOIN FETCH"를 사용하여 연관된 엔티티를 '즉시 로딩'합니다.
    @Query("SELECT p FROM Performance p " +
           "JOIN FETCH p.musical m " +
           "JOIN FETCH p.venue v " +
           "WHERE m.id = :musicalId")
    List<Performance> findByMusicalIdWithFetch(@Param("musicalId") Long musicalId);
    
    // [추가!] 2일차(BookingPage)에서도 100% 같은 에러가 날 것이므로, 미리 수정합니다.
    @Query("SELECT p FROM Performance p " +
           "JOIN FETCH p.musical m " +
           "JOIN FETCH p.venue v " +
           "LEFT JOIN FETCH p.performanceSeats ps " + // 좌석이 없어도 공연은 나와야 하므로 LEFT JOIN
           "LEFT JOIN FETCH ps.seat s " +
           "WHERE p.id = :performanceId")
    Optional<Performance> findByIdWithFetch(@Param("performanceId") Long performanceId);
}