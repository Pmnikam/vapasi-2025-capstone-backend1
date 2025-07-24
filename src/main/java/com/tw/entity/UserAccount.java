package com.tw.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "user_account")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserAccount {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "login_id")
    private Long loginId;

    @Column(length = 50, nullable = false)
    private String name;

    @Column(nullable = false, unique = true)
    private String email;

    @Column(nullable = false)
    private String password;

    @Column(name = "user_role", nullable = false)
    private String role;

    @OneToOne(mappedBy = "loginAccount", cascade = CascadeType.ALL)
    private CustomerProfile customerProfile;

    public UserAccount(String name, String email, String password, String role) {
        this.role = role;
        this.name = name;
        this.email = email;
        this.password = password;
    }

}
