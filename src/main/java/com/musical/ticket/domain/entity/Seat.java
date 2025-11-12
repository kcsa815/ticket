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

    //좌석(N) : 공연장(1)
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "venue_id", nullable = false)
    private Venue venue;

    @Enumerated(EnumType.STRING)
    @Column(name = "seat_grade", nullable = false)
    private SeatGrade seatGrade;

    @Column(name = "seat_number", nullable = false)
    private String seatNumber;

    @Column(name = "x_coord", nullable = false)
    private Integer xCoord;

    @Column(name = "y_coord", nullable = false)
    private Integer yCoord;

    //좌석(1) : 공연좌석(N)
    @OneToMany(mappedBy = "seat")
    private List<PerformanceSeat> performanceSeats = new ArrayList<>();

    @Builder
    public Seat(Venue venue, SeatGrade seatGrade, String seatNumber, Integer xCoord, Integer yCoord) {
        this.venue = venue;
        this.seatGrade = seatGrade;
        this.seatNumber = seatNumber;
        this.xCoord = (xCoord != null) ? xCoord : 0;
        this.yCoord = (yCoord != null) ? yCoord : 0;
    }
}
