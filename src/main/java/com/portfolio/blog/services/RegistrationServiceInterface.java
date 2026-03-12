package com.portfolio.blog.services;

import com.portfolio.blog.domain.dto.authentication.RegistrationRequest;

public interface RegistrationServiceInterface {

    void createUser(RegistrationRequest request);

}
