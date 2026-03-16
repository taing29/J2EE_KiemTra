package com.example.kiemtra.repository;

import com.example.kiemtra.entity.Course;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CourseRepository extends JpaRepository<Course, Long> {
    // Câu 1: phân trang tất cả
    Page<Course> findAll(Pageable pageable);

    // Câu 8: tìm kiếm theo tên (không phân biệt hoa thường)
    Page<Course> findByNameContainingIgnoreCase(String name, Pageable pageable);
}