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

       // "JOIN FETCH"를 사용하여 연관된 엔티티를 '즉시 로딩'
       @Query("SELECT p FROM Performance p " +
                     "JOIN FETCH p.musical m " +
                     "JOIN FETCH p.venue v " +
                     "WHERE m.id = :musicalId")
       List<Performance> findByMusicalIdWithFetch(@Param("musicalId") Long musicalId);

       // BookingPage용 쿼리
       @Query("SELECT p FROM Performance p " +
                     "JOIN FETCH p.musical m " +
                     "JOIN FETCH p.venue v " +
                     "LEFT JOIN FETCH p.performanceSeats ps " + // 좌석
                     "LEFT JOIN FETCH ps.seat s " +            //좌석 탬플릿
                     "WHERE p.id = :performanceId")
       Optional<Performance> findByIdWithFetch(@Param("performanceId") Long performanceId);
}