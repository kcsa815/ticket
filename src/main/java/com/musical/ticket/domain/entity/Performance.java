package com.musical.ticket.domain.entity;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor
@Table(name = "performance")
public class Performance {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "performance_id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY) //지연 로딩(성능 최적화)
    @Column(name = "musical_id", nullable = false)
    private Musical musical;

    @Column(name = "performance_date", nullable = false)
    private LocalDateTime performanceDate;

    @Column(nullable = false)
    private String venue;

    // 1:M 관계
    @OneToMany(mappedBy = "performance", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Seat> seats = new ArrayList<>(); 

    // 1:M 관계
    @OneToMany(mappedBy = "performance", orphanRemoval = true) //Booking이 삭제되더라도 Performance는 남아있어야 하므로 cascade 옵션제거
    private List<Booking> bookings = new ArrayList<>();

    @Builder
    public Performance(Musical musical, LocalDateTime performanceDate, String venue) {
        this.musical = musical;
        this.performanceDate = performanceDate;
        this.venue = venue;
    }
}
