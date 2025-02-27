package com.keduit.shop.config;


import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import javax.validation.Valid;

@Configuration
public class WebMvcConfig implements WebMvcConfigurer {

//    application.properties에 설정함 "uploadPath" 프로퍼티의 값을 읽어옴
    @Value("${uploadPath}")
    String uploadPath;

//    url이 /images/** 로 시작하는 경우(접근 경로) uploadPath에 설정한 폴더를 기준으로 파일을 읽도록 설정
//    addResourceLocations(uploadPath) : 로컬 컴퓨터에서 읽어올 루트 경로를 설정
//    uploadPath : 실제 파일 경로
    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        registry.addResourceHandler("/images/**")
                .addResourceLocations(uploadPath);
    }
}
