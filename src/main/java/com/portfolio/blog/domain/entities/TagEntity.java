package com.portfolio.blog.domain.entities;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Set;
import java.util.UUID;

@Entity
@Table(name = "tags")
@NoArgsConstructor
@AllArgsConstructor
@Data
public class TagEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column( nullable = false, updatable = false, unique = true)
    private UUID id;

    @Column(nullable = false, updatable = false)
    private String name;

    @ManyToMany(mappedBy = "tags")
    private Set<PostEntity> posts;

}
