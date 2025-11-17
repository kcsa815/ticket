package com.musical.ticket.dto.venue;

import java.util.List;
import java.util.stream.Collectors;
import com.musical.ticket.domain.entity.Seat;
import com.musical.ticket.domain.entity.Venue;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class VenueResDto {
    private Long venueId;
    private String name;
    private String location;
    private String region; 
    private List<SeatResDto> seats;

    // (기존 생성자 - N+1 발생 가능성 있음)
    public VenueResDto(Venue venue) {
        this.venueId = venue.getId();
        this.name = venue.getName();
        this.location = venue.getLocation();
        this.region = venue.getRegion(); 
        
        // (주의!) venue.getSeats()가 Lazy Loading일 수 있음
        this.seats = venue.getSeats().stream()
                .map(SeatResDto::new)
                .collect(Collectors.toList());

    }

    public VenueResDto(Venue venue, List<Seat> seats) {
        this.venueId = venue.getId();
        this.name = venue.getName();
        this.location = venue.getLocation();
        this.region = venue.getRegion(); 
        
        this.seats = seats.stream()
                .map(SeatResDto::new)
                .collect(Collectors.toList());
    }
}