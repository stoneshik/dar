package com.main.entities.order;

import com.main.entities.file.FileInfoEntity;
import lombok.Data;

import java.util.List;

@Data
public class OrderPrintWithFilesInfoEntity {
    private OrderWithAddress orderInfo;
    private List<FileInfoEntity> filesInfo;
    public OrderPrintWithFilesInfoEntity(OrderWithAddress orderInfo, List<FileInfoEntity> filesInfo) {
        this.orderInfo = orderInfo;
        this.filesInfo = filesInfo;
    }
}
