package com.portfolio.blog.security;

import com.portfolio.blog.domain.entities.UserEntity;
import com.portfolio.blog.repositories.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

@RequiredArgsConstructor
public class UserDetailService implements UserDetailsService {

    private final UserRepository repository;

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {

        UserEntity user = repository.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException("User not found by email: " + email));
        return new BlogUserDetails(user);
    }
}
