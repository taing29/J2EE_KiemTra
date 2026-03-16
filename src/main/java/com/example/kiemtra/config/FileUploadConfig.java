package com.example.kiemtra.config;

import com.example.kiemtra.service.CourseService;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class FileUploadConfig implements WebMvcConfigurer {

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        // ✅ Trỏ /images/uploads/** tới thư mục user.home/eduportal-uploads/
        String uploadDir = CourseService.getUploadDir();
        registry.addResourceHandler("/images/uploads/**")
                .addResourceLocations("file:" + uploadDir + "/");
    }
}