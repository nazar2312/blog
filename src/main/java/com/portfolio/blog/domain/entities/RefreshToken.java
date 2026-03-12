package com.portfolio.blog.domain.entities;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "refresh_tokens")
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Builder
public class RefreshToken {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(nullable = false, updatable = false, unique = true)
    UUID id;

    @Column(nullable = false, updatable = false, unique = true)
    String token;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false, updatable = false)
    UserEntity user;

    @Column(name = "issued_at", nullable = false)
    LocalDateTime issuedAt;

    @Column(name = "expiring_at", nullable = false)
    LocalDateTime expiringAt;

    @PrePersist
    protected void prePersist(){
        this.issuedAt = LocalDateTime.now();
        this.expiringAt = issuedAt.plusDays(30);
    }

}
