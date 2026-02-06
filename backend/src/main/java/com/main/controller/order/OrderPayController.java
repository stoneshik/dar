package com.main.controller.order;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.main.ResponseMessageWrapper;
import com.main.dto.OrderWithAddressDto;
import com.main.security.AuthorizeHandler;
import com.main.services.order.OrderPayService;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
public class OrderPayController {
    private final AuthorizeHandler authorizeHandler;
    private final OrderPayService orderPayService;

    @PostMapping(
        path = "/api/v1/orders/pay",
        consumes = MediaType.APPLICATION_JSON_VALUE,
        produces = MediaType.APPLICATION_JSON_VALUE
    )
    public ResponseEntity<Object> payOrders(
        HttpServletRequest httpServletRequest,
        @Valid @RequestBody List<OrderWithAddressDto> ordersDto
    ) {
        if (ordersDto.isEmpty()) {
            return new ResponseEntity<>(
                new ResponseMessageWrapper("Передан пустой список заказов"),
                HttpStatus.BAD_REQUEST
            );
        }
        final String login = authorizeHandler.getLoginBySessionId(httpServletRequest);
        if (login.isEmpty()) {
            return new ResponseEntity<>(
                new ResponseMessageWrapper("Пользователь не авторизован"),
                HttpStatus.BAD_REQUEST
            );
        }
        return orderPayService.payOrders(login, ordersDto);
    }
}
