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

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "booking_seat_id", nullable = false)
    private BookingStatus bookingStatus;

    @Column(name = "total_price", nullable = false)
    private Integer totalPrice;

    @OneToMany(mappedBy = "booking", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<BookingSeat> bookingSeats = new ArrayList<>();

    @Builder
    public Booking(User user, Performance performance, BookingStatus bookingStatus, Integer totalPrice) {
        this.user = user;
        this.performance = performance;
        this.bookingStatus = bookingStatus;
        this.totalPrice = totalPrice;
    }

    //예매 취소 매서드
    public void cancelBooking() {
        this.bookingStatus = BookingStatus.CANCELED;

        for (BookingSeat bookingSeat : this.bookingSeats) {
            ((Seat) bookingSeat.getSeat()).cancel();
        }
    }
    
}
