package com.main.entities.task;

import lombok.Data;

@Data
public class PrintTaskEntity {
    private Long printTaskId;
    private Long orderId;
    private Long machineId;
    private PrintTaskColor printTaskColor;
    private Long printTaskNumberCopies;
    public PrintTaskEntity(
            Long printTaskId,
            Long orderId,
            Long machineId,
            String printTaskColor,
            Long printTaskNumberCopies) {
        this.printTaskId = printTaskId;
        this.orderId = orderId;
        this.machineId = machineId;
        this.printTaskColor = PrintTaskColor.getValueByName(printTaskColor);
        this.printTaskNumberCopies = printTaskNumberCopies;
    }
}
