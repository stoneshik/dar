package com.main.entities.file;

import lombok.Data;

import java.util.Date;

@Data
public class FileInfoEntity {
    private Long fileId;
    private Long userId;
    private String fileName;
    private Date fileLoadDatetime;
    public FileInfoEntity(Long fileId, Long userId, String fileName, Date fileLoadDatetime) {
        this.fileId = fileId;
        this.userId = userId;
        this.fileName = fileName;
        this.fileLoadDatetime = fileLoadDatetime;
    }
}
