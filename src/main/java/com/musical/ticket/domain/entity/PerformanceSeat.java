package com.musical.ticket.domain.entity;
import jakarta.persistence.Column;
//특정 공연 회차의 좌석 상태(가격, 예약 여부)를 관리함
/**
 * setBooking(Booking booking)메서드 수정 필요
 * <양방향 연관관계 편의 메서드>
 *  이 메서드가 PerformanceSeat의 booking필드(FK)를 설정할 때,
 *  반대편인 Booking 엔티티의 performanceSeats리스트에도 this(자기자신)을 추가해 줘야 함.
 */
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity 
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "performance_seat", uniqueConstraints = {@UniqueConstraint(columnNames = {"performance_id", "seat_id"})})
public class PerformanceSeat extends BaseTimeEntity{
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "performance_seat_id")
    private Long id;

    //공연 좌석(N) : 공연(1)
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "performance_id", nullable = false)
    private Performance performance;

    //공연 좌석(N) : 좌석탬플릿(1)
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "seat_id", nullable = false)
    private Seat seat;

    @Column(nullable = false)
    private Integer price;

    @Column(name = "is_reserved", nullable = false)
    private Boolean isReserved = false;

    //공연좌석(N) : 예약정보(1)
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "booking_id")
    private Booking booking;

    @Builder
    public PerformanceSeat(Performance performance, Seat seat, Integer price, Boolean isReserved){
        this.performance = performance;
        this.seat = seat;
        this.price = price;
        this.isReserved = (isReserved !=null)?isReserved:false;
    }

    //연관관계 편의 매서드(예매 시 사용)
    public void setBooking(Booking booking){
        this.booking = booking;
        this.isReserved = true;
        /* 중요!!
            양방향 연관관계 설정 : Booking엔티티 리스트에도 자기 자신을 추가해야됨!
            이렇게 해야 Service에서 new BookingResDto(savedBooking)시 savedBooking객체가 예매된 좌석목록 알 수 있음.
        */
        booking.getPerformanceSeats().add(this);
    }

    //예매 취소 매서드
    public void cancelBooking(){
        //양방향 연관관계 제거!!
        if(this.booking !=null){
            //1. Booking엔티티의 리스트에서 *나*를 제거
            this.booking.getPerformanceSeats().remove(this);
        }
        //2. 나의 상태를 *예약 가능*으로 변경
        this.booking = null;
        this.isReserved = false;
    }
}
