package com.main.controller.order.get;

import com.main.ResponseMessageWrapper;
import com.main.entities.order.OrderStatus;
import com.main.entities.order.OrderWithAddress;
import com.main.security.AuthorizeHandler;
import com.main.services.OrderWithAddressService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequiredArgsConstructor
public class GetOrdersController {
    private final OrderWithAddressService orderWithAddressService;
    private final AuthorizeHandler authorizeHandler;

    private List<OrderWithAddress> getOrdersFromBd(OrderStatus orderStatus, String login) {
        List<OrderWithAddress> orders;
        switch (orderStatus) {
            case PAID -> orders = orderWithAddressService.getPaidOrders(login);
            case NOT_PAID -> orders = orderWithAddressService.getNotPaidOrders(login);
            default -> orders = null;
        }
        return orders;
    }

    private ResponseEntity<Object> getOrders(HttpServletRequest httpServletRequest, OrderStatus orderStatus) {
        final String login = authorizeHandler.getLoginBySessionId(httpServletRequest);
        if (login.isEmpty()) {
            return new ResponseEntity<>(
                    new ResponseMessageWrapper("Пользователь не авторизован"),
                    HttpStatus.BAD_REQUEST
            );
        }
        final List<OrderWithAddress> orders = getOrdersFromBd(orderStatus, login);
        if (orders == null) {
            return new ResponseEntity<>(
                    new ResponseMessageWrapper("Не получилось получить информацию о заказах"),
                    HttpStatus.BAD_REQUEST
            );
        }
        return new ResponseEntity<>(orders, HttpStatus.OK);
    }

    @GetMapping(
            path = "/api/order/get_paid",
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    public ResponseEntity<Object> getPaidOrders(HttpServletRequest httpServletRequest) {
        return getOrders(httpServletRequest, OrderStatus.PAID);
    }

    @GetMapping(
            path = "/api/order/get_not_paid",
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    public ResponseEntity<Object> getNotPaidOrders(HttpServletRequest httpServletRequest) {
        return getOrders(httpServletRequest, OrderStatus.NOT_PAID);
    }
}
