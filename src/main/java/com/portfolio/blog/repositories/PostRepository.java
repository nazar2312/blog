package com.portfolio.blog.repositories;

import com.portfolio.blog.domain.entities.*;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Collection;
import java.util.List;
import java.util.UUID;

@Repository
public interface PostRepository extends JpaRepository<PostEntity, UUID> {

                    // Status must be "PUBLISHED" for methods below to avoid showing drafts to anonymous users;

    @EntityGraph(attributePaths = {"author", "category", "tags"})
    Page<PostEntity> findByStatus(StatusEntity status, Pageable pageable);


    @EntityGraph(attributePaths = {"author", "category", "tags"})
    List<PostEntity> findByStatusAndAuthorId(StatusEntity status, UUID authorId, Pageable pageable);

                   // Methods will return all published posts and drafts of authenticated user;

    @EntityGraph(attributePaths = {"author", "category", "tags"})
    List<PostEntity> findByStatusOrAuthor(StatusEntity status, UserEntity user, Pageable pageable);


    List<PostEntity> findByIdIn(Collection<UUID> ids);
}