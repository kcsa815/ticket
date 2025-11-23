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

    // application.properties에서 file.upload-dir 경로 주입 (./musical-uploads/ 값)
    @Value("${file.upload-dir}")
    private String uploadDir; 

    public String saveFile(MultipartFile file) {
        if (file == null || file.isEmpty()) {
            return null;
        }

        try {
            // --- 절대 경로 기반 Path 생성 ---
            // 1. System.getProperty("user.dir"): Gradle이 실행되는 프로젝트 루트의 절대 경로를 가져옴
            // 2. Paths.get으로 결합하여 절대 경로 Path 객체를 생성
            Path uploadPath = Paths.get(System.getProperty("user.dir"), "musical-uploads");

            if (!Files.exists(uploadPath)) {
                Files.createDirectories(uploadPath); // 이제 권한만 있다면 100% 생성됨
            }

            // 2. 고유한 파일 이름 생성
            String originalFilename = file.getOriginalFilename();
            String extension = originalFilename.substring(originalFilename.lastIndexOf("."));
            String filename = UUID.randomUUID().toString() + extension;
            
            // 3. 파일 저장 경로 및 쓰기
            Path filePath = uploadPath.resolve(filename);
            file.transferTo(filePath.toFile()); 
            
            log.info("파일 저장 성공: {}", filePath.toString());
            // [주의!] 브라우저 접근용 논리적 경로는 'uploads' 폴더 대신 '/images/'를 사용
            return "/images/" + filename;

        } catch (IOException e) {
            log.error("파일 저장 실패: {}", e.getMessage(), e);
            throw new CustomException(ErrorCode.FILE_UPLOAD_FAILED, "파일 저장에 실패했습니다. (I/O 권한 문제)"); 
            
        } catch (Exception e) {
            log.error("파일 처리 중 알 수 없는 오류 발생: {}", e.getMessage(), e);
            throw new CustomException(ErrorCode.FILE_UPLOAD_FAILED, "알 수 없는 파일 처리 오류");
        }
    }
    
    // (deleteFile 메서드도 동일하게 수정 필요)
    public void deleteFile(String fileUrl) {
        if (fileUrl == null || fileUrl.isEmpty() || fileUrl.equals("null")) {
            return;
        }
        try {
            String filename = fileUrl.replace("/images/", "");
            
            // 삭제 시에도 절대 경로 사용
            Path uploadPath = Paths.get(System.getProperty("user.dir"), "musical-uploads");
            Path filePath = uploadPath.resolve(filename);

            if (Files.exists(filePath)) {
                Files.delete(filePath);
                log.info("파일 삭제 성공: {}", filename);
            }
        } catch (Exception e) {
            log.warn("파일 삭제 실패 (파일이 이미 없을 수 있음): {}", fileUrl);
        }
    }
}