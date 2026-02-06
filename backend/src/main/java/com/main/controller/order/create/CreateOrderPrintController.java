package com.main.controller.order.create;

import com.main.ResponseMessageWrapper;
import com.main.dto.OrderPrintDto;
import com.main.dto.TaskPrintDto;
import com.main.entities.account.BalanceEntity;
import com.main.entities.task.PrintTaskColor;
import com.main.security.AuthorizeHandler;
import com.main.services.AccountService;
import com.main.services.OrderService;
import com.main.services.UserService;
import com.main.services.task.TaskPrintService;
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
public class CreateOrderPrintController {
    private final AuthorizeHandler authorizeHandler;
    private final OrderService orderService;
    private final AccountService accountService;
    private final UserService userService;
    private final TaskPrintService taskService;

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
                    new BigDecimal(pagePrice).multiply(new BigDecimal(taskPrintDto.getNumberCopies()))
            );
        }
        return amount;
    }

    private boolean checkTypeAndSizeAllFiles(OrderPrintDto orderPrintDto) {
        final String MIME_IMAGE_JPEG = "image/jpeg";
        final String MIME_IMAGE_PNG = "image/png";
        final long maxSize = 10485760; // 10 мб в байтах
        for (TaskPrintDto fileDto : orderPrintDto.getFiles()) {
            final String contentType = fileDto.getType();
            if (contentType == null || (!contentType.equals(MIME_IMAGE_JPEG) && !contentType.equals(MIME_IMAGE_PNG))) {
                return false;
            }
            if (fileDto.getBlob().length > maxSize) {
                return false;
            }
            final String typePrint = fileDto.getTypePrint();
            if (!typePrint.equals(PrintTaskColor.BLACK_WHITE.getName()) &&
                    !typePrint.equals(PrintTaskColor.COLOR.getName())) {
                return false;
            }
        }
        return true;
    }


    @PostMapping(
            path = "/api/order/create/print_order",
            consumes = MediaType.APPLICATION_JSON_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    public ResponseEntity<Object> createOrderPrint(
            HttpServletRequest httpServletRequest,
            @Valid @RequestBody OrderPrintDto orderPrintDto) {
        final String login = authorizeHandler.getLoginBySessionId(httpServletRequest);
        if (login.isEmpty()) {
            return new ResponseEntity<>(
                    new ResponseMessageWrapper("Пользователь не авторизован"),
                    HttpStatus.BAD_REQUEST
            );
        }
        Long userId = userService.getUserIdByLogin(login);
        if (userId == null) {
            return new ResponseEntity<>(
                    new ResponseMessageWrapper("Пользователь не найден"),
                    HttpStatus.BAD_REQUEST
            );
        }
        if (!checkTypeAndSizeAllFiles(orderPrintDto)) {
            return new ResponseEntity<>(
                    new ResponseMessageWrapper("Передан файл не подходящего типа, либо размера"),
                    HttpStatus.BAD_REQUEST
            );
        }
        final BigDecimal orderAmount = countAmount(orderPrintDto);
        BalanceEntity balanceEntity = accountService.getBalance(login);
        final Long orderId = orderService.createNewPrintOrder(
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
        Long machineId = taskService.findMachineIdForTaskPrint(orderPrintDto);
        if (machineId == null) {
            return new ResponseEntity<>(
                    new ResponseMessageWrapper("Не получилось создать новый заказ"),
                    HttpStatus.BAD_REQUEST
            );
        }
        final boolean isCreatedTask = taskService.createTasksPrint(
                orderId, machineId, orderPrintDto, userId
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
