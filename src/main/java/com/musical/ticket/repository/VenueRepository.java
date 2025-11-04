package com.musical.ticket.repository;
// Venue엔티티(DB의 venue테이블)에 대한 CRUD직업을 수행하기 위해 JpaRepository를 상속받는다

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import com.musical.ticket.domain.entity.Venue;


@Repository
public interface VenueRepository extends JpaRepository<Venue, Long>{
    
}
