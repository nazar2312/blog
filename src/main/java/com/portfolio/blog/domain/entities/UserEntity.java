package com.portfolio.blog.domain.entities;

import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Entity
@Table(name = "users")
@NoArgsConstructor
@AllArgsConstructor
@Data
@Builder
public class UserEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(updatable = false, nullable = false)
    private UUID id;

    @Column(nullable = false)
    private String username;

    @Email
    @Column(nullable = false, unique = true)
    private String email;

    @Column(nullable = false)
    private String password;

    @Column(nullable = false, updatable = false)
    private LocalDateTime created;

    @PrePersist
    protected void onCreate() {
        this.created = LocalDateTime.now();
        this.updated = LocalDateTime.now();
    }

    @Column(nullable = false)
    private LocalDateTime updated;

    @PreUpdate
    protected void onUpdate() {
        this.updated = LocalDateTime.now();
    }

    /*
        ONE user can have MANY posts
     */
    @OneToMany(mappedBy = "author", cascade = {
            CascadeType.REMOVE, CascadeType.PERSIST
    })
    private List<PostEntity> posts;


}
