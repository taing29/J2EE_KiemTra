package com.example.kiemtra.security;

import com.example.kiemtra.entity.Role;
import com.example.kiemtra.entity.Student;
import com.example.kiemtra.repository.RoleRepository;
import com.example.kiemtra.repository.StudentRepository;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

@Component
@RequiredArgsConstructor
public class OAuth2LoginSuccessHandler extends SimpleUrlAuthenticationSuccessHandler {

    private final StudentRepository studentRepository;
    private final RoleRepository roleRepository;

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request,
                                        HttpServletResponse response,
                                        Authentication authentication) throws IOException, ServletException {

        OAuth2User oAuth2User = (OAuth2User) authentication.getPrincipal();
        String email = oAuth2User.getAttribute("email");
        String name = oAuth2User.getAttribute("name");
        String googleId = oAuth2User.getAttribute("sub");

        // Tìm hoặc tạo student từ Google account
        Student student = studentRepository.findByOauth2ProviderAndOauth2Id("google", googleId)
                .orElseGet(() -> {
                    // Kiểm tra nếu email đã tồn tại thì liên kết
                    return studentRepository.findByEmail(email).orElseGet(() -> {
                        Student newStudent = new Student();
                        // Tạo username từ email
                        String username = email.split("@")[0];
                        // Đảm bảo username unique
                        if (studentRepository.existsByUsername(username)) {
                            username = username + "_" + UUID.randomUUID().toString().substring(0, 5);
                        }
                        newStudent.setUsername(username);
                        newStudent.setEmail(email);
                        newStudent.setPassword(""); // Không dùng password với OAuth2
                        newStudent.setOauth2Provider("google");
                        newStudent.setOauth2Id(googleId);

                        Role studentRole = roleRepository.findByName("STUDENT")
                                .orElseGet(() -> roleRepository.save(new Role("STUDENT")));
                        Set<Role> roles = new HashSet<>();
                        roles.add(studentRole);
                        newStudent.setRoles(roles);

                        return studentRepository.save(newStudent);
                    });
                });

        // Cập nhật oauth2Id nếu chưa có
        if (student.getOauth2Id() == null) {
            student.setOauth2Provider("google");
            student.setOauth2Id(googleId);
            studentRepository.save(student);
        }

        setDefaultTargetUrl("/home");
        super.onAuthenticationSuccess(request, response, authentication);
    }
}