package com.musical.ticket.service;

import java.util.List;
import java.util.stream.Collectors;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.musical.ticket.domain.entity.Musical;
import com.musical.ticket.dto.musical.MusicalResDto;
import com.musical.ticket.dto.musical.MusicalSaveReqDto;
import com.musical.ticket.handler.exception.CustomException;
import com.musical.ticket.handler.exception.ErrorCode;
import com.musical.ticket.repository.MusicalRepository;
import com.musical.ticket.repository.PerformanceSeatRepository;
import com.musical.ticket.util.FileUtil;
import lombok.RequiredArgsConstructor;
import java.util.Collections;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class MusicalService {
    
    private final MusicalRepository musicalRepository;
    private final FileUtil fileUtil; //파일 저장을 위해 주입
    private final PerformanceSeatRepository performanceSeatRepository;

    //(Admin) 뮤지컬 등록(C)
    @Transactional
    public MusicalResDto saveMusical(MusicalSaveReqDto reqDto){
        //1. 파일 저장(FileUtil사용, 파일이 없으면 null저장됨)
        String posterImageUrl = fileUtil.saveFile(reqDto.getPosterImage());
        //2. dto ->entity 변환(저장된 url포함)
        Musical musical = reqDto.toEntity(posterImageUrl);
        //3. db에 저장
        Musical savedMusical = musicalRepository.save(musical);

        return new MusicalResDto(savedMusical);
    }

    //(Admin) 뮤지컬 정보 수정(U)
    @Transactional
    public MusicalResDto updateMusical(Long musicalId, MusicalSaveReqDto reqDto){
        //1. 대상 조회
        Musical musical = musicalRepository.findById(musicalId).orElseThrow(()->new CustomException(ErrorCode.MUSICAL_NOT_FOUND));
        //2. 새 이미지 파일이 있는지 확인
        String newImageUrl = null;
        if(reqDto.getPosterImage() !=null && !reqDto.getPosterImage().isEmpty()){
            //2-1. 새 이미지 저장;
            newImageUrl = fileUtil.saveFile(reqDto.getPosterImage());
            //2-2. 기존 이미지 삭제
            fileUtil.deleteFile(musical.getPosterImageUrl());
        }    
        //3. 엔티티 정보 업데이트
        String finalImageUrl = (newImageUrl !=null) ? newImageUrl : musical.getPosterImageUrl();
        musical.update(
                reqDto.getTitle(),
                reqDto.getDescription(), // (HTML이 포함된 description)
                finalImageUrl, reqDto.getRunningTime(),
                reqDto.getAgeRating(), finalImageUrl
        );
        return new MusicalResDto(musical);      
    }

    //(Admin) 뮤지컬 삭제(D)
    @Transactional
    public void deleteMusical(Long musicalId){
        //1. 대상 조회;
        Musical musical = musicalRepository.findById(musicalId).orElseThrow(()->new CustomException(ErrorCode.MUSICAL_NOT_FOUND));
        //2. 저장된 이미지 파일 삭제
        fileUtil.deleteFile(musical.getPosterImageUrl());
        //3. DB에서 삭제
        musicalRepository.delete(musical);
    }

    /*
     * (User/All) 뮤지컬 전체 조회(R)
     */
    public List<MusicalResDto> getAllMusicals(String section) {
        // DB에서 '모든' 뮤지컬을 일단 다 가져옴
        List<Musical> musicals = null;
        
        // 1. (임시 로직) section 값에 따라 목록을 가공/필터링
        if (section != null && !section.isEmpty()) {
            String category = section.toUpperCase();  //ranking ->RANKING
            musicals = musicalRepository.findByCategory(category);

            int limit = "RANKING".equals(category) ? 5:4;
            return musicals.stream()
                .limit(limit)
                .map(MusicalResDto::new)
                .collect(Collectors.toList());
        }
        
        musicals = musicalRepository.findAll();

        // 2. section 값이 없거나(null) 일치하는게 없으면 "전체" 목록 반환
        return musicals.stream()
                .map(MusicalResDto::new)
                .collect(Collectors.toList());
    }

    /*
     *(User/All) 뮤지컬 상세 조회(R)
    */
    public MusicalResDto getMusicalById(Long musicalId){
        //1. 뮤지컬 조회
        Musical musical =  musicalRepository.findById(musicalId)
            .orElseThrow(()->new CustomException(ErrorCode.MUSICAL_NOT_FOUND));

        //2. 가격 범위 조회
        Integer minPrice = null;
        Integer maxPrice = null;

        try {
            List<Object[]> priceResult = performanceSeatRepository.findMinMaxPriceByMusicalId(musicalId);
            if(priceResult !=null && !priceResult.isEmpty() && priceResult.get(0)[0] !=null){
                minPrice = (Integer)priceResult.get(0)[0];
                maxPrice = (Integer)priceResult.get(0)[1];
            }
        } catch (Exception e) {
            // 가격 조회 실패는 무시
        }

        return new MusicalResDto(musical, minPrice, maxPrice);
    }
}
