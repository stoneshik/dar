package com.main.controller.file;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import com.main.ResponseMessageWrapper;
import com.main.entities.file.FileInfoEntity;
import com.main.repositories.impls.FileRepositoryImpl;
import com.main.repositories.impls.UserRepositoryImpl;
import com.main.security.AuthorizeHandler;

import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
public class FileInfoController {
    private final FileRepositoryImpl fileService;
    private final UserRepositoryImpl userService;
    private final AuthorizeHandler authorizeHandler;

    @GetMapping(
        path = "/api/v1/files/scan",
        produces = MediaType.APPLICATION_JSON_VALUE
    )
    private ResponseEntity<Object> getOrderScanById(HttpServletRequest httpServletRequest) {
        final String login = authorizeHandler.getLoginBySessionId(httpServletRequest);
        if (login.isEmpty()) {
            return new ResponseEntity<>(
                new ResponseMessageWrapper("Пользователь не авторизован"),
                HttpStatus.BAD_REQUEST
            );
        }
        final Long userId = userService.getUserIdByLogin(login);
        if (userId == null) {
            return new ResponseEntity<>(
                new ResponseMessageWrapper("Пользователь не найден"),
                HttpStatus.BAD_REQUEST
            );
        }
        List<FileInfoEntity> files = fileService.getFilesAttachedScanOrder(userId);
        if (files == null) {
            return new ResponseEntity<>(
                new ResponseMessageWrapper("Не получилось получить информацию о файлах приложенных к заказу"),
                HttpStatus.BAD_REQUEST
            );
        }
        return new ResponseEntity<>(files, HttpStatus.OK);
    }

    @GetMapping(
        path = "/api/v1/files/print",
        produces = MediaType.APPLICATION_JSON_VALUE
    )
    private ResponseEntity<Object> getOrderPrintById(HttpServletRequest httpServletRequest) {
        final String login = authorizeHandler.getLoginBySessionId(httpServletRequest);
        if (login.isEmpty()) {
            return new ResponseEntity<>(
                new ResponseMessageWrapper("Пользователь не авторизован"),
                HttpStatus.BAD_REQUEST
            );
        }
        final Long userId = userService.getUserIdByLogin(login);
        if (userId == null) {
            return new ResponseEntity<>(
                new ResponseMessageWrapper("Пользователь не найден"),
                HttpStatus.BAD_REQUEST
            );
        }
        List<FileInfoEntity> files = fileService.getFilesAttachedPrintOrder(userId);
        if (files == null) {
            return new ResponseEntity<>(
                new ResponseMessageWrapper("Не получилось получить информацию о файлах приложенных к заказу"),
                HttpStatus.BAD_REQUEST
            );
        }
        return new ResponseEntity<>(files, HttpStatus.OK);
    }
}
