package com.musical.ticket.repository;
import java.util.List;

/*findAllByIdWithPessimisticLock : Booking API의 핵심
    @Lock(LockModeType.PESSIMISTIC_WRITE) : 비관적 락
     작동 원리
         1. 사용자A가 [1, 2, 3] 번 좌석을 예매하기 위해 이 쿼리를 호출
         2. DB는 SELECT * FROM performance_seat WHERE id IN(1, 2, 3) FOR UPDATE
         3. DB는 이 3개의 레코드(ROW)에 ROCK을 검
         4. 만약 동시에 사용자B가 [3, 4, 5]번 좌석을 예매하려고 하면, B의 쿼리는 3번에 ROCK이 걸려있기 때문에,
            A의 트랜잭션(작업)이 끝날 때까지 *대기*함
        5. A가 예매를 완료(커밋) 하거나 실패(Rollback)하여 ROCK를 풀면, B의 쿼리가 그제서야 실행됨
    결론 : FOR UPDATE를 통해, DB레벨에서 데이터 접근을 *선점*하여 동시성 문제를 원천적으로 차단함
*/ 
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.musical.ticket.domain.entity.PerformanceSeat;

import jakarta.persistence.LockModeType;

@Repository
public interface PerformanceSeatRepository extends JpaRepository<PerformanceSeat, Long>{

    // 동시성 제어를 위해 ID 리스트로 좌석들을 조회하며 비관적락을 검
    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("SELECT ps FROM PerformanceSeat WHERE ps.id IN :ids")
    List<PerformanceSeat> findAllByIdWithPessimisticLock(@Param("ids")List<Long> ids);
}
