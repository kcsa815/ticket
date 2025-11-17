package com.musical.ticket.repository;

import com.musical.ticket.domain.entity.Venue;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface VenueRepository extends JpaRepository<Venue, Long> {

    // --- 👇 [신규!] N+1 문제 해결용 JOIN FETCH ---
    @Query("SELECT v FROM Venue v " +
           "LEFT JOIN FETCH v.seats s " + // 👈 (좌석 템플릿 포함)
           "WHERE v.id = :venueId")
    Optional<Venue> findByIdWithFetch(@Param("venueId") Long venueId);
    // --- 👆 ---
}