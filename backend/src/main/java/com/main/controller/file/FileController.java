package com.main.controller.file;

import com.main.ResponseMessageWrapper;
import com.main.entities.file.FileWithContentEntity;
import com.main.entities.file.FileWithOidEntity;
import com.main.security.AuthorizeHandler;
import com.main.services.FileService;
import com.main.services.UserService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class FileController {
    private final FileService fileService;
    private final UserService userService;
    private final AuthorizeHandler authorizeHandler;

    @GetMapping(
            path = "/api/file/get/{fileId}",
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    private ResponseEntity<Object> getOrderScanById(
            HttpServletRequest httpServletRequest,
            @PathVariable Long fileId) {
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
        final FileWithOidEntity fileEntity = fileService.getFileById(userId, fileId);
        if (fileEntity == null) {
            return new ResponseEntity<>(
                    new ResponseMessageWrapper("Не удалось загрузить файл"),
                    HttpStatus.BAD_REQUEST
            );
        }
        final byte[] fileContent = fileService.loadFileByOid(fileEntity.getFileOid());
        if (fileContent == null) {
            return new ResponseEntity<>(
                    new ResponseMessageWrapper("Не удалось загрузить файл"),
                    HttpStatus.BAD_REQUEST
            );
        }
        final FileWithContentEntity fileWithContentEntity = new FileWithContentEntity(
                fileEntity.getFileId(),
                fileEntity.getUserId(),
                fileEntity.getFileName(),
                fileEntity.getFileLoadDatetime(),
                fileContent
        );
        return new ResponseEntity<>(fileWithContentEntity, HttpStatus.OK);
    }
}
