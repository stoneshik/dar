package com.main.controller;

import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.main.ResponseMessageWrapper;
import com.main.dto.AuthDto;
import com.main.dto.RegisterDto;
import com.main.services.UserService;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
public class AuthorizationController {
    private final UserService userService;

    @PostMapping(
        path = "/api/v1/open/register",
        consumes = MediaType.APPLICATION_JSON_VALUE,
        produces = MediaType.APPLICATION_JSON_VALUE
    )
    public ResponseEntity<ResponseMessageWrapper> register(
        @Valid @RequestBody RegisterDto registerDto
    ) {
        return userService.register(registerDto);
    }

    @PostMapping(
        path = "/api/v1/open/auth",
        consumes = MediaType.APPLICATION_JSON_VALUE,
        produces = MediaType.APPLICATION_JSON_VALUE
    )
    public ResponseEntity<ResponseMessageWrapper> auth(
        @Valid @RequestBody AuthDto authDto,
        HttpServletRequest httpServletRequest
    ) {
        return userService.auth(authDto, httpServletRequest);
    }

    @PostMapping(
        path = "/api/v1/logout",
        produces = MediaType.APPLICATION_JSON_VALUE
    )
    public ResponseEntity<ResponseMessageWrapper> logout(HttpServletRequest httpServletRequest) {
        return userService.logout(httpServletRequest);
    }
}
