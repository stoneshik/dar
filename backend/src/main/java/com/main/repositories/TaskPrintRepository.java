package com.main.repositories;

import com.main.dto.OrderPrintDto;

public interface TaskPrintRepository {
    Long findMachineIdForTaskPrint(OrderPrintDto orderPrintDto);
    boolean createTasksPrint(Long orderId, Long machineId, OrderPrintDto orderPrintDto, Long userId);
}
