package com.main.entities.file;

import lombok.Data;

import java.util.Date;

@Data
public class FileWithOidEntity {
    private Long fileId;
    private Long userId;
    private String fileName;
    private Date fileLoadDatetime;
    private Long fileOid;
    public FileWithOidEntity(
            Long fileId,
            Long userId,
            String fileName,
            Date fileLoadDatetime,
            Long fileOid) {
        this.fileId = fileId;
        this.userId = userId;
        this.fileName = fileName;
        this.fileLoadDatetime = fileLoadDatetime;
        this.fileOid = fileOid;
    }
}