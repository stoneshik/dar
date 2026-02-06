package com.main.controller.order;

import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.main.ResponseMessageWrapper;
import com.main.dto.OrderPrintDto;
import com.main.dto.OrderScanDto;
import com.main.dto.TaskPrintDto;
import com.main.entities.task.PrintTaskColor;
import com.main.repositories.impls.UserRepositoryImpl;
import com.main.security.AuthorizeHandler;
import com.main.services.order.CreateOrderService;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
public class CreateOrderController {
    private final AuthorizeHandler authorizeHandler;
    private final UserRepositoryImpl userRepository;
    private final CreateOrderService createOrderService;

    @PostMapping(
        path = "/api/v1/orders/print",
        consumes = MediaType.APPLICATION_JSON_VALUE,
        produces = MediaType.APPLICATION_JSON_VALUE
    )
    public ResponseEntity<Object> createOrderPrint(
        HttpServletRequest httpServletRequest,
        @Valid @RequestBody OrderPrintDto orderPrintDto
    ) {
        final String login = authorizeHandler.getLoginBySessionId(httpServletRequest);
        if (login.isEmpty()) {
            return new ResponseEntity<>(
                new ResponseMessageWrapper("Пользователь не авторизован"),
                HttpStatus.BAD_REQUEST
            );
        }
        Long userId = userRepository.getUserIdByLogin(login);
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
        return createOrderService.createOrderPrint(userId, login, orderPrintDto);
    }

    @PostMapping(
        path = "/api/v1/orders/scan",
        consumes = MediaType.APPLICATION_JSON_VALUE,
        produces = MediaType.APPLICATION_JSON_VALUE
    )
    public ResponseEntity<Object> createOrderScan(
        HttpServletRequest httpServletRequest,
        @Valid @RequestBody OrderScanDto orderScanDto
    ) {
        final String login = authorizeHandler.getLoginBySessionId(httpServletRequest);
        if (login.isEmpty()) {
            return new ResponseEntity<>(
                new ResponseMessageWrapper("Пользователь не авторизован"),
                HttpStatus.BAD_REQUEST
            );
        }
        return createOrderService.createOrderScan(login, orderScanDto);
    }

    private boolean checkTypeAndSizeAllFiles(OrderPrintDto orderPrintDto) {
        final String MIME_IMAGE_JPEG = "image/jpeg";
        final String MIME_IMAGE_PNG = "image/png";
        final long maxSize = 10485760; // 10 мб в байтах
        for (TaskPrintDto fileDto : orderPrintDto.getFiles()) {
            final String contentType = fileDto.getType();
            if (contentType == null ||
                (!contentType.equals(MIME_IMAGE_JPEG) && !contentType.equals(MIME_IMAGE_PNG))) {
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
}
