package com.example.userservice.jpa;

import lombok.Data;
import lombok.Getter;

import javax.persistence.*;
@Data
@Entity
@Table(name="users")
public class UserEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(nullable = false)
    private String email;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false, unique = true)
    private String userId;

    @Column(nullable = true)
    private String encryptedPwd;

}
