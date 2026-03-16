package com.example.kiemtra.controller;

import com.example.kiemtra.dto.CourseDTO;
import com.example.kiemtra.entity.Course;
import com.example.kiemtra.repository.CategoryRepository;
import com.example.kiemtra.service.CourseService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/admin")
@RequiredArgsConstructor
public class AdminCourseController {

    private final CourseService courseService;
    private final CategoryRepository categoryRepository;

    @GetMapping({"", "/"})
    public String dashboard() {
        return "redirect:/admin/courses";
    }

    @GetMapping("/courses")
    public String listCourses(Model model) {
        model.addAttribute("courses", courseService.findAll());
        return "admin/course-list";
    }

    @GetMapping("/courses/add")
    public String addForm(Model model) {
        model.addAttribute("courseDTO", new CourseDTO());
        model.addAttribute("categories", categoryRepository.findAll());
        return "admin/course-form";
    }

    @PostMapping("/courses/add")
    public String addCourse(@RequestParam String name,
                            @RequestParam Integer credits,
                            @RequestParam String lecturer,
                            @RequestParam(required = false) Long categoryId,
                            @RequestParam(required = false) MultipartFile imageFile,
                            RedirectAttributes redirectAttributes) {
        try {
            CourseDTO dto = new CourseDTO();
            dto.setName(name);
            dto.setCredits(credits);
            dto.setLecturer(lecturer);
            dto.setCategoryId(categoryId);
            dto.setImageFile(imageFile);
            courseService.createCourse(dto);
            redirectAttributes.addFlashAttribute("successMsg", "Thêm học phần thành công!");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMsg", "Lỗi: " + e.getMessage());
        }
        return "redirect:/admin/courses";
    }

    @GetMapping("/courses/edit/{id}")
    public String editForm(@PathVariable Long id, Model model) {
        Course course = courseService.findById(id);
        CourseDTO dto = new CourseDTO();
        dto.setName(course.getName());
        dto.setCredits(course.getCredits());
        dto.setLecturer(course.getLecturer());
        dto.setExistingImage(course.getImage());
        if (course.getCategory() != null) dto.setCategoryId(course.getCategory().getId());

        model.addAttribute("courseDTO", dto);
        model.addAttribute("course", course);
        model.addAttribute("categories", categoryRepository.findAll());
        return "admin/course-edit";
    }

    @PostMapping("/courses/edit/{id}")
    public String updateCourse(@PathVariable Long id,
                               @RequestParam String name,
                               @RequestParam Integer credits,
                               @RequestParam String lecturer,
                               @RequestParam(required = false) Long categoryId,
                               @RequestParam(required = false) MultipartFile imageFile,
                               // ✅ Nhận existingImage từ hidden input trong form
                               @RequestParam(required = false) String existingImage,
                               RedirectAttributes redirectAttributes) {
        try {
            CourseDTO dto = new CourseDTO();
            dto.setName(name);
            dto.setCredits(credits);
            dto.setLecturer(lecturer);
            dto.setCategoryId(categoryId);
            dto.setImageFile(imageFile);
            // ✅ Gán existingImage để CourseService giữ ảnh cũ nếu không upload mới
            dto.setExistingImage(existingImage);
            courseService.updateCourse(id, dto);
            redirectAttributes.addFlashAttribute("successMsg", "Cập nhật học phần thành công!");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMsg", "Lỗi: " + e.getMessage());
        }
        return "redirect:/admin/courses";
    }

    @PostMapping("/courses/delete/{id}")
    public String deleteCourse(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        try {
            courseService.deleteCourse(id);
            redirectAttributes.addFlashAttribute("successMsg", "Xóa học phần thành công!");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMsg", "Không thể xóa học phần này!");
        }
        return "redirect:/admin/courses";
    }
}