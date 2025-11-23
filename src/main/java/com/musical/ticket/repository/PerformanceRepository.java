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
           "LEFT JOIN FETCH p.performanceSeats ps " + // (공연 좌석)
           "LEFT JOIN FETCH ps.seat s " +             //  (좌석 템플릿 - 여기에 좌표가 있음)
           "WHERE p.id = :performanceId")
    Optional<Performance> findByIdWithFetch(@Param("performanceId") Long performanceId);

    // 지도 UI용
    @Query("SELECT p FROM Performance p " +
              "JOIN FETCH p.musical m " +
              "JOIN FETCH p.venue v " +
              "WHERE v.region = :region")
       List<Performance> findByVenueRegionWithFetch(@Param("region") String region);

    List<Performance> findByMusicalId(Long musicalId);

}