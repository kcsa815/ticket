package com.musical.ticket.repository;

import com.musical.ticket.entity.Musical; //Entity 경로 확인
import org.springframework.data.jpa.repository.JpaRepository;

public interface MusicalRepository extends JpaRepository<Musical, Long>{
    //JpaRepository를 상속받기만 해도 save(), findById() 등의 핵심 CRUD매서드들이 자동으로 구현됨.
}
