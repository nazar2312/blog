package com.portfolio.blog.services.impl;

import com.portfolio.blog.domain.entities.UserEntity;
import com.portfolio.blog.repositories.UserRepository;
import com.portfolio.blog.services.UserServiceInterface;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;


@Service
@RequiredArgsConstructor
public class UserService implements UserServiceInterface {

    private final UserRepository repository;

    @Override
    public UserEntity extractUserFromSecurityContextHolder() {

        String email = SecurityContextHolder.getContext()
                .getAuthentication()
                .getName();

        return repository.findByEmail(email)
                .orElseThrow(EntityNotFoundException::new);
    }


}
