package com.example.kiemtra.repository;

import com.example.kiemtra.entity.Category;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CategoryRepository extends JpaRepository<Category, Long> {
}