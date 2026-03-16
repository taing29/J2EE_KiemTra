package com.example.kiemtra.security;

import com.example.kiemtra.entity.Role;
import com.example.kiemtra.entity.Student;
import com.example.kiemtra.repository.RoleRepository;
import com.example.kiemtra.repository.StudentRepository;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

@Slf4j
@Component
@RequiredArgsConstructor
public class OAuth2LoginSuccessHandler implements AuthenticationSuccessHandler {

    private final StudentRepository studentRepository;
    private final RoleRepository roleRepository;

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request,
                                        HttpServletResponse response,
                                        Authentication authentication) throws IOException, ServletException {
        try {
            log.info("=== OAuth2 Login Success Handler triggered ===");
            log.info("Authentication class: {}", authentication.getClass().getName());

            OAuth2User oAuth2User = (OAuth2User) authentication.getPrincipal();

            String email    = oAuth2User.getAttribute("email");
            String googleId = oAuth2User.getAttribute("sub");
            String name     = oAuth2User.getAttribute("name");

            log.info("Google user info - email: {}, googleId: {}, name: {}", email, googleId, name);
            log.info("OAuth2User attributes: {}", oAuth2User.getAttributes());

            if (email == null || googleId == null) {
                log.error("Email hoặc googleId null! email={}, googleId={}", email, googleId);
                response.sendRedirect("/auth/login?oauth2error=true");
                return;
            }

            // Tìm hoặc tạo student
            Student student = studentRepository
                    .findByOauth2ProviderAndOauth2Id("google", googleId)
                    .orElseGet(() -> {
                        log.info("Không tìm thấy theo googleId, tìm theo email: {}", email);
                        return studentRepository
                                .findByEmail(email)
                                .orElseGet(() -> {
                                    log.info("Không tìm thấy email, tạo student mới...");
                                    return createNewStudent(email, googleId);
                                });
                    });

            log.info("Student found/created: id={}, username={}, roles={}",
                    student.getStudentId(), student.getUsername(), student.getRoles());

            // Cập nhật oauth2Id nếu chưa có
            if (student.getOauth2Id() == null) {
                student.setOauth2Provider("google");
                student.setOauth2Id(googleId);
                studentRepository.save(student);
                log.info("Đã cập nhật oauth2Id cho student: {}", student.getUsername());
            }

            // Tạo CustomUserDetails và set lại Authentication
            CustomUserDetails userDetails = new CustomUserDetails(student);
            log.info("Authorities: {}", userDetails.getAuthorities());

            OAuth2AuthenticationToken newAuth = new OAuth2AuthenticationToken(
                    oAuth2User,
                    userDetails.getAuthorities(),
                    ((OAuth2AuthenticationToken) authentication).getAuthorizedClientRegistrationId()
            );

            SecurityContextHolder.getContext().setAuthentication(newAuth);
            request.getSession().setAttribute(
                    "SPRING_SECURITY_CONTEXT",
                    SecurityContextHolder.getContext()
            );

            log.info("=== OAuth2 Login thành công, redirect về /home ===");
            response.sendRedirect("/home");

        } catch (Exception e) {
            log.error("=== LỖI trong OAuth2LoginSuccessHandler ===", e);
            log.error("Exception message: {}", e.getMessage());
            response.sendRedirect("/auth/login?oauth2error=true");
        }
    }

    private Student createNewStudent(String email, String googleId) {
        Student newStudent = new Student();

        String base = email.split("@")[0].replaceAll("[^a-zA-Z0-9_]", "");
        String username = base;
        if (studentRepository.existsByUsername(username)) {
            username = base + "_" + UUID.randomUUID().toString().substring(0, 5);
        }
        log.info("Tạo student mới với username: {}, email: {}", username, email);

        newStudent.setUsername(username);
        newStudent.setEmail(email);
        newStudent.setPassword("");
        newStudent.setOauth2Provider("google");
        newStudent.setOauth2Id(googleId);

        Role studentRole = roleRepository.findByName("STUDENT")
                .orElseGet(() -> roleRepository.save(new Role("STUDENT")));
        Set<Role> roles = new HashSet<>();
        roles.add(studentRole);
        newStudent.setRoles(roles);

        Student saved = studentRepository.save(newStudent);
        log.info("Đã tạo student mới: id={}", saved.getStudentId());
        return saved;
    }
}