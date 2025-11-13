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
    public void addResourceHandlers(ResourceHandlerRegistry registry){
        registry.addResourceHandler(resourceHandler).addResourceLocations("file:///"+uploadDir);
    }

    /*
     * ----CORS설정 추가-----
     * React 프론트엔드(localhost:5173)에서의 API요청을 허용하기 위한 CORS설정
     */
    // @Override
    // public void addCorsMappings(CorsRegistry registry){
    //     registry.addMapping("/api/**") //지금까지 만들어둔 API의 기본 경로인 /api/**
    //         .allowedOrigins("http://localhost:5173") // React앱의 주소
    //         .allowedMethods("GET", "POST", "PUT", "DELETE", "OPTIONS") //허용할 HTTP메서드 
    //         .allowedHeaders("*") // 허용할 헤더
    //         .allowCredentials(true) //인증 정보(쿠키, 토큰 등) 허용
    //         .maxAge(3600); //OPTIONS요청 -> 캐시 시간(초)
    // }
}
