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

    // MusicalDetailPage용 쿼리
    @Query("SELECT p FROM Performance p " +
           "JOIN FETCH p.musical m " +
           "JOIN FETCH p.venue v " +
           "WHERE m.id = :musicalId")
    List<Performance> findByMusicalIdWithFetch(@Param("musicalId") Long musicalId);
    
    // BookingPage용 쿼리
    // (JOIN FETCH로 N+1 문제 해결)
    @Query("SELECT p FROM Performance p " +
           "JOIN FETCH p.musical m " +
           "JOIN FETCH p.venue v " +
           "LEFT JOIN FETCH p.performanceSeats ps " +   //  (1) 공연 좌석들
           "LEFT JOIN FETCH ps.seat s " +               // (2) 좌석 템플릿 (좌표가 여기 있음)
           "LEFT JOIN FETCH s.venue sv " +              // (3)좌석 템플릿의 공연장
           "WHERE p.id = :performanceId")
    Optional<Performance> findByIdWithFetch(@Param("performanceId") Long performanceId);
    // --- 👆👆👆 ---
}