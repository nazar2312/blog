package com.portfolio.blog.services.impl;

import com.portfolio.blog.domain.entities.PostEntity;
import com.portfolio.blog.domain.entities.Role;
import com.portfolio.blog.domain.entities.UserEntity;
import com.portfolio.blog.exceptions.ForbiddenException;
import com.portfolio.blog.services.AuthorizationServiceInterface;
import com.portfolio.blog.services.UserServiceInterface;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class AuthorizationService implements AuthorizationServiceInterface {

    private final UserServiceInterface userService;

    //  If Role.ADMIN user is allowed to delete/update posts of any user.
    //  In case if Role.USER, user is only allowed to delete/update his own posts.

    @Override
    public void authorizeUpdatingOrDeleting(PostEntity postToUpdate, UserEntity currentUser) {

        if (currentUser == null
                ||!currentUser.getId().equals(postToUpdate.getAuthor().getId()) && !currentUser.getRole().equals(Role.ADMIN)
        ) {
            log.warn("User attempted to update post of the user [ {} ]", postToUpdate.getAuthor().getEmail());
            throw new ForbiddenException("Only updating of own posts is permitted");
        }

    }

    @Override
    public void authorizeCategoryOrTagDeleting() {

        UserEntity currentUser = userService.getUserFromSecurityContextHolder();
        if (currentUser == null
                || !currentUser.getRole().equals(Role.ADMIN)) throw new ForbiddenException("Action is not permitted");

    }

}











