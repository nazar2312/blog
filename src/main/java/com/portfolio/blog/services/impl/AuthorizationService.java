package com.portfolio.blog.services.impl;

import com.portfolio.blog.domain.entities.PostEntity;
import com.portfolio.blog.domain.entities.Role;
import com.portfolio.blog.domain.entities.UserEntity;
import com.portfolio.blog.services.AuthorizationServiceInterface;
import com.portfolio.blog.services.UserServiceInterface;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.access.AuthorizationServiceException;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class AuthorizationService implements AuthorizationServiceInterface {

    private final UserServiceInterface userService;

    //  If Role.ADMIN user is allowed to delete/update posts of any user.
    //  In case if Role.USER, user is only allowed to delete/update his own posts.

    @Override
    public void authorizeDeleting(PostEntity postToDelete, UserEntity currentUser) {

        if(currentUser.getRole().equals(Role.ADMIN)
                || currentUser.getId().equals(postToDelete.getAuthor())
                && currentUser.getRole().equals(Role.USER)
        ){
            return;
          // Authorized
        } else {
            log.warn("User: {} attempted to delete post, action unauthorized", currentUser.getEmail());
            throw new AuthorizationServiceException("");
        }

    }

    @Override
    public void authorizeUpdating(PostEntity postToUpdate, UserEntity currentUser) {

        if(currentUser.getRole().equals(Role.ADMIN)
                || currentUser.getId().equals(postToUpdate.getAuthor())
                && currentUser.getRole().equals(Role.USER)
        ){
            return;
            // Authorized
        } else {
            log.warn("User: {} attempted to update post, action unauthorized", currentUser.getEmail());
            throw new AuthorizationServiceException("");
        }
    }

    @Override
    public void authorizeCategoryOrTagDeleting(){
        if(!userService.extractUserFromSecurityContextHolder().getRole().equals(Role.ADMIN)) {
            throw new AuthorizationServiceException("");
        }
    }
}
