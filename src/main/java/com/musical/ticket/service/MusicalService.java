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
import com.musical.ticket.util.FileUtil;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class MusicalService {
    
    private final MusicalRepository musicalRepository;
    private final FileUtil fileUtil; //파일 저장을 위해 주입

    //(Admin) 뮤지컬 등록(C)
    @Transactional
    public MusicalResDto saveMusical(MusicalSaveReqDto reqDto){
        //1. 파일 저장(FileUtil사용, 파일이 없으면 null저장됨)
        String posterImageUrl = fileUtil.saveFile(reqDto.getPosterImageUrl());
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
        if(reqDto.getPosterImageUrl() !=null && !reqDto.getPosterImageUrl().isEmpty()){
            //2-1. 새 이미지 저장;
            newImageUrl = fileUtil.saveFile(reqDto.getPosterImageUrl());
            //2-2. 기존 이미지 삭제
            fileUtil.deleteFile(musical.getPosterImageUrl());
        }    
        //3. 엔티티 정보 업데이트
        String finalImageUrl = (newImageUrl !=null) ? newImageUrl : musical.getPosterImageUrl();
        musical.update(
            reqDto.getTitle(),
            reqDto.getDescription(),
            finalImageUrl,
            reqDto.getRunningTime(),
            reqDto.getAgeRating()
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

    //(User/All) 뮤지컬 전체 조회(R)
    public List<MusicalResDto> getAllMusicals(){
        List<Musical> musicals = musicalRepository.findAll();
        return musicals.stream()
            .map(MusicalResDto::new)
            .collect(Collectors.toList());
    }

    //(User/All) 뮤지컬 상세 조회(R)
    public MusicalResDto getMusicalById(Long musicalId){
        Musical musical =  musicalRepository.findById(musicalId)
            .orElseThrow(()->new CustomException(ErrorCode.MUSICAL_NOT_FOUND));
        return new MusicalResDto(musical);
    }
}
