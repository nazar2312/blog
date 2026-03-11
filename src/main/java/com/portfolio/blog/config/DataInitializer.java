package com.portfolio.blog.config;

import com.portfolio.blog.domain.entities.Role;
import com.portfolio.blog.domain.entities.UserEntity;
import com.portfolio.blog.repositories.UserRepository;
import lombok.RequiredArgsConstructor;
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

    @Override
    public void run(ApplicationArguments args) throws Exception {

        if (userRepo.findByEmail("admin@gmail.com").isEmpty()) {

            UserEntity user = UserEntity.builder()

                    .id(null)
                    .email("admin@gmail.com")
                    .username("admin")
                    .password(passwordEncoder.encode("password"))
                    .role(Role.ADMIN)
                    .nonLocked(true)
                    .build();

            userRepo.save(user);
        }


    }
}
