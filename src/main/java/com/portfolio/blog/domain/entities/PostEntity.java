package com.portfolio.blog.domain.entities;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.Set;
import java.util.UUID;

@Entity
@Table(name = "posts")
@NoArgsConstructor
@AllArgsConstructor
@Data
public class PostEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(nullable = false, updatable = false)
    private UUID id;

    @Column(nullable = false)
    private String title;

    @Column(nullable = false)
    private String content;

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
        MANY posts belong to ONE user;
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "author_id", nullable = false )
    private UserEntity author;

    @Column(nullable = false)
    private StatusEntity status;

    @Column(nullable = false)
    private Integer readingTime;


    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "post_category", nullable = false)
    private CategoryEntity category;

    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(
            name = "post_tags",
            joinColumns = @JoinColumn(name = "post_id"),
            inverseJoinColumns = @JoinColumn(name = "tag_id")
    )
    private Set<TagEntity> tags;





}
