package com.musical.ticket.util;

import com.musical.ticket.handler.exception.CustomException;
import com.musical.ticket.handler.exception.ErrorCode;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.UUID;

@Slf4j
@Component
public class FileUtil {

    @Value("${file.upload-dir}")
    private String uploadDir;

    public String saveFile(MultipartFile file) {
        if (file == null || file.isEmpty()) {
            return null;
        }

        try {
            // 1. 저장할 디렉토리의 Path 객체 생성
            Path uploadPath = Paths.get(uploadDir); //  [Paths.get 사용]
            if (!Files.exists(uploadPath)) {
                Files.createDirectories(uploadPath); 
            }

            // 2. 고유한 파일 이름 생성 및 저장
            String originalFilename = file.getOriginalFilename();
            String extension = originalFilename.substring(originalFilename.lastIndexOf("."));
            String filename = UUID.randomUUID().toString() + extension;
            
            Path filePath = uploadPath.resolve(filename);

            // 3. 파일 저장 (쓰기 권한 문제 최종 해결)
            file.transferTo(filePath.toFile()); 
            
            log.info("파일 저장 성공: {}", filePath.toString());
            return "/images/" + filename;

        } catch (IOException e) {
            log.error("파일 저장 실패: {}", e.getMessage(), e);
            throw new CustomException(ErrorCode.FILE_UPLOAD_FAILED, "파일 저장에 실패했습니다. (I/O 권한 문제)"); 
            
        } catch (Exception e) {
            log.error("파일 처리 중 알 수 없는 오류 발생: {}", e.getMessage(), e);
            throw new CustomException(ErrorCode.FILE_UPLOAD_FAILED, "알 수 없는 파일 처리 오류");
        }
    }
    
    public void deleteFile(String fileUrl) {
        if (fileUrl == null || fileUrl.isEmpty() || fileUrl.equals("null")) {
            return;
        }
        try {
            String filename = fileUrl.replace("/images/", "");
            Path filePath = Paths.get(uploadDir, filename);

            if (Files.exists(filePath)) {
                Files.delete(filePath);
                log.info("파일 삭제 성공: {}", filename);
            }
        } catch (Exception e) {
            log.warn("파일 삭제 실패 (파일이 이미 없을 수 있음): {}", fileUrl);
        }
    }
}