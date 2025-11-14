package com.musical.ticket.config;
//정적 리소스 매핑(브라우저가 /images/** url로 요청을 보냈을 때 서버가 실제 물리 경로에서 파일을 찾아 반환하도록 매핑)
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;


@Configuration
public class WebConfig implements WebMvcConfigurer{

    @Value("${file.resource-handler}")
    private String resourceHandler;
    
    @Value("${file.upload-dir}")
    private String uploadDir;

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        // /images/** 요청을 uploadDir 폴더로 매핑
        registry.addResourceHandler(resourceHandler)
                .addResourceLocations("file:" + uploadDir + "/");
    }
}
