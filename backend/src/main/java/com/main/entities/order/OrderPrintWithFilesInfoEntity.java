package com.main.entities.order;

import java.util.List;

import com.main.entities.file.FileInfoEntity;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class OrderPrintWithFilesInfoEntity {
    private OrderWithAddress orderInfo;
    private List<FileInfoEntity> filesInfo;
}
