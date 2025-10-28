package com.musical.ticket.service;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.musical.ticket.dto.AdminShowRegisterDto;
import com.musical.ticket.dto.AdminShowUpdateDto;
import com.musical.ticket.entity.Show;
import com.musical.ticket.repository.ShowRepository;

@Service
@Transactional
public class ShowService {
    
    private final ShowRepository showRepository;

    public ShowService(ShowRepository showRepository){
        this.showRepository = showRepository;
    }

    //공연 등록
    public Show registerShow(AdminShowRegisterDto dto){
        Show show = Show.createShow(dto);

        Show saveShow = showRepository.save(show);

        return saveShow;
    }

    //관리자페이지 등록한 모든 공연 조회
    @Transactional(readOnly = true)
    public List<Show> findAllShows(){
        return showRepository.findAll();
    }

    //공연 수정(get)
    @Transactional(readOnly = true)
    public Show findShowById(Long showId) {
        return showRepository.findById(showId)
                .orElseThrow(() -> new IllegalArgumentException("해당 공연을 찾을 수 없습니다. id=" + showId));
    }

    //공연 수정(post)
    @Transactional
    public void updateShow(Long showId, AdminShowUpdateDto dto) {
        // 1. DB에서 원본 엔티티 조회 (동일)
        Show showToUpdate = showRepository.findById(showId)
                .orElseThrow(() -> new IllegalArgumentException("해당 공연을 찾을 수 없습니다. id=" + showId));

        // 2. DTO -> Entity 값 변경
        showToUpdate.setTitle(dto.getTitle());
        showToUpdate.setPosterUrl(dto.getPosterUrl());
        showToUpdate.setDescription(dto.getDescription());
        
        // 3. (핵심) 날짜 변환 (String -> LocalDate)
        if (dto.getStartDate() != null && !dto.getStartDate().isEmpty()) {
            LocalDate startDate = LocalDate.parse(dto.getStartDate(), DateTimeFormatter.ISO_LOCAL_DATE);
            showToUpdate.setStartDate(startDate);
        }
        if (dto.getEndDate() != null && !dto.getEndDate().isEmpty()) {
            LocalDate endDate = LocalDate.parse(dto.getEndDate(), DateTimeFormatter.ISO_LOCAL_DATE);
            showToUpdate.setEndDate(endDate);
        }

        // 4. 좌석 정보 변경
        showToUpdate.setVipPrice(dto.getVipPrice());
        showToUpdate.setVipTotalSeats(dto.getVipTotalSeats());
        showToUpdate.setRPrice(dto.getRPrice());
        showToUpdate.setRTotalSeats(dto.getRTotalSeats());
        showToUpdate.setSPrice(dto.getSPrice());
        showToUpdate.setSTotalSeats(dto.getSTotalSeats());
        showToUpdate.setAPrice(dto.getAPrice());
        showToUpdate.setATotalSeats(dto.getATotalSeats());
        
        // (메서드 종료 시 변경 감지로 UPDATE 실행)
    }

    //공연 삭제
    public void deleteShow(Long showId){
        showRepository.deleteById(showId);
    }

}
