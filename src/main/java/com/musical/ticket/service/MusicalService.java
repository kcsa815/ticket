package com.musical.ticket.service;

import java.util.List;
import java.util.stream.Collectors;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import com.musical.ticket.domain.entity.Musical;
import com.musical.ticket.domain.entity.Performance; 
import com.musical.ticket.dto.musical.MusicalResDto;
import com.musical.ticket.dto.musical.MusicalSaveReqDto;
import com.musical.ticket.handler.exception.CustomException;
import com.musical.ticket.handler.exception.ErrorCode;
import com.musical.ticket.repository.MusicalRepository;
import com.musical.ticket.repository.PerformanceRepository; 
import com.musical.ticket.repository.PerformanceSeatRepository;
import com.musical.ticket.repository.BookingRepository;
import com.musical.ticket.util.FileUtil; 
import lombok.RequiredArgsConstructor;


@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class MusicalService {
    
    private final MusicalRepository musicalRepository;
    private final FileUtil fileUtil;
    private final PerformanceSeatRepository performanceSeatRepository;
    private final PerformanceRepository performanceRepository;
    private final BookingRepository bookingRepository;

    //(Admin) 뮤지컬 등록(C)
    @Transactional
    public MusicalResDto saveMusical(MusicalSaveReqDto reqDto, MultipartFile posterImage){ 
        
        String posterImageUrl = fileUtil.saveFile(posterImage);
        Musical musical = reqDto.toEntity(posterImageUrl);
        Musical savedMusical = musicalRepository.save(musical);
        return new MusicalResDto(savedMusical);
    }

    //(Admin) 뮤지컬 정보 수정(U)
    @Transactional
    public MusicalResDto updateMusical(Long musicalId, MusicalSaveReqDto reqDto, MultipartFile posterImage){
        // 1. 기존 Musical 엔티티 조회
        Musical musical = musicalRepository.findById(musicalId).orElseThrow(()->new CustomException(ErrorCode.MUSICAL_NOT_FOUND));
        
        String newImageUrl = null;
        String finalImageUrl = musical.getPosterImageUrl(); // 기본적으로 기존 URL 유지

        // 2. 새 파일이 업로드된 경우에만 처리
        if(posterImage !=null && !posterImage.isEmpty()){ 
             // 2-1. 새 파일 저장 (I/O 충돌 가능 지점)
             newImageUrl = fileUtil.saveFile(posterImage);
             
             // 2-2. [최종 방어] 기존 포스터 URL이 유효할 때만 삭제
             String currentPosterUrl = musical.getPosterImageUrl();
             if (currentPosterUrl != null && currentPosterUrl.startsWith("/images/")) {
                 fileUtil.deleteFile(currentPosterUrl); //안전하게 이전 파일 삭제
             }
             
             // 2-3. 최종 URL을 새 이미지로 확정
             finalImageUrl = newImageUrl;
        } 
        
        // 3. Entity 업데이트 (Dirty Checking)
        musical.update(
            reqDto.getTitle(),
            reqDto.getDescription(),
            finalImageUrl, //새 파일이 없으면 기존 URL 유지
            reqDto.getRunningTime(),
            reqDto.getAgeRating(),
            reqDto.getCategory() 
        );
        
        return new MusicalResDto(musical);
    }

    //(Admin) 뮤지컬 삭제(D)
    @Transactional
    // 종속된 자식 데이터를 먼저 삭제
    public void deleteMusical(Long musicalId){
        Musical musical = musicalRepository.findById(musicalId).orElseThrow(()->new CustomException(ErrorCode.MUSICAL_NOT_FOUND));
        
        // 1. 뮤지컬에 연결된 모든 공연 회차(Performance)를 조회
        List<Performance> performances = performanceRepository.findByMusicalId(musicalId);
        
        // 2. 각 공연 회차에 대해 종속된 엔티티들을 수동으로 삭제
        for (Performance performance : performances) {
            
            // 2-1. 해당 공연에 대한 모든 '예매 내역(Booking)'을 삭제
            bookingRepository.deleteByPerformanceId(performance.getId());
            // 2-2. 해당 공연에 대한 모든 '개별 좌석 상태(PerformanceSeat)' 삭제
            performanceSeatRepository.deleteByPerformanceId(performance.getId()); 
        }
        
        // 3. 포스터 이미지 삭제
        fileUtil.deleteFile(musical.getPosterImageUrl());
        
        // 4. 마지막으로, Performance 회차들을 삭제
        performanceRepository.deleteAll(performances); // (Performance 삭제)
        
        // 5. Musical 자체를 삭제 (FK 위반 없음)
        musicalRepository.delete(musical);
    }

    /**
     * (User/All) 뮤지컬 전체 조회(R)
     * [수정!] N+1 쿼리로 가격(min/max)과 첫 번째 공연장(venueName)을 함께 조회
     */
    public List<MusicalResDto> getAllMusicals(String section) {
        
        List<Musical> musicals;

        // (1) 기본 뮤지컬 목록 조회
        if (section != null && !section.isEmpty()) {
            String category = section.toUpperCase();
            musicals = musicalRepository.findByCategory(category); 
        }
        else {
            musicals = musicalRepository.findAll();
        }
        
        // (2) [N+1] Iterate and fetch details for each musical
        List<MusicalResDto> dtoList = musicals.stream()
            .map(musical -> {
                
                // (A) N+1 쿼리: 가격 범위 조회
                Integer minPrice = null;
                Integer maxPrice = null;
                try {
                    List<Object[]> priceResult = performanceSeatRepository.findMinMaxPriceByMusicalId(musical.getId());
                    if (priceResult != null && !priceResult.isEmpty() && priceResult.get(0)[0] != null) {
                        minPrice = (Integer) priceResult.get(0)[0];
                        maxPrice = (Integer) priceResult.get(0)[1];
                    }
                } catch (Exception e) {} 

                // (B) N+1 쿼리: 첫 번째 공연장 이름 조회
                String venueName = null;
                try {
                    List<Performance> perfs = performanceRepository.findByMusicalIdWithFetch(musical.getId());
                    if (!perfs.isEmpty()) {
                        venueName = perfs.get(0).getVenue().getName();
                    }
                } catch (Exception e) {} 

                // (C) DTO 생성 및 값 주입
                MusicalResDto dto = new MusicalResDto(musical, minPrice, maxPrice);
                dto.setVenueName(venueName); 
                return dto;
                
            })
            .collect(Collectors.toList());

        // (3) (HomePage용) limit 로직 적용
        if (section != null && !section.isEmpty()) {
            String category = section.toUpperCase();
            int limit = 5; 
            return dtoList.stream().limit(limit).collect(Collectors.toList());
        }

        // (4) (ListPage용) DTO 전체 목록 반환
        return dtoList;
    }
    
    //(User/All) 뮤지컬 상세 조회(R)
    public MusicalResDto getMusicalById(Long musicalId){
        Musical musical =  musicalRepository.findById(musicalId)
            .orElseThrow(()->new CustomException(ErrorCode.MUSICAL_NOT_FOUND));

        Integer minPrice = null;
        Integer maxPrice = null;
        try {
            List<Object[]> priceResult = performanceSeatRepository.findMinMaxPriceByMusicalId(musicalId);
            if(priceResult !=null && !priceResult.isEmpty() && priceResult.get(0)[0] !=null){
                minPrice = (Integer)priceResult.get(0)[0];
                maxPrice = (Integer)priceResult.get(0)[1];
            }
        } catch (Exception e) {
            // (가격 조회 실패는 무시)
        }
        return new MusicalResDto(musical, minPrice, maxPrice);
    }
}