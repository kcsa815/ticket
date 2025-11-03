package com.musical.ticket.domain.entity;
import java.util.ArrayList;
import java.util.List;

//좌석 탬플릿 역할을 하는 엔티티
import com.musical.ticket.domain.enums.SeatGrade;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Builder;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "seat")
public class Seat {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "seat_id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "performance_id", nullable = false)
    private Performance performance;

    //좌석(N) : 공연장(1)
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "venue_id", nullable = false)
    private Venue venue;

    @Enumerated(EnumType.STRING)
    @Column(name = "seat_grade", nullable = false)
    private SeatGrade seatGrade;

    @Column(name = "seat_number", nullable = false)
    private String seatNumber;

    //좌석(1) : 공연좌석(N)
    @OneToMany(mappedBy = "seat")
    private List<PerformanceSeat> performanceSeats = new ArrayList<>();

    @Builder
    public Seat(Performance performance, SeatGrade seatGrade, String seatNumber, Integer price) {
        this.venue = venue;
        this.seatGrade = seatGrade;
        this.seatNumber = seatNumber;
    }
}
