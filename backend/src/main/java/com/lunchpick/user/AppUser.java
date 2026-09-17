package com.lunchpick.user;

import jakarta.persistence.*;
import java.time.Instant;

@Entity
@Table(name = "users", uniqueConstraints = @UniqueConstraint(name = "uk_users_email", columnNames = "email"))
public class AppUser {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY) private Long id;
    @Column(nullable = false, length = 80) private String nickname;
    @Column(nullable = false, length = 190) private String email;
    @Column(nullable = false, length = 100) private String passwordHash;
    @Column(nullable = false, updatable = false) private Instant createdAt;
    protected AppUser() {}
    public AppUser(String nickname, String email, String passwordHash) {
        this.nickname = nickname; this.email = email.toLowerCase(); this.passwordHash = passwordHash; this.createdAt = Instant.now();
    }
    public Long getId() { return id; }
    public String getNickname() { return nickname; }
    public String getEmail() { return email; }
    public String getPasswordHash() { return passwordHash; }
}
