package com.portfolio.blog.services.impl;

import com.portfolio.blog.domain.dto.registration.RegistrationRequest;
import com.portfolio.blog.domain.entities.UserEntity;
import com.portfolio.blog.repositories.UserRepository;
import com.portfolio.blog.services.RegistrationServiceInterface;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class RegistrationService implements RegistrationServiceInterface {

    private final UserRepository repository;
    private final PasswordEncoder passwordEncoder;

    @Override
    public void createUser(RegistrationRequest request) {

        if(repository.findByEmail(request.getEmail()).isEmpty()) {

            String encodedPassword = passwordEncoder.encode(request.getPassword());

            UserEntity userEntity = new UserEntity(
                    null,
                    request.getUsername(),
                    request.getEmail(),
                    encodedPassword,
                    null,
                    null,
                    null
            );
            repository.save(userEntity);
        } else throw new IllegalArgumentException("User is already exists");
    }
}
