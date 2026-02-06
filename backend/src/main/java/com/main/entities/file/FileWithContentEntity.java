package com.main.entities.file;

import java.util.Date;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class FileWithContentEntity {
    private Long fileId;
    private Long userId;
    private String fileName;
    private Date fileLoadDatetime;
    private byte[] fileContent;
}
