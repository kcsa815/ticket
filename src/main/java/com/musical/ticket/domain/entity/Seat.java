package com.musical.ticket.domain.entity;

import com.musical.ticket.dto.AdminMusicalRegisterDto.SeatGrade;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Builder;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "seat", uniqueConstraints = {
    @UniqueConstraint(columnNames = {"performance_id", "seat_number"}) //복합 유니크 키
})
public class Seat extends BaseTimeEntity {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "seat_id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "performance_id", nullable = false)
    private Performance performance;

    @Enumerated(EnumType.STRING)
    @Column(name = "seat_grade", nullable = false)
    private SeatGrade seatGrade;

    @Column(name = "seat_number", nullable = false)
    private String seatNumber;

    @Column(nullable = false)
    private Integer price;

    @Column(name = "is_reserved", nullable = false)
    private Boolean isReserved = false; //기본값 false

    @Builder
    public Seat(Performance performance, SeatGrade seatGrade, String seatNumber, Integer price) {
        this.performance = performance;
        this.seatGrade = seatGrade;
        this.seatNumber = seatNumber;
        this.price = price;
        this.isReserved = (isReserved != null) ? isReserved : false;
    }

    //좌석 예약 상태 변경 메서드
    public void reserve() {
        this.isReserved = true;
    }
    //좌석 예약 취소 메서드
    public void cancel() {
        this.isReserved = false;
    }
}
