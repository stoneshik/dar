package com.main.controller;

import com.main.ResponseMessageWrapper;
import com.main.security.AuthorizeHandler;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class LogoutController {
    private final AuthorizeHandler authorizeHandler;

    @PostMapping(
            path = "/api/logout",
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    public ResponseEntity<ResponseMessageWrapper> logout(HttpServletRequest httpServletRequest) {
        if (!authorizeHandler.logout(httpServletRequest)) {
            return new ResponseEntity<>(new ResponseMessageWrapper("Информация об входе не найдена"), HttpStatus.NOT_FOUND);
        }
        return new ResponseEntity<>(new ResponseMessageWrapper("Успешный выход"), HttpStatus.OK);
    }
}
