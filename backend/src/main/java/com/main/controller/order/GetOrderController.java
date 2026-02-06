package com.main.controller.order;

import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import com.main.ResponseMessageWrapper;
import com.main.entities.order.OrderStatus;
import com.main.security.AuthorizeHandler;
import com.main.services.order.GetOrderService;

import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
public class GetOrderController {
    private final AuthorizeHandler authorizeHandler;
    private final GetOrderService getOrderService;

    @GetMapping(
        path = "/api/v1/orders/print/{orderId}",
        produces = MediaType.APPLICATION_JSON_VALUE
    )
    private ResponseEntity<Object> getOrderPrintById(
        HttpServletRequest httpServletRequest,
        @PathVariable Long orderId
    ) {
        final String login = authorizeHandler.getLoginBySessionId(httpServletRequest);
        if (login.isEmpty()) {
            return new ResponseEntity<>(
                new ResponseMessageWrapper("Пользователь не авторизован"),
                HttpStatus.BAD_REQUEST
            );
        }
        return getOrderService.getOrderPrintById(login, orderId);
    }

    @GetMapping(
        path = "/api/v1/orders/scan/{orderId}",
        produces = MediaType.APPLICATION_JSON_VALUE
    )
    private ResponseEntity<Object> getOrderScanById(
        HttpServletRequest httpServletRequest,
        @PathVariable Long orderId
    ) {
        final String login = authorizeHandler.getLoginBySessionId(httpServletRequest);
        if (login.isEmpty()) {
            return new ResponseEntity<>(
                new ResponseMessageWrapper("Пользователь не авторизован"),
                HttpStatus.BAD_REQUEST
            );
        }
        return getOrderService.getOrderScanById(login, orderId);
    }

    @GetMapping(
        path = "/api/v1/orders/status/paid",
        produces = MediaType.APPLICATION_JSON_VALUE
    )
    public ResponseEntity<Object> getPaidOrders(HttpServletRequest httpServletRequest) {
        final String login = authorizeHandler.getLoginBySessionId(httpServletRequest);
        if (login.isEmpty()) {
            return new ResponseEntity<>(
                new ResponseMessageWrapper("Пользователь не авторизован"),
                HttpStatus.BAD_REQUEST
            );
        }
        return getOrderService.getOrders(login, OrderStatus.PAID);
    }

    @GetMapping(
        path = "/api/v1/orders/status/not-paid",
        produces = MediaType.APPLICATION_JSON_VALUE
    )
    public ResponseEntity<Object> getNotPaidOrders(HttpServletRequest httpServletRequest) {
        final String login = authorizeHandler.getLoginBySessionId(httpServletRequest);
        if (login.isEmpty()) {
            return new ResponseEntity<>(
                new ResponseMessageWrapper("Пользователь не авторизован"),
                HttpStatus.BAD_REQUEST
            );
        }
        return getOrderService.getOrders(login, OrderStatus.NOT_PAID);
    }
}
