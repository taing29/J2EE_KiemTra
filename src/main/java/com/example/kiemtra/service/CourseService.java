package com.example.kiemtra.service;

import com.example.kiemtra.dto.CourseDTO;
import com.example.kiemtra.entity.Category;
import com.example.kiemtra.entity.Course;
import com.example.kiemtra.repository.CategoryRepository;
import com.example.kiemtra.repository.CourseRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class CourseService {

    private final CourseRepository courseRepository;
    private final CategoryRepository categoryRepository;

    private static final String UPLOAD_DIR = System.getProperty("user.home") + File.separator + "eduportal-uploads";

    public Page<Course> getAllCourses(int page, String keyword) {
        Pageable pageable = PageRequest.of(page, 5, Sort.by("id").descending());
        if (keyword != null && !keyword.isBlank()) {
            return courseRepository.findByNameContainingIgnoreCase(keyword, pageable);
        }
        return courseRepository.findAll(pageable);
    }

    public Course findById(Long id) {
        return courseRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy học phần ID: " + id));
    }

    public List<Course> findAll() {
        return courseRepository.findAll();
    }

    @Transactional
    public Course createCourse(CourseDTO dto) throws IOException {
        Course course = new Course();
        mapDtoToCourse(dto, course);
        return courseRepository.save(course);
    }

    @Transactional
    public Course updateCourse(Long id, CourseDTO dto) throws IOException {
        Course course = findById(id);
        mapDtoToCourse(dto, course);
        return courseRepository.save(course);
    }

    @Transactional
    public void deleteCourse(Long id) {
        courseRepository.deleteById(id);
    }

    private void mapDtoToCourse(CourseDTO dto, Course course) throws IOException {
        course.setName(dto.getName());
        course.setCredits(dto.getCredits());
        course.setLecturer(dto.getLecturer());

        if (dto.getCategoryId() != null) {
            Category category = categoryRepository.findById(dto.getCategoryId()).orElse(null);
            course.setCategory(category);
        }

        MultipartFile imageFile = dto.getImageFile();

        if (imageFile != null && !imageFile.isEmpty()) {
            // ✅ Có ảnh mới → lưu ảnh mới, GHI ĐÈ lên field image
            String newImageName = saveImage(imageFile);
            course.setImage(newImageName);
        } else {
            // ✅ Không upload ảnh mới → GIỮ ảnh cũ từ existingImage
            String existing = dto.getExistingImage();
            if (existing != null && !existing.isBlank()) {
                course.setImage(existing);
            }
            // Nếu cả 2 đều null/blank → không thay đổi image (giữ nguyên trong entity)
        }
    }

    private String saveImage(MultipartFile file) throws IOException {
        Path uploadPath = Paths.get(UPLOAD_DIR);
        if (!Files.exists(uploadPath)) {
            Files.createDirectories(uploadPath);
        }

        String ext = "";
        String originalName = file.getOriginalFilename();
        if (originalName != null && originalName.contains(".")) {
            ext = originalName.substring(originalName.lastIndexOf("."));
        }
        String fileName = UUID.randomUUID() + ext;

        Path filePath = uploadPath.resolve(fileName);
        Files.copy(file.getInputStream(), filePath, StandardCopyOption.REPLACE_EXISTING);

        return fileName;
    }

    public static String getUploadDir() {
        return UPLOAD_DIR;
    }
}