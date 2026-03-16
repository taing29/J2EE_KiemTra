package com.example.kiemtra.service;

import com.example.kiemtra.entity.Course;
import com.example.kiemtra.entity.Enrollment;
import com.example.kiemtra.entity.Student;
import com.example.kiemtra.repository.CourseRepository;
import com.example.kiemtra.repository.EnrollmentRepository;
import com.example.kiemtra.repository.StudentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class EnrollmentService {

    private final EnrollmentRepository enrollmentRepository;
    private final StudentRepository studentRepository;
    private final CourseRepository courseRepository;

    // Câu 6: Đăng ký học phần
    @Transactional
    public String enrollCourse(String username, Long courseId) {
        Student student = studentRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy sinh viên"));
        Course course = courseRepository.findById(courseId)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy học phần"));

        if (enrollmentRepository.existsByStudentAndCourse(student, course)) {
            return "Bạn đã đăng ký học phần này rồi!";
        }

        Enrollment enrollment = new Enrollment();
        enrollment.setStudent(student);
        enrollment.setCourse(course);
        enrollmentRepository.save(enrollment);
        return "Đăng ký học phần '" + course.getName() + "' thành công!";
    }

    // Câu 7: Danh sách học phần đã đăng ký
    public List<Enrollment> getMyCourses(String username) {
        Student student = studentRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy sinh viên"));
        return enrollmentRepository.findByStudent(student);
    }

    // Kiểm tra đã đăng ký chưa (dùng trong view)
    public boolean isEnrolled(String username, Long courseId) {
        try {
            Student student = studentRepository.findByUsername(username).orElse(null);
            Course course = courseRepository.findById(courseId).orElse(null);
            if (student == null || course == null) return false;
            return enrollmentRepository.existsByStudentAndCourse(student, course);
        } catch (Exception e) {
            return false;
        }
    }
}