package com.main.repositories;

public interface TaskScanRepository {
    Long findMachineIdForTaskScan(Long vendingPointId);
    boolean createTaskScan(Long orderId, Long machineId, Long scanTaskNumberPages);

    Long getScanTaskNumberPagesByOrderId(Long orderId);
}
