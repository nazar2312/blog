package com.portfolio.blog.config;

import com.portfolio.blog.domain.entities.Role;
import com.portfolio.blog.domain.entities.UserEntity;
import com.portfolio.blog.repositories.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

// Class that generates admin user.
@Component
@RequiredArgsConstructor
public class DataInitializer implements ApplicationRunner {

    private final PasswordEncoder passwordEncoder;
    private final UserRepository userRepo;

    @Value("${admin.email}")
    private String email;

    @Value("${admin.password}")
    private String password;

    @Override
    public void run(ApplicationArguments args) throws Exception {

        if (userRepo.findByEmail(email).isEmpty()) {

            UserEntity user = UserEntity.builder()

                    .id(null)
                    .email(email)
                    .username("admin")
                    .password(passwordEncoder.encode(password))
                    .role(Role.ADMIN)
                    .nonLocked(true)
                    .build();

            userRepo.save(user);
        }


    }
}
