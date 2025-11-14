package com.musical.ticket.dto.venue;

import java.util.List;
import java.util.stream.Collectors;
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
    private String layoutImageUrl;  // 👈 추가
    private List<SeatResDto> seats;

    public VenueResDto(Venue venue){
        this.venueId = venue.getId();
        this.name = venue.getName();
        this.location = venue.getLocation();
        this.layoutImageUrl = venue.getLayoutImageUrl();  // 👈 추가

        this.seats = venue.getSeats().stream()
            .map(SeatResDto::new)
            .collect(Collectors.toList());
    }
}