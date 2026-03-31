package com.portfolio.blog.services;

import com.portfolio.blog.domain.dto.user.User;

import java.util.UUID;

public interface AdministrationServiceInterface {

    User block(UUID uuid);
}
