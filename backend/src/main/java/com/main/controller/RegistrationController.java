package com.main.controller;

import com.main.ResponseMessageWrapper;
import com.main.dto.RegisterDto;
import com.main.entities.user.UserEntity;
import com.main.services.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class RegistrationController {
    private final UserService userService;
    private final BCryptPasswordEncoder passwordEncoder;

    @PostMapping(
            path = "/api/open/register",
            consumes = MediaType.APPLICATION_JSON_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    public ResponseEntity<ResponseMessageWrapper> register(
            @Valid @RequestBody RegisterDto registerDto) {
        final String email = registerDto.getEmail();
        final String login = registerDto.getLogin();
        final String password = passwordEncoder.encode(registerDto.getPassword());
        UserEntity userEntity = userService.findByLogin(login);
        if (userEntity != null) {
            return new ResponseEntity<>(
                    new ResponseMessageWrapper("Пользователь с таким логином уже существует"),
                    HttpStatus.BAD_REQUEST
            );
        }
        UserEntity userEntityByEmail = userService.findByEmail(email);
        if (userEntityByEmail != null) {
            return new ResponseEntity<>(
                    new ResponseMessageWrapper("Пользователь с такой почтой уже существует"),
                    HttpStatus.BAD_REQUEST
            );
        }
        if (userService.create(email, login, password) == 0) {
            return new ResponseEntity<>(new ResponseMessageWrapper("Не получилось создать пользователя"), HttpStatus.BAD_REQUEST);
        }
        return new ResponseEntity<>(new ResponseMessageWrapper("Пользователь успешно создан"), HttpStatus.CREATED);
    }
}
