package com.musical.ticket.dto.venue;
// 실질적인 요청의 본문(@RequestBody)이 되는 dto
// 공연장 정보(name, location), SeatTemplateReqDto의 리스트를 멤버 변수로 가짐.
// 이렇게 중첩된 dto구조를 사용하면, 단 한번의 API요청으로 공연장 1개와 좌석 여러개를 동시에 생성할 수 있음.

import java.util.List;

import com.musical.ticket.domain.entity.Venue;
import com.musical.ticket.dto.seat.SeatTemplateReqDto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class VenueSaveReqDto {

    @NotBlank
    private String name; //공연장 이름

    private String location; //공연장 주소

    @NotEmpty(message = "좌석의 탬플릿은 최소 1개 이상 등록해야 합니다.")
    @Valid //리스트 안의 SeatTemplateReqDto의 Validation도 검사
    private List<SeatTemplateReqDto> seats;

    // Service에서 Entity로 변환(좌석 제외, 공연장 정보만)
    public Venue toEntity(){
        return Venue.builder()
            .name(this.name)
            .location(location)
            .build();
    }
    
}