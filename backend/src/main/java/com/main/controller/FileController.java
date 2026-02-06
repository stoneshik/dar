package com.main.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import com.main.ResponseMessageWrapper;
import com.main.repositories.impls.UserRepositoryImpl;
import com.main.security.AuthorizeHandler;
import com.main.services.FileService;

import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
public class FileController {
    private final AuthorizeHandler authorizeHandler;
    private final UserRepositoryImpl userRepository;
    private final FileService fileService;

    @GetMapping(
        path = "/api/v1/files/{fileId}",
        produces = MediaType.APPLICATION_JSON_VALUE
    )
    public ResponseEntity<Object> getOrderScanById(
        HttpServletRequest httpServletRequest,
        @PathVariable Long fileId
    ) {
        final String login = authorizeHandler.getLoginBySessionId(httpServletRequest);
        if (login.isEmpty()) {
            return new ResponseEntity<>(
                new ResponseMessageWrapper("Пользователь не авторизован"),
                HttpStatus.BAD_REQUEST
            );
        }
        final Long userId = userRepository.getUserIdByLogin(login);
        if (userId == null) {
            return new ResponseEntity<>(
                new ResponseMessageWrapper("Пользователь не найден"),
                HttpStatus.BAD_REQUEST
            );
        }
        return fileService.downloadFileById(userId, fileId);
    }

    @GetMapping(
        path = "/api/v1/files/scan",
        produces = MediaType.APPLICATION_JSON_VALUE
    )
    public ResponseEntity<Object> getOrderScanById(HttpServletRequest httpServletRequest) {
        final String login = authorizeHandler.getLoginBySessionId(httpServletRequest);
        if (login.isEmpty()) {
            return new ResponseEntity<>(
                new ResponseMessageWrapper("Пользователь не авторизован"),
                HttpStatus.BAD_REQUEST
            );
        }
        final Long userId = userRepository.getUserIdByLogin(login);
        if (userId == null) {
            return new ResponseEntity<>(
                new ResponseMessageWrapper("Пользователь не найден"),
                HttpStatus.BAD_REQUEST
            );
        }
        return fileService.getOrderScanById(userId);
    }

    @GetMapping(
        path = "/api/v1/files/print",
        produces = MediaType.APPLICATION_JSON_VALUE
    )
    public ResponseEntity<Object> getOrderPrintById(HttpServletRequest httpServletRequest) {
        final String login = authorizeHandler.getLoginBySessionId(httpServletRequest);
        if (login.isEmpty()) {
            return new ResponseEntity<>(
                new ResponseMessageWrapper("Пользователь не авторизован"),
                HttpStatus.BAD_REQUEST
            );
        }
        final Long userId = userRepository.getUserIdByLogin(login);
        if (userId == null) {
            return new ResponseEntity<>(
                new ResponseMessageWrapper("Пользователь не найден"),
                HttpStatus.BAD_REQUEST
            );
        }
        return fileService.getOrderPrintById(userId);
    }
}
