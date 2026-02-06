package com.main.entities.file;

import lombok.Data;

import java.util.Date;

@Data
public class FileWithContentEntity {
    private Long fileId;
    private Long userId;
    private String fileName;
    private Date fileLoadDatetime;
    private byte[] fileContent;
    public FileWithContentEntity(
            Long fileId,
            Long userId,
            String fileName,
            Date fileLoadDatetime,
            byte[] fileContent) {
        this.fileId = fileId;
        this.userId = userId;
        this.fileName = fileName;
        this.fileLoadDatetime = fileLoadDatetime;
        this.fileContent = fileContent;
    }
}
