package com.main.controller.order;


import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import com.main.ResponseMessageWrapper;
import com.main.security.AuthorizeHandler;
import com.main.services.order.remove.OrderRemoveService;

import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
public class OrderRemoveController {
    private final AuthorizeHandler authorizeHandler;
    private final OrderRemoveService orderRemoveService;

    @DeleteMapping(path = "/api/v1/orders/{id}")
    public ResponseEntity<ResponseMessageWrapper> removeOrder(
        @PathVariable("id") Long orderId,
        HttpServletRequest httpServletRequest
    ) {
        final String login = authorizeHandler.getLoginBySessionId(httpServletRequest);
        if (login.isEmpty()) {
            return new ResponseEntity<>(
                new ResponseMessageWrapper("Пользователь не авторизован"),
                HttpStatus.BAD_REQUEST
            );
        }
        return orderRemoveService.removeOrder(login, orderId);
    }
}
