package com.portfolio.blog.services;

import com.portfolio.blog.domain.entities.PostEntity;
import com.portfolio.blog.domain.entities.UserEntity;


public interface AuthorizationServiceInterface {

    void authorizeUpdatingOrDeleting(PostEntity postToUpdate, UserEntity currentUser);

    void authorizeCategoryOrTagDeleting();
}

