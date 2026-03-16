package com.example.kiemtra.config;

import com.example.kiemtra.entity.Category;
import com.example.kiemtra.entity.Course;
import com.example.kiemtra.entity.Role;
import com.example.kiemtra.entity.Student;
import com.example.kiemtra.repository.CategoryRepository;
import com.example.kiemtra.repository.CourseRepository;
import com.example.kiemtra.repository.RoleRepository;
import com.example.kiemtra.repository.StudentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Component
@RequiredArgsConstructor
public class DataInitializer implements CommandLineRunner {

    private final RoleRepository roleRepository;
    private final StudentRepository studentRepository;
    private final CategoryRepository categoryRepository;
    private final CourseRepository courseRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    public void run(String... args) {
        // Tạo Roles nếu chưa có
        Role adminRole = roleRepository.findByName("ADMIN")
                .orElseGet(() -> roleRepository.save(new Role("ADMIN")));
        Role studentRole = roleRepository.findByName("STUDENT")
                .orElseGet(() -> roleRepository.save(new Role("STUDENT")));

        // Tạo tài khoản ADMIN mặc định
        if (!studentRepository.existsByUsername("admin")) {
            Student admin = new Student();
            admin.setUsername("admin");
            admin.setPassword(passwordEncoder.encode("admin123"));
            admin.setEmail("admin@edu.vn");
            Set<Role> roles = new HashSet<>();
            roles.add(adminRole);
            admin.setRoles(roles);
            studentRepository.save(admin);
            System.out.println("==> Tạo tài khoản ADMIN: admin / admin123");
        }

        // Tạo tài khoản STUDENT mẫu
        if (!studentRepository.existsByUsername("student1")) {
            Student student = new Student();
            student.setUsername("student1");
            student.setPassword(passwordEncoder.encode("student123"));
            student.setEmail("student1@edu.vn");
            Set<Role> roles = new HashSet<>();
            roles.add(studentRole);
            student.setRoles(roles);
            studentRepository.save(student);
            System.out.println("==> Tạo tài khoản STUDENT: student1 / student123");
        }

        // Tạo Categories mẫu
        if (categoryRepository.count() == 0) {
            List<Category> categories = List.of(
                    new Category(null, "Công nghệ thông tin", null),
                    new Category(null, "Kinh tế", null),
                    new Category(null, "Ngoại ngữ", null),
                    new Category(null, "Khoa học tự nhiên", null)
            );
            categoryRepository.saveAll(categories);

            // Tạo Courses mẫu
            Category cntt = categories.get(0);
            Category kt = categories.get(1);
            Category nn = categories.get(2);
            Category khtn = categories.get(3);

            List<Course> courses = List.of(
                    new Course(null, "Lập trình Java", null, 3, "GV. Nguyễn Văn A", cntt),
                    new Course(null, "Cơ sở dữ liệu", null, 3, "GV. Trần Thị B", cntt),
                    new Course(null, "Lập trình Web", null, 3, "GV. Lê Văn C", cntt),
                    new Course(null, "Cấu trúc dữ liệu & Giải thuật", null, 4, "GV. Phạm Thị D", cntt),
                    new Course(null, "Mạng máy tính", null, 3, "GV. Hoàng Văn E", cntt),
                    new Course(null, "Kinh tế vi mô", null, 3, "GV. Vũ Thị F", kt),
                    new Course(null, "Marketing căn bản", null, 3, "GV. Đặng Văn G", kt),
                    new Course(null, "Tiếng Anh 1", null, 3, "GV. Ngô Thị H", nn),
                    new Course(null, "Toán cao cấp", null, 4, "GV. Bùi Văn I", khtn),
                    new Course(null, "Xác suất thống kê", null, 3, "GV. Lý Thị K", khtn),
                    new Course(null, "Spring Boot nâng cao", null, 3, "GV. Trịnh Văn L", cntt),
                    new Course(null, "Trí tuệ nhân tạo", null, 4, "GV. Đinh Thị M", cntt)
            );
            courseRepository.saveAll(courses);
            System.out.println("==> Đã tạo " + courses.size() + " học phần mẫu");
        }
    }
}