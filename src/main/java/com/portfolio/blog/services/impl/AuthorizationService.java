package com.portfolio.blog.services.impl;

import com.portfolio.blog.domain.entities.PostEntity;
import com.portfolio.blog.domain.entities.Role;
import com.portfolio.blog.domain.entities.UserEntity;
import com.portfolio.blog.repositories.PostRepository;
import com.portfolio.blog.services.AuthorizationServiceInterface;
import com.portfolio.blog.services.UserServiceInterface;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class AuthorizationService implements AuthorizationServiceInterface {

    private final UserServiceInterface userService;
    private final PostRepository repository;

    @Override
    public boolean canDelete(UUID uuid) {

        Optional<PostEntity> postToDelete = repository.findById(uuid);
        if(postToDelete.isEmpty()) throw new EntityNotFoundException();
        UUID authorUUID = postToDelete.get().getAuthor().getId();

        UserEntity currentUser = userService.extractUserFromSecurityContextHolder();

        boolean canDelete = currentUser.getRole().equals(Role.ADMIN)
                || currentUser.getId().equals(authorUUID) && currentUser.getRole().equals(Role.USER);

        return canDelete;
    }

    @Override
    public boolean canUpdate(UUID postToUpdate) {
        return false;
    }
}
