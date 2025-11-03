package com.musical.ticket.util;
import java.io.IOException;
//파일을 서버에 서장하고 파일명을 UUID로 변환하는 등 반복적 작업을 처리할 Util
import java.nio.file.Files;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;
import com.musical.ticket.handler.exception.CustomException;
import com.musical.ticket.handler.exception.ErrorCode;
import lombok.extern.slf4j.Slf4j;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.UUID;

@Slf4j
@Component //String이 관리하는 Bean으로 등록
public class FileUtil {
    
    private final String uploadDir;

    public FileUtil(@Value("${file.upload-dir}") String uploadDir){
        this.uploadDir = uploadDir;
    }

    //MultipartFile을 서버의 저장된 경로에 저장
    public String saveFile(MultipartFile file){
        if(file ==null || file.isEmpty()){
            return null; //파일이 없으면 null반환
        }
        try{
            //1. 원본 파일명에서 확장자 추출
            String originalFilename = file.getOriginalFilename();
            String extension = originalFilename != null && originalFilename.contains(".") 
                ? originalFilename.substring(originalFilename.lastIndexOf(".")) 
                : "";
            
            //2. UUID로 고유한 파일명 생성
            String storedFilename = UUID.randomUUID().toString() + extension;
            
            //3. 저장할 경로 생성
            Path destinationPath = Paths.get(uploadDir, storedFilename);
            
            //4. 파일 저장
            Files.copy(file.getInputStream(), destinationPath);
            log.info("파일 저장 성공  :{}", destinationPath);

            return "/images/" + storedFilename;

        }catch(IOException e){
            log.error("파일 저장 실패", e);
            throw new CustomException(ErrorCode.INTERNAL_SERVER_ERROR);
        }
    }

    //서버에서 파일 삭제
    public void deleteFile(String fileUrl){
        if(fileUrl == null || fileUrl.isEmpty()){
            return;
        }

        try {
            //1. url에서 파일명 추출
            String filename = fileUrl.substring(fileUrl.lastIndexOf("/") +1);
            //2. 실제 파일 경로
            Path filePath = Paths.get(uploadDir + filename);
            //3. 파일 삭제
            Files.deleteIfExists(filePath);
            log.info("파일 삭제 성공 : {}", filePath);

        } catch (IOException e) { 
            log.error("파일 삭제 실패", e);
            //삭제 실패는 치명적인 오류가 아니라서 예외 x
        }
    }
}
