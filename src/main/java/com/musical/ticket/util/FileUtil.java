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
import java.util.UUID;

@Slf4j
@Component
public class FileUtil {

    // application.properties에서 file.upload-dir 경로 주입
    @Value("${file.upload-dir}")
    private String uploadDir;

    /**
     * 파일 저장 (MultipartFile -> 파일 시스템)
     * @param file 포스터 이미지 파일
     * @return 저장된 파일의 논리적 경로 (예: /images/uuid.jpg)
     */
    public String saveFile(MultipartFile file) {
        // 1. 파일이 비어있는지 체크 (AdminPage에서 required=false일 경우 대비)
        if (file == null || file.isEmpty()) {
            return null;
        }

        try {
            // 2. 저장할 디렉토리의 Path 객체 생성 (운영체제와 무관한 Path 사용)
            Path uploadPath = Path.of(uploadDir);
            
            // 3. [핵심 방어 코드!] 디렉토리가 없으면 생성 (Docker 권한 문제 방지)
            if (!Files.exists(uploadPath)) {
                Files.createDirectories(uploadPath); 
            }

            // 4. 고유한 파일 이름 생성 (UUID 사용)
            String originalFilename = file.getOriginalFilename();
            String extension = originalFilename.substring(originalFilename.lastIndexOf("."));
            String filename = UUID.randomUUID().toString() + extension;
            
            // 5. 실제 저장될 파일 경로 (Path.of 사용)
            Path filePath = uploadPath.resolve(filename);

            // 6. 파일 저장 (transferTo)
            file.transferTo(filePath.toFile());
            
            log.info("파일 저장 성공: {}", filePath.toString());

            // 7. 브라우저 접근용 논리적 경로 반환 (예: /images/uuid.jpg)
            return "/images/" + filename;

        } catch (IOException e) {
            log.error("파일 저장 실패: {}", e.getMessage(), e);
            // FileUtil에서 CustomException을 던지도록 처리 (GlobalExceptionHandler가 받음)
            throw new CustomException(ErrorCode.FILE_UPLOAD_FAILED, e.getMessage()); 
            
        } catch (Exception e) {
            log.error("파일 처리 중 알 수 없는 오류 발생: {}", e.getMessage(), e);
            throw new CustomException(ErrorCode.FILE_UPLOAD_FAILED, "알 수 없는 파일 처리 오류");
        }
    }
    
    /**
     * 파일 삭제
     * @param fileUrl 삭제할 파일의 논리적 경로 (예: /images/uuid.jpg)
     */
    public void deleteFile(String fileUrl) {
        if (fileUrl == null || fileUrl.isEmpty() || fileUrl.equals("null")) {
            return;
        }

        try {
            // 1. 논리적 경로 -> 물리적 경로 변환 ( /images/ 제거)
            String filename = fileUrl.replace("/images/", "");
            Path filePath = Path.of(uploadDir, filename);

            // 2. 파일이 실제로 존재하면 삭제
            if (Files.exists(filePath)) {
                Files.delete(filePath);
                log.info("파일 삭제 성공: {}", filename);
            }
        } catch (Exception e) {
            log.warn("파일 삭제 실패 (파일이 이미 없을 수 있음): {}", fileUrl);
            // 삭제 실패는 치명적이지 않으므로 WARN만 기록하고 에러를 던지지 않음
        }
    }
}