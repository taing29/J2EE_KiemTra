package com.example.kiemtra.service;

import com.example.kiemtra.dto.RegisterDTO;
import com.example.kiemtra.entity.Role;
import com.example.kiemtra.entity.Student;
import com.example.kiemtra.repository.RoleRepository;
import com.example.kiemtra.repository.StudentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashSet;
import java.util.Set;

@Service
@RequiredArgsConstructor
public class StudentService {

    private final StudentRepository studentRepository;
    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;

    @Transactional
    public Student register(RegisterDTO dto) {
        if (studentRepository.existsByUsername(dto.getUsername())) {
            throw new RuntimeException("Username đã tồn tại!");
        }
        if (studentRepository.existsByEmail(dto.getEmail())) {
            throw new RuntimeException("Email đã được sử dụng!");
        }

        Student student = new Student();
        student.setUsername(dto.getUsername());
        student.setPassword(passwordEncoder.encode(dto.getPassword()));
        student.setEmail(dto.getEmail());

        // Câu 3: Quyền mặc định là STUDENT
        Role studentRole = roleRepository.findByName("STUDENT")
                .orElseGet(() -> roleRepository.save(new Role("STUDENT")));
        Set<Role> roles = new HashSet<>();
        roles.add(studentRole);
        student.setRoles(roles);

        return studentRepository.save(student);
    }

    public Student findByUsername(String username) {
        return studentRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy student"));
    }
}