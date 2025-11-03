package com.musical.ticket.dto.booking;
//예매 내역 응답 dto
import lombok.Getter;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;
import com.musical.ticket.domain.entity.Booking;
import com.musical.ticket.domain.enums.BookingStatus;
import com.musical.ticket.dto.performance.PerformanceResDto;
import com.musical.ticket.dto.seat.SeatResDto;
import com.musical.ticket.dto.user.UserResDto;

@Getter
public class BookingResDto {
    private Long bookingId;
    private UserResDto user;
    private PerformanceResDto performance;
    private BookingStatus bookingStatus;
    private Integer totalPrice;
    private LocalDateTime bookingDate;
    private List<SeatResDto> seats; // 예매한 좌석 목록

    // Entity -> DTO 변환
    public BookingResDto(Booking booking) {
        this.bookingId = booking.getId();
        this.user = new UserResDto(booking.getUser());
        this.performance = new PerformanceResDto(booking.getPerformance());
        this.bookingStatus = booking.getBookingStatus();
        this.totalPrice = booking.getTotalPrice();
        this.bookingDate = booking.getCreatedAt();
        
        // PerformanceSeat 엔티티 리스트에서 SeatResDto 리스트로 변환
        this.seats = booking.getPerformanceSeats().stream()
                .map(performanceSeat -> new SeatResDto(performanceSeat))
                .collect(Collectors.toList());
    }
}
