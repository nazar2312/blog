package com.portfolio.blog.repositories;

import com.portfolio.blog.domain.entities.*;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface PostRepository extends JpaRepository<PostEntity, UUID>, JpaSpecificationExecutor<PostEntity> {

        // Method that fetches
    @EntityGraph(attributePaths = {"author", "category", "tags"})
    List<PostEntity> findByIdIn(List<UUID> uuids);
}