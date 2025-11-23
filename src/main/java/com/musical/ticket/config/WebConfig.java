package com.musical.ticket.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.io.File; // java.io.File 임포트
import java.nio.file.Path;
import java.nio.file.Paths;
import lombok.RequiredArgsConstructor;

@Configuration
public class WebConfig implements WebMvcConfigurer {

    // application.properties에서 file.upload-dir 경로 주입
    @Value("${file.upload-dir}")
    private String uploadDir; // 현재: ./uploads/

    @Value("${file.resource-handler}")
    private String resourceHandler; // 현재: /images/**
    
    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        
        // 1. 상대 경로 ('./uploads/')를 Java File 객체를 통해 시스템의 "절대 경로"로 변환
        String absolutePath = new File(uploadDir).getAbsolutePath();
        
        // 2. 경로의 디렉토리 구분자(\)를 웹 경로 구분자(/)로 변경하고 'file:' 접두사를 붙임
        String fileUri = "file:" + absolutePath.replace("\\", "/"); 
        
        // 3. 리소스 핸들러 등록
        registry.addResourceHandler(resourceHandler) // 논리적 경로: /images/**
                .addResourceLocations(fileUri);    // 물리적 경로: file:/C:/project/uploads/
    }
}