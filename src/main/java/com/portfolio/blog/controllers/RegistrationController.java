package com.portfolio.blog.controllers;

import com.portfolio.blog.domain.dto.authentication.RegistrationRequest;
import com.portfolio.blog.domain.dto.authentication.RegistrationResponse;
import com.portfolio.blog.services.RegistrationServiceInterface;
import jakarta.validation.Valid;
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
            @Valid @RequestBody RegistrationRequest request
            ) {

        registrationService.createUser(request);
        return ResponseEntity.ok().body(new RegistrationResponse("User successfully registered"));
    }
}
