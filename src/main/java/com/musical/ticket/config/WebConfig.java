package com.musical.ticket.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.io.File; 
import java.nio.file.Path;
import java.nio.file.Paths;
import lombok.RequiredArgsConstructor;

@Configuration
public class WebConfig implements WebMvcConfigurer {

    @Value("${file.upload-dir}")
    private String uploadDir; // 현재: ./uploads/

    @Value("${file.resource-handler}")
    private String resourceHandler; // 현재: /images/**
    
    /**
     * [최종 안정화!] File 객체를 이용한 절대 경로 URI 생성
     * (이 방식이 Windows 환경에서 가장 안정적으로 경로를 해결합니다.)
     */
    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        
        // 1. 상대 경로('./uploads/')를 File 객체를 통해 "절대 경로"로 변환
        String absolutePath = new File(uploadDir).getAbsolutePath();
        
        // 2. 경로 구분자(\)를 웹 구분자(/)로 변경하고 'file:' 접두사로 URI 완성
        String fileUri = "file:" + absolutePath.replace("\\", "/"); 
        
        // 3. 리소스 핸들러 등록
        registry.addResourceHandler(resourceHandler) // 논리적 경로: /images/**
                .addResourceLocations(fileUri);    
    }
}