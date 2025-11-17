package com.musical.ticket.service;

import java.util.List;
import java.util.stream.Collectors;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.musical.ticket.domain.entity.Musical;
import com.musical.ticket.domain.entity.Performance;
import com.musical.ticket.dto.musical.MusicalResDto;
import com.musical.ticket.dto.musical.MusicalSaveReqDto;
import com.musical.ticket.handler.exception.CustomException;
import com.musical.ticket.handler.exception.ErrorCode;
import com.musical.ticket.repository.MusicalRepository;
import com.musical.ticket.repository.PerformanceRepository;
import com.musical.ticket.repository.PerformanceSeatRepository;
import com.musical.ticket.util.FileUtil;
import lombok.RequiredArgsConstructor;
import java.util.Collections; // (imports 확인)

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class MusicalService {
    
    private final MusicalRepository musicalRepository;
    private final FileUtil fileUtil;
    private final PerformanceSeatRepository performanceSeatRepository;
    private final PerformanceRepository performanceRepository;

    //(Admin) 뮤지컬 등록(C)
    @Transactional
    public MusicalResDto saveMusical(MusicalSaveReqDto reqDto){
        String posterImageUrl = fileUtil.saveFile(reqDto.getPosterImage());
        Musical musical = reqDto.toEntity(posterImageUrl);
        Musical savedMusical = musicalRepository.save(musical);
        return new MusicalResDto(savedMusical);
    }

    //(Admin) 뮤지컬 정보 수정(U)
    @Transactional
    public MusicalResDto updateMusical(Long musicalId, MusicalSaveReqDto reqDto){
        Musical musical = musicalRepository.findById(musicalId).orElseThrow(()->new CustomException(ErrorCode.MUSICAL_NOT_FOUND));
        
        String newImageUrl = null;
        if(reqDto.getPosterImage() !=null && !reqDto.getPosterImage().isEmpty()){
            newImageUrl = fileUtil.saveFile(reqDto.getPosterImage());
            fileUtil.deleteFile(musical.getPosterImageUrl());
        }     
        String finalImageUrl = (newImageUrl !=null) ? newImageUrl : musical.getPosterImageUrl();
        
        // (update 메서드 호출 - 이 코드는 정상이었습니다)
        musical.update(
                reqDto.getTitle(),
                reqDto.getDescription(),
                finalImageUrl, 
                reqDto.getRunningTime(),
                reqDto.getAgeRating(),
                reqDto.getCategory() 
        );
        
        return new MusicalResDto(musical);      
    }

    //(Admin) 뮤지컬 삭제(D)
    @Transactional
    public void deleteMusical(Long musicalId){
        Musical musical = musicalRepository.findById(musicalId).orElseThrow(()->new CustomException(ErrorCode.MUSICAL_NOT_FOUND));
        fileUtil.deleteFile(musical.getPosterImageUrl());
        musicalRepository.delete(musical);
    }

    /**
     * (User/All) 뮤지컬 전체 조회 (R)
     * [수정!] N+1 쿼리로 가격(min/max)과 첫 번째 공연장(venueName)을 함께 조회
     */
    public List<MusicalResDto> getAllMusicals(String section) {
        
        List<Musical> musicals;

        // (1) 기본 뮤지컬 목록 조회
        if (section != null && !section.isEmpty()) {
            String category = section.toUpperCase();
            musicals = musicalRepository.findByCategory(category);
        } else {
            musicals = musicalRepository.findAll();
        }
        
        // --- 👇 [4. (핵심 수정!) N+1 쿼리로 DTO를 수동 생성] ---
        // (N+1: 뮤지컬 10개를 조회하면, 10번의 가격 쿼리 + 10번의 공연장 쿼리가 추가로 나감)
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
                } catch (Exception e) {} // (오류 시 null 유지)

                // (B) N+1 쿼리: 첫 번째 공연장 이름 조회
                String venueName = null;
                try {
                    // (findByMusicalIdWithFetch 쿼리를 재사용하여 첫 번째 공연장만 가져옴)
                    List<Performance> perfs = performanceRepository.findByMusicalIdWithFetch(musical.getId());
                    if (!perfs.isEmpty()) {
                        venueName = perfs.get(0).getVenue().getName();
                    }
                } catch (Exception e) {} // (오류 시 null 유지)

                // (C) DTO 생성 (가격 정보 포함)
                MusicalResDto dto = new MusicalResDto(musical, minPrice, maxPrice);
                // (D) DTO에 공연장 이름 주입 (Setter 사용)
                dto.setVenueName(venueName); 
                return dto;
                
            })
            .collect(Collectors.toList());
        // --- 👆 [수정 끝] ---


        // (5) (HomePage용) limit 로직은 "최종" DTO 리스트에 적용
        if (section != null && !section.isEmpty()) {
            String category = section.toUpperCase();
            int limit = "RANKING".equals(category) ? 5 : 4;
            return dtoList.stream().limit(limit).collect(Collectors.toList());
        }

        // (6) (ListPage용) DTO 전체 목록 반환
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

        // (MusicalResDto에 category가 추가되었으므로, 이 DTO가 알아서 처리)
        return new MusicalResDto(musical, minPrice, maxPrice);
    }
}