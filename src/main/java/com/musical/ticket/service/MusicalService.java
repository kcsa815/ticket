package com.musical.ticket.service;

import java.io.IOException;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import com.musical.ticket.dto.AdminMusicalUpdateDto;
import com.musical.ticket.entity.Musical;
import com.musical.ticket.repository.MusicalRepository;
import com.musical.ticket.dto.AdminMusicalRegisterDto;
import java.io.File;
import java.nio.file.Path;
import java.util.UUID;

@Service
@Transactional
public class MusicalService {
    
    private final MusicalRepository musicalRepository;

    public MusicalService(MusicalRepository musicalRepository){
        this.musicalRepository = musicalRepository;
    }

    //공연 등록
    public Musical registerMusical(AdminMusicalRegisterDto musicalDto){
        Musical musical = Musical.createMusical(musicalDto);
        Musical saveMusical = musicalRepository.save(musical);
        return saveMusical;
    }

    private final String uploadDir = System.getProperty("user.dir") + 
                                     "/src/main/resources/static/images/";

    public void registerMusical(AdminMusicalRegisterDto dto, MultipartFile file) throws IOException {
        String savedFilePath = null;

        if (file != null && !file.isEmpty()) {
            
            String originalFilename = file.getOriginalFilename();
            String extension = originalFilename.substring(originalFilename.lastIndexOf("."));
            String savedFilename = UUID.randomUUID().toString() + extension;

            Path destinationPath = Paths.get(uploadDir + savedFilename);

            File directory = new File(uploadDir);
            if (!directory.exists()) {
                directory.mkdirs(); // 
            }
            file.transferTo(destinationPath);
            
            savedFilePath = "/images/" + savedFilename;

        } else {
            savedFilePath = "/images/default-poster.jpg"; 
        }

        dto.setPosterUrl(savedFilePath);

        Musical musical = Musical.createShow(dto);
        musicalRepository.save(musical);
    }

    //관리자페이지 등록한 모든 뮤지컬 조회
    @Transactional(readOnly = true)
    public List<Musical> findAllMusicals(){
        return musicalRepository.findAll();
    }

    //공연 수정(get)
    @Transactional(readOnly = true)
    public Musical findMusicalById(Long musicalId) {
        return musicalRepository.findById(musicalId)
                .orElseThrow(() -> new IllegalArgumentException("해당 공연을 찾을 수 없습니다. id=" + musicalId));
    }

    //공연 수정(post)
    @Transactional
    public void updateMusical(Long musicalId, AdminMusicalUpdateDto dto) {
        // 1. DB에서 원본 엔티티 조회 (동일)
        Musical musicalToUpdate = musicalRepository.findById(musicalId)
                .orElseThrow(() -> new IllegalArgumentException("해당 뮤지컬을 찾을 수 없습니다. id=" + musicalId));

        // 2. DTO -> Entity 값 변경
        musicalToUpdate.setTitle(dto.getTitle());
        musicalToUpdate.setPosterUrl(dto.getPosterUrl());
        musicalToUpdate.setDescription(dto.getDescription());
        
        // 3. (핵심) 날짜 변환 (String -> LocalDate)
        if (dto.getStartDate() != null && !dto.getStartDate().isEmpty()) {
            LocalDate startDate = LocalDate.parse(dto.getStartDate(), DateTimeFormatter.ISO_LOCAL_DATE);
            musicalToUpdate.setStartDate(startDate);
        }
        if (dto.getEndDate() != null && !dto.getEndDate().isEmpty()) {
            LocalDate endDate = LocalDate.parse(dto.getEndDate(), DateTimeFormatter.ISO_LOCAL_DATE);
            musicalToUpdate.setEndDate(endDate);
        }

        // 4. 좌석 정보 변경
        musicalToUpdate.setVipPrice(dto.getVipPrice());
        musicalToUpdate.setVipTotalSeats(dto.getVipTotalSeats());
        musicalToUpdate.setRPrice(dto.getRPrice());
        musicalToUpdate.setRTotalSeats(dto.getRTotalSeats());
        musicalToUpdate.setSPrice(dto.getSPrice());
        musicalToUpdate.setSTotalSeats(dto.getSTotalSeats());
        musicalToUpdate.setAPrice(dto.getAPrice());
        musicalToUpdate.setATotalSeats(dto.getATotalSeats());
    }

    //공연 삭제
    public void deleteMusical(Long musicalId){
        musicalRepository.deleteById(musicalId);
    }

}
