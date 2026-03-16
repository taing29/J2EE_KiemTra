package com.example.kiemtra.controller;

import com.example.kiemtra.dto.RegisterDTO;
import com.example.kiemtra.service.StudentService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthController {

    private final StudentService studentService;

    // Câu 5: Trang đăng nhập
    @GetMapping("/login")
    public String loginPage(@RequestParam(required = false) String error,
                            @RequestParam(required = false) String logout,
                            Model model) {
        if (error != null) model.addAttribute("errorMsg", "Tên đăng nhập hoặc mật khẩu không đúng!");
        if (logout != null) model.addAttribute("logoutMsg", "Đăng xuất thành công!");
        return "auth/login";
    }

    // Câu 3: Trang đăng ký
    @GetMapping("/register")
    public String registerPage(Model model) {
        model.addAttribute("registerDTO", new RegisterDTO());
        return "auth/register";
    }

    // Câu 3: Xử lý đăng ký - nhận trực tiếp từng field
    @PostMapping("/register")
    public String register(@RequestParam String username,
                           @RequestParam String password,
                           @RequestParam String email,
                           Model model,
                           RedirectAttributes redirectAttributes) {
        // Validate đơn giản
        if (username == null || username.trim().length() < 3) {
            model.addAttribute("errorMsg", "Username phải có ít nhất 3 ký tự!");
            return "auth/register";
        }
        if (password == null || password.trim().length() < 6) {
            model.addAttribute("errorMsg", "Password phải có ít nhất 6 ký tự!");
            return "auth/register";
        }
        if (email == null || !email.contains("@")) {
            model.addAttribute("errorMsg", "Email không hợp lệ!");
            return "auth/register";
        }

        try {
            RegisterDTO dto = new RegisterDTO();
            dto.setUsername(username.trim());
            dto.setPassword(password);
            dto.setEmail(email.trim());
            studentService.register(dto);
            redirectAttributes.addFlashAttribute("successMsg", "Đăng ký thành công! Vui lòng đăng nhập.");
            return "redirect:/auth/login";
        } catch (RuntimeException e) {
            model.addAttribute("errorMsg", e.getMessage());
            return "auth/register";
        }
    }
}