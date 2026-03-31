package com.portfolio.blog.services.impl;

import com.portfolio.blog.domain.dto.user.User;
import com.portfolio.blog.domain.entities.Role;
import com.portfolio.blog.domain.entities.UserEntity;
import com.portfolio.blog.exceptions.ConflictException;
import com.portfolio.blog.exceptions.ForbiddenException;
import com.portfolio.blog.exceptions.ResourceNotFoundException;
import com.portfolio.blog.mappers.UserMapper;
import com.portfolio.blog.repositories.UserRepository;
import com.portfolio.blog.services.AdministrationServiceInterface;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class AdministrationService implements AdministrationServiceInterface {

    private final UserRepository repository;
    private final UserMapper userMapper;
    private final UserService userService;

    private void verifyRole() {
        if(userService.getUserFromSecurityContextHolder().getRole().equals(Role.USER) ) {
            throw new ForbiddenException("Action forbidden");
        }
    }
    @Override
    public User block(UUID uuid) {

        verifyRole(); //Exception is thrown if role is not "ADMIN"
        UserEntity user = repository.findById(uuid).orElseThrow(() -> new ResourceNotFoundException("User is not found"));

        if(user.getRole().equals(Role.ADMIN))
            throw new ForbiddenException("Not allowed to block administrators.");

        if(user.isNonLocked() == false) {
            log.warn("Attempted to block user that is already blocked");
            throw new ConflictException("User is already blocked");
        }
        user.setNonLocked(false);

        return userMapper.toDto(repository.save(user));
    }
}
