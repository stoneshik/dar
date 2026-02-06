package com.main.services;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.main.ResponseMessageWrapper;
import com.main.dto.AuthDto;
import com.main.dto.RegisterDto;
import com.main.entities.user.UserEntity;
import com.main.repositories.impls.UserRepositoryImpl;
import com.main.security.AuthorizeHandler;

import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class UserService {
    private final UserRepositoryImpl userRepository;
    private final BCryptPasswordEncoder passwordEncoder;
    private final AuthorizeHandler authorizeHandler;

    @Transactional
    public ResponseEntity<ResponseMessageWrapper> register(RegisterDto registerDto) {
        final String email = registerDto.getEmail();
        final String login = registerDto.getLogin();
        final String password = passwordEncoder.encode(registerDto.getPassword());
        UserEntity userEntity = userRepository.findByLogin(login);
        if (userEntity != null) {
            return new ResponseEntity<>(
                new ResponseMessageWrapper("Пользователь с таким логином уже существует"),
                HttpStatus.BAD_REQUEST
            );
        }
        UserEntity userEntityByEmail = userRepository.findByEmail(email);
        if (userEntityByEmail != null) {
            return new ResponseEntity<>(
                new ResponseMessageWrapper("Пользователь с такой почтой уже существует"),
                HttpStatus.BAD_REQUEST
            );
        }
        if (userRepository.create(email, login, password) == 0) {
            return new ResponseEntity<>(
                new ResponseMessageWrapper("Не получилось создать пользователя"),
                HttpStatus.BAD_REQUEST
            );
        }
        return new ResponseEntity<>(
            new ResponseMessageWrapper("Пользователь успешно создан"),
            HttpStatus.CREATED
        );
    }

    @Transactional
    public ResponseEntity<ResponseMessageWrapper> auth(
        AuthDto authDto,
        HttpServletRequest httpServletRequest
    ) {
        final String login = authDto.getLogin();
        final String password = authDto.getPassword();
        UserEntity userEntity = userRepository.findByLogin(login);
        if (userEntity == null) {
            return new ResponseEntity<>(
                new ResponseMessageWrapper("Пользователь не найден"),
                HttpStatus.NOT_FOUND
            );
        }
        if (!passwordEncoder.matches(password, userEntity.getUserPasswordHash())) {
            return new ResponseEntity<>(
                new ResponseMessageWrapper("Неправильный пароль"),
                HttpStatus.BAD_REQUEST
            );
        }
        if (!authorizeHandler.newAuth(httpServletRequest, login)) {
            return new ResponseEntity<>(
                new ResponseMessageWrapper("Уже был произведен вход"),
                HttpStatus.BAD_REQUEST
            );
        }
        return new ResponseEntity<>(
            new ResponseMessageWrapper("Успешный вход"),
            HttpStatus.OK
        );
    }

    @Transactional
    public ResponseEntity<ResponseMessageWrapper> logout(HttpServletRequest httpServletRequest) {
        if (!authorizeHandler.logout(httpServletRequest)) {
            return new ResponseEntity<>(
                new ResponseMessageWrapper("Информация об входе не найдена"),
                HttpStatus.NOT_FOUND
            );
        }
        return new ResponseEntity<>(
            new ResponseMessageWrapper("Успешный выход"),
            HttpStatus.OK
        );
    }
}
