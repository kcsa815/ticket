package com.musical.ticket.repository;
//Booking 엔티티를 저장하고, 나중에 "내 예매 내역 조회"를 하기 위해 필요함(save -> findByUser)         

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import com.musical.ticket.domain.entity.Booking;
import com.musical.ticket.domain.entity.User;

@Repository
public interface BookingRepository extends JpaRepository<Booking, Long>{
    
    // *내 예매 내역 조회* 기능을 위한 커스텀 쿼리
    List<Booking> findByUserOrderByCreatedAtDesc(User user);
}
