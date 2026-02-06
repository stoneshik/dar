package com.main.entities.task;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class ScanTaskEntity {
    private Long scanTaskId;
    private Long orderId;
    private Long machineId;
    private Long scanTaskNumberPages;
}
