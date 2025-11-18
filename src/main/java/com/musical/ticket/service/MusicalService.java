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
     * (User/All) 뮤지컬 전체 조회(R)
     */
    public List<MusicalResDto> getAllMusicals(String section) {
        
        List<Musical> musicals;

        // (1) section 값이 있으면 (HomePage)
        if (section != null && !section.isEmpty()) {
            String category = section.toUpperCase(); 
            musicals = musicalRepository.findByCategory(category); 
        }
        else {
            // (2) section 값이 없으면 (MusicalListPage) "전체" 목록 반환
            musicals = musicalRepository.findAll();
        }
        
        // (3) (N+1 쿼리로 DTO를 수동 생성)
        List<MusicalResDto> dtoList = musicals.stream()
            .map(musical -> {
                // ... (N+1 쿼리: 가격/공연장 조회) ...
                Integer minPrice = null;
                Integer maxPrice = null;
                try {
                    List<Object[]> priceResult = performanceSeatRepository.findMinMaxPriceByMusicalId(musical.getId());
                    if (priceResult != null && !priceResult.isEmpty() && priceResult.get(0)[0] != null) {
                        minPrice = (Integer) priceResult.get(0)[0];
                        maxPrice = (Integer) priceResult.get(0)[1];
                    }
                } catch (Exception e) {} 

                String venueName = null;
                try {
                    List<Performance> perfs = performanceRepository.findByMusicalIdWithFetch(musical.getId());
                    if (!perfs.isEmpty()) {
                        venueName = perfs.get(0).getVenue().getName();
                    }
                } catch (Exception e) {} 

                MusicalResDto dto = new MusicalResDto(musical, minPrice, maxPrice);
                dto.setVenueName(venueName); 
                return dto;
            })
            .collect(Collectors.toList());
        // (5) (ListPage용) DTO 전체 목록 반환
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