package com.musical.ticket.dto.booking;
//예매 내역 응답 dto
import lombok.Getter;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;
import com.musical.ticket.domain.entity.Booking;
import com.musical.ticket.domain.enums.BookingStatus;
import com.musical.ticket.dto.performance.PerformanceSeatResDto;
import com.musical.ticket.dto.performance.PerformanceSimpleResDto;
import com.musical.ticket.dto.user.UserResDto;

@Getter
public class BookingResDto {
    private Long bookingId;
    private UserResDto user;
    private PerformanceSimpleResDto performance;
    private BookingStatus bookingStatus;
    private Integer totalPrice;
    private LocalDateTime bookingDate;
    private List<PerformanceSeatResDto> bookedSeats; // 예매된 좌석 목록

    // Entity -> DTO 변환
    public BookingResDto(Booking booking) {
        this.bookingId = booking.getId();
        this.user = new UserResDto(booking.getUser());
        this.performance = new PerformanceSimpleResDto(booking.getPerformance());
        this.bookingStatus = booking.getBookingStatus();
        this.totalPrice = booking.getTotalPrice();
        this.bookingDate = booking.getCreatedAt();
        
        // Booking엔티티가 가진 PerformanceSeat리스트를 dto리스트로 변환
        this.bookedSeats = booking.getPerformanceSeats().stream()
                .map(PerformanceSeatResDto::new)
                .collect(Collectors.toList());
    }
}
