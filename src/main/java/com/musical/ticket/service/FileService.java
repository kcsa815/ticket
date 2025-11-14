package com.musical.ticket.service;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.UUID;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import com.musical.ticket.handler.exception.CustomException;
import com.musical.ticket.handler.exception.ErrorCode;

@Service
public class FileService {
    
    @Value("${file.upload-dir}")
    private String uploadDir;
    
    /**
     * 파일을 서버에 저장하고 접근 가능한 URL을 반환
     * @param file 업로드할 파일
     * @param subDir 하위 디렉토리 (예: "venue-layouts", "posters")
     * @return 저장된 파일의 접근 URL (예: "/images/venue-layouts/uuid.jpg")
     */
    public String saveFile(MultipartFile file, String subDir) {
        try {
            // 1. 원본 파일명과 확장자 추출
            String originalFilename = file.getOriginalFilename();
            if (originalFilename == null || originalFilename.isEmpty()) {
                throw new CustomException(ErrorCode.INVALID_INPUT_VALUE);
            }
            
            String extension = "";
            int lastDotIndex = originalFilename.lastIndexOf(".");
            if (lastDotIndex > 0) {
                extension = originalFilename.substring(lastDotIndex);
            }
            
            // 2. 고유한 파일명 생성 (UUID 사용)
            String filename = UUID.randomUUID().toString() + extension;
            
            // 3. 저장 경로 생성
            Path dirPath = Paths.get(uploadDir, subDir);
            Files.createDirectories(dirPath);  // 디렉토리가 없으면 생성
            
            // 4. 파일 저장
            Path filePath = dirPath.resolve(filename);
            Files.copy(file.getInputStream(), filePath, StandardCopyOption.REPLACE_EXISTING);
            
            // 5. 웹에서 접근 가능한 URL 반환
            // application.properties의 file.resource-handler=/images/** 와 매핑
            return "/images/" + subDir + "/" + filename;
            
        } catch (IOException e) {
            throw new CustomException(ErrorCode.FILE_UPLOAD_FAILED);
        }
    }
    
    /**
     * 파일 삭제
     * @param fileUrl 삭제할 파일의 URL (예: "/images/venue-layouts/uuid.jpg")
     */
    public void deleteFile(String fileUrl) {
        if (fileUrl == null || fileUrl.isEmpty()) {
            return;
        }
        
        try {
            // URL에서 파일 경로 추출 (/images/venue-layouts/uuid.jpg -> venue-layouts/uuid.jpg)
            String filePath = fileUrl.replace("/images/", "");
            Path path = Paths.get(uploadDir, filePath);
            
            if (Files.exists(path)) {
                Files.delete(path);
            }
        } catch (IOException e) {
            // 파일 삭제 실패는 로그만 남기고 예외는 던지지 않음
            e.printStackTrace();
        }
    }
}