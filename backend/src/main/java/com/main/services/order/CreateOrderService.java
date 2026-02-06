package com.main.services.order;

import java.math.BigDecimal;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.main.ResponseMessageWrapper;
import com.main.dto.OrderPrintDto;
import com.main.dto.OrderScanDto;
import com.main.dto.TaskPrintDto;
import com.main.entities.account.BalanceEntity;
import com.main.entities.task.PrintTaskColor;
import com.main.repositories.impls.AccountRepositoryImpl;
import com.main.repositories.impls.OrderRepositoryImpl;
import com.main.repositories.impls.task.TaskPrintRepositoryImpl;
import com.main.repositories.impls.task.TaskScanRepositoryImpl;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class CreateOrderService {
    private final OrderRepositoryImpl orderRepository;
    private final AccountRepositoryImpl accountRepository;
    private final TaskPrintRepositoryImpl taskPrintRepository;
    private final TaskScanRepositoryImpl taskScanRepository;

    @Transactional
    public ResponseEntity<Object> createOrderPrint(
        Long userId,
        String login,
        OrderPrintDto orderPrintDto
    ) {
        final BigDecimal orderAmount = countAmount(orderPrintDto);
        BalanceEntity balanceEntity = accountRepository.getBalance(login);
        final Long orderId = orderRepository.createNewPrintOrder(
            balanceEntity.getAccountId(),
            orderPrintDto.getVendingPointId(),
            orderAmount
        );
        if (orderId < 0L) {
            return new ResponseEntity<>(
                new ResponseMessageWrapper("Не получилось создать новый заказ"),
                HttpStatus.BAD_REQUEST
            );
        }
        Long machineId = taskPrintRepository.findMachineIdForTaskPrint(orderPrintDto);
        if (machineId == null) {
            return new ResponseEntity<>(
                new ResponseMessageWrapper("Не получилось создать новый заказ"),
                HttpStatus.BAD_REQUEST
            );
        }
        final boolean isCreatedTask = taskPrintRepository.createTasksPrint(
            orderId,
            machineId,
            orderPrintDto,
            userId
        );
        if (!isCreatedTask) {
            return new ResponseEntity<>(
                new ResponseMessageWrapper("Не получилось создать новый заказ"),
                HttpStatus.BAD_REQUEST
            );
        }
        return new ResponseEntity<>(
            new ResponseMessageWrapper("Новый заказ создан"),
            HttpStatus.OK
        );
    }

    @Transactional
    public ResponseEntity<Object> createOrderScan(
        String login,
        OrderScanDto orderScanDto
    ) {
        BalanceEntity balanceEntity = accountRepository.getBalance(login);
        final BigDecimal orderAmount = countAmount(orderScanDto);
        final Long orderId = orderRepository.createNewScanOrder(
            balanceEntity.getAccountId(),
            orderScanDto.getVendingPointId(),
            orderAmount
        );
        if (orderId < 0L) {
            return new ResponseEntity<>(
                new ResponseMessageWrapper("Не получилось создать новый заказ"),
                HttpStatus.BAD_REQUEST
            );
        }
        Long machineId = taskScanRepository.findMachineIdForTaskScan(
            orderScanDto.getVendingPointId()
        );
        if (machineId == null) {
            return new ResponseEntity<>(
                new ResponseMessageWrapper("Не получилось создать новый заказ"),
                HttpStatus.BAD_REQUEST
            );
        }
        final boolean isCreatedTask = taskScanRepository.createTaskScan(
            orderId,
            machineId,
            orderScanDto.getScanTaskNumberPages()
        );
        if (!isCreatedTask) {
            return new ResponseEntity<>(
                new ResponseMessageWrapper("Не получилось создать новый заказ"),
                HttpStatus.BAD_REQUEST
            );
        }
        return new ResponseEntity<>(
            new ResponseMessageWrapper("Новый заказ создан"),
            HttpStatus.OK
        );
    }

    private BigDecimal countAmount(OrderPrintDto orderPrintDto) {
        final double pagePriceForBlackWhite = 7.0;
        final double pagePriceForColor = 15.0;
        BigDecimal amount = new BigDecimal(0);
        for (TaskPrintDto taskPrintDto : orderPrintDto.getFiles()) {
            double pagePrice;
            if (taskPrintDto.getTypePrint().equals(PrintTaskColor.BLACK_WHITE.getName())) {
                pagePrice = pagePriceForBlackWhite;
            } else {
                pagePrice = pagePriceForColor;
            }
            amount = amount.add(
                new BigDecimal(pagePrice).multiply(
                    new BigDecimal(taskPrintDto.getNumberCopies())
                )
            );
        }
        return amount;
    }

    private BigDecimal countAmount(OrderScanDto orderScanDto) {
        final double pagePrice = 0.5;
        return new BigDecimal(orderScanDto.getScanTaskNumberPages() * pagePrice);
    }
}
