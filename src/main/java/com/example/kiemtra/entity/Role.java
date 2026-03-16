package com.example.kiemtra.entity;

import jakarta.persistence.*;
import lombok.*;
import java.util.Set;

@Entity
@Table(name = "role")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor
public class Role {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "role_id")
    private Long roleId;

    @Column(nullable = false, unique = true, length = 50)
    private String name; // "ADMIN" hoặc "STUDENT"

    @ManyToMany(mappedBy = "roles")
    private Set<Student> students;

    public Role(String name) {
        this.name = name;
    }
}