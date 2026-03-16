package com.example.kiemtra.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import org.springframework.web.multipart.MultipartFile;

@Data
public class CourseDTO {

    @NotBlank(message = "Tên học phần không được để trống")
    private String name;

    @NotNull(message = "Số tín chỉ không được để trống")
    @Min(value = 1, message = "Số tín chỉ tối thiểu là 1")
    private Integer credits;

    @NotBlank(message = "Tên giảng viên không được để trống")
    private String lecturer;

    private Long categoryId;

    // Ảnh upload mới
    private MultipartFile imageFile;

    // Ảnh cũ (khi update)
    private String existingImage;
}