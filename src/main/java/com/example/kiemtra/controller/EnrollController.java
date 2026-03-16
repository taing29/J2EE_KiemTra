package com.example.kiemtra.controller;

import com.example.kiemtra.service.EnrollmentService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/enroll")
@RequiredArgsConstructor
public class EnrollController {

    private final EnrollmentService enrollmentService;

    // Câu 6: Đăng ký học phần
    @PostMapping("/{courseId}")
    public String enroll(@PathVariable Long courseId,
                         Authentication authentication,
                         RedirectAttributes redirectAttributes) {
        String username = authentication.getName();
        String message = enrollmentService.enrollCourse(username, courseId);
        redirectAttributes.addFlashAttribute("enrollMsg", message);
        return "redirect:/home";
    }

    // Câu 7: Xem học phần đã đăng ký
    @GetMapping("/my-courses")
    public String myCourses(Authentication authentication, Model model) {
        String username = authentication.getName();
        model.addAttribute("enrollments", enrollmentService.getMyCourses(username));
        return "student/my-courses";
    }
}