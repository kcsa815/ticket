package com.musical.ticket.repository;

import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import com.musical.ticket.domain.entity.Musical;

@Repository
public interface MusicalRepository extends JpaRepository<Musical, Long>{
    //jpaRepository가 save, findById, findAll, deleteByid 등을 제공함
    List<Musical> findByCategory(String category);
}
