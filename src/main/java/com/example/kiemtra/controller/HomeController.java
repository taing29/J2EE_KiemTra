package com.example.kiemtra.controller;

import com.example.kiemtra.entity.Course;
import com.example.kiemtra.service.CourseService;
import com.example.kiemtra.service.EnrollmentService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@Controller
@RequiredArgsConstructor
public class HomeController {

    private final CourseService courseService;
    private final EnrollmentService enrollmentService;

    @GetMapping({"/", "/home"})
    public String home(@RequestParam(defaultValue = "0") int page,
                       @RequestParam(required = false) String keyword,
                       Authentication authentication,
                       Model model) {

        // Câu 1 + 8: lấy danh sách có phân trang + tìm kiếm
        Page<Course> coursePage = courseService.getAllCourses(page, keyword);

        // Map trạng thái đã đăng ký (chỉ khi là STUDENT đã login)
        Map<Long, Boolean> enrolledMap = new HashMap<>();
        boolean isStudent = false;
        if (authentication != null && authentication.isAuthenticated()) {
            isStudent = authentication.getAuthorities()
                    .contains(new SimpleGrantedAuthority("ROLE_STUDENT"));
            if (isStudent) {
                String username = authentication.getName();
                for (Course c : coursePage.getContent()) {
                    enrolledMap.put(c.getId(), enrollmentService.isEnrolled(username, c.getId()));
                }
            }
        }

        model.addAttribute("coursePage", coursePage);
        model.addAttribute("currentPage", page);
        model.addAttribute("totalPages", coursePage.getTotalPages());
        model.addAttribute("keyword", keyword);
        model.addAttribute("enrolledMap", enrolledMap);
        model.addAttribute("isStudent", isStudent);

        return "home";
    }

    @GetMapping("/access-denied")
    public String accessDenied() {
        return "access-denied";
    }
}