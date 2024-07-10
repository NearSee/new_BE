package org.example.near.domain.User;


import jakarta.persistence.*;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.sql.Timestamp;

@Entity
@Table(name = "USER")
@Data
@Getter
@Setter
@NoArgsConstructor  // 기본 생성자를 자동으로 생성해주는 Lombok 어노테이션
public class User {

    @Id
    @Column(name = "user_id", nullable = false)
    private Long userId; // 카카오에서 받아온 사용자 ID를 문자열로 저장

    @Column(nullable = false)
    private String name;

    private String email;

    @Column
    private String profile_image;

    @Enumerated(EnumType.STRING)
    private UserRole role;

    @Column(nullable = false, updatable = false, columnDefinition = "TIMESTAMP DEFAULT CURRENT_TIMESTAMP")
    private Timestamp createdAt;

    @Column(nullable = false, columnDefinition = "TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP")
    private Timestamp updatedAt;

    // 생성자
    public User(Long userId, String name, String email, UserRole role) {
        this.userId = userId;
        this.name = name;
        this.email = email;
        this.role = role;
    }
}
