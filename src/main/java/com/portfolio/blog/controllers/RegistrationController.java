package com.portfolio.blog.controllers;

import com.portfolio.blog.domain.dto.registration.RegistrationRequest;
import com.portfolio.blog.domain.dto.registration.RegistrationResponse;
import com.portfolio.blog.services.RegistrationServiceInterface;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping(path = "/api/registration")
public class RegistrationController {

    private final RegistrationServiceInterface registrationService;

    @PostMapping
    public ResponseEntity<RegistrationResponse> requestToRegister(
            @RequestBody RegistrationRequest request
            ) {

        registrationService.createUser(request);
        return ResponseEntity.ok().body(new RegistrationResponse("User successfully registered"));
    }
}
