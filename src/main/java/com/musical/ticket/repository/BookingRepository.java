package com.musical.ticket.repository;
//Booking 엔티티를 저장하고, 나중에 "내 예매 내역 조회"를 하기 위해 필요함(save -> findByUser)         

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import com.musical.ticket.domain.entity.Booking;
import com.musical.ticket.domain.entity.User;

import jakarta.persistence.LockModeType;

@Repository
public interface BookingRepository extends JpaRepository<Booking, Long>{
    
    // *내 예매 내역 조회* 기능을 위한 커스텀 쿼리
    List<Booking> findByUserOrderByCreatedAtDesc(User user);

    @Query("SELECT b FROM Booking b " + 
           "JOIN FETCH b.user u " + 
           "JOIN FETCH b.performance p " + 
           "JOIN FETCH p.musical m " +
           "JOIN FETCH p.venue v " + 
           "LEFT JOIN FETCH b.performanceSeats ps " + 
           "LEFT JOIN FETCH ps.seat s " + 
           "LEFT JOIN FETCH s.venue v_seat " +
           "WHERE b.user = :user " + 
           "ORDER BY b.createdAt DESC")
    List<Booking> findByUserWithFetch(@Param("user") User user);


    //예매 취소를 위해 Booking ID로 조회 시 비관적 락을 검
    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("SELECT b FROM Booking b " + 
           "LEFT JOIN FETCH b.performanceSeats ps " + // (취소 시 좌석도 필요하므로)
           "LEFT JOIN FETCH ps.seat s " +
           "WHERE b.id = :bookingId")
    Optional<Booking> findByIdWithPessimisticLock(@Param("bookingId") Long bookingId);
}
