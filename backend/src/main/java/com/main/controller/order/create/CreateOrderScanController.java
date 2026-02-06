package com.main.controller.order.create;

import com.main.ResponseMessageWrapper;
import com.main.dto.OrderScanDto;
import com.main.entities.account.BalanceEntity;
import com.main.security.AuthorizeHandler;
import com.main.services.AccountService;
import com.main.services.OrderService;
import com.main.services.task.TaskScanService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.math.BigDecimal;

@RestController
@RequiredArgsConstructor
public class CreateOrderScanController {
    private final AuthorizeHandler authorizeHandler;
    private final OrderService orderService;
    private final AccountService accountService;
    private final TaskScanService taskService;

    private BigDecimal countAmount(OrderScanDto orderScanDto) {
        final double pagePrice = 0.5;
        return new BigDecimal(orderScanDto.getScanTaskNumberPages() * pagePrice);
    }

    @PostMapping(
            path = "/api/order/create/scan_order",
            consumes = MediaType.APPLICATION_JSON_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    public ResponseEntity<Object> createOrderScan(
            HttpServletRequest httpServletRequest,
            @Valid @RequestBody OrderScanDto orderScanDto) {
        final String login = authorizeHandler.getLoginBySessionId(httpServletRequest);
        if (login.isEmpty()) {
            return new ResponseEntity<>(
                    new ResponseMessageWrapper("Пользователь не авторизован"),
                    HttpStatus.BAD_REQUEST
            );
        }
        final BigDecimal orderAmount = countAmount(orderScanDto);
        BalanceEntity balanceEntity = accountService.getBalance(login);
        final Long orderId = orderService.createNewScanOrder(
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
        Long machineId = taskService.findMachineIdForTaskScan(orderScanDto.getVendingPointId());
        if (machineId == null) {
            return new ResponseEntity<>(
                    new ResponseMessageWrapper("Не получилось создать новый заказ"),
                    HttpStatus.BAD_REQUEST
            );
        }
        final boolean isCreatedTask = taskService.createTaskScan(
                orderId, machineId, orderScanDto.getScanTaskNumberPages()
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
}
