package com.main.services;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.main.ResponseMessageWrapper;
import com.main.entities.file.FileInfoEntity;
import com.main.entities.file.FileWithContentEntity;
import com.main.entities.file.FileWithOidEntity;
import com.main.repositories.impls.FileRepositoryImpl;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class FileService {
    private final FileRepositoryImpl fileRepository;

    @Transactional
    public ResponseEntity<Object> downloadFileById(Long userId, Long fileId) {
        final FileWithOidEntity fileEntity = fileRepository.getFileById(userId, fileId);
        if (fileEntity == null) {
            return new ResponseEntity<>(
                new ResponseMessageWrapper("Не удалось загрузить файл"),
                HttpStatus.BAD_REQUEST
            );
        }
        final byte[] fileContent = fileRepository.loadFileByOid(fileEntity.getFileOid());
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

    @Transactional
    public ResponseEntity<Object> getOrderScanById(Long userId) {
        List<FileInfoEntity> files = fileRepository.getFilesAttachedScanOrder(userId);
        if (files == null) {
            return new ResponseEntity<>(
                new ResponseMessageWrapper("Не получилось получить информацию о файлах приложенных к заказу"),
                HttpStatus.BAD_REQUEST
            );
        }
        return new ResponseEntity<>(files, HttpStatus.OK);
    }

    @Transactional
    public ResponseEntity<Object> getOrderPrintById(Long userId) {
        List<FileInfoEntity> files = fileRepository.getFilesAttachedPrintOrder(userId);
        if (files == null) {
            return new ResponseEntity<>(
                new ResponseMessageWrapper("Не получилось получить информацию о файлах приложенных к заказу"),
                HttpStatus.BAD_REQUEST
            );
        }
        return new ResponseEntity<>(files, HttpStatus.OK);
    }
}
