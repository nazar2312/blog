package com.portfolio.blog.services.impl;

import com.portfolio.blog.domain.entities.UserEntity;
import com.portfolio.blog.security.BlogUserDetails;
import com.portfolio.blog.services.UserServiceInterface;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserService implements UserServiceInterface {

    @Override
    public UserEntity getUserFromSecurityContextHolder() {

        Authentication auth = SecurityContextHolder.getContext().getAuthentication();

        return auth instanceof AnonymousAuthenticationToken ? null :
                ((BlogUserDetails) auth.getPrincipal())
                        .getUser(); //Returns userEntity

    }
}
