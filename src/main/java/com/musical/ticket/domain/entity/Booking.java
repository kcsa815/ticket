package com.musical.ticket.domain.entity;

import java.util.ArrayList;
import java.util.List;

import com.musical.ticket.domain.enums.BookingStatus;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "booking")
public class Booking extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "booking_id")
    private Long id;

    //부킹(M) : 유저(1) 관계
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    // 부킹(M) : 공연(1) 관계
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "performance_id", nullable = false)
    private Performance performance;

    @Enumerated(EnumType.STRING)
    @Column(name = "booking_status", nullable = false)
    private BookingStatus bookingStatus;

    @Column(name = "total_price", nullable = false)
    private Integer totalPrice;

    //예약(1) : 공연좌석(N)
    @OneToMany(mappedBy = "booking")
    private List<PerformanceSeat> performanceSeats = new ArrayList<>();

    @Builder
    public Booking(User user, Performance performance, BookingStatus bookingStatus, Integer totalPrice) {
        this.user = user;
        this.performance = performance;
        this.bookingStatus = bookingStatus;
        this.totalPrice = totalPrice;
    }

    //예매 취소 로직 (연관된 좌석 모두 취소)
    public void cancelBooking() {
        //1. 예매 상태 변경
        this.bookingStatus = BookingStatus.CANCELED;

        //2. 이 예매에 연결된 모든 좌석들의 상태를 *예약취소*로 변경
        for (PerformanceSeat performanceSeat : new ArrayList<>(this.performanceSeats)) {
            performanceSeat.cancelBooking();
        }
    }
    
}
