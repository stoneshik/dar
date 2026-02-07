package com.main.services.order;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.main.ResponseMessageWrapper;
import com.main.dto.OrderWithAddressDto;
import com.main.entities.account.BalanceEntity;
import com.main.entities.order.OrderStatus;
import com.main.entities.order.OrderType;
import com.main.entities.order.OrderWithAddress;
import com.main.repositories.impls.AccountRepositoryImpl;
import com.main.repositories.impls.OrderWithAddressRepositoryImpl;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class OrderPayService {
    private final OrderWithAddressRepositoryImpl orderWithAddressService;
    private final AccountRepositoryImpl accountService;

    @Transactional
    public ResponseEntity<Object> payOrders(
        String login,
        List<OrderWithAddressDto> ordersDto
    ) {
        BalanceEntity balanceEntity = accountService.getBalance(login);
        List<OrderWithAddress> orders = new ArrayList<>();
        BigDecimal amountOrders = new BigDecimal("0.0");
        for (OrderWithAddressDto orderDto: ordersDto) {
            if (!Objects.equals(orderDto.getAccountId(), balanceEntity.getAccountId())) {
                // айди счета в пришедшем заказе отличается от айди счета пользователя
                return new ResponseEntity<>(
                    new ResponseMessageWrapper("Передана неверная информация о заказе"),
                    HttpStatus.BAD_REQUEST
                );
            }
            OrderWithAddress order = mapOrderWithAddressDto(orderDto);
            amountOrders = amountOrders.add(order.getOrderAmount());
            orders.add(order);
        }
        final BigDecimal newAccountBalance = balanceEntity.getAccountBalance().subtract(amountOrders);
        if (newAccountBalance.compareTo(new BigDecimal("0.0")) < 0) {
            return new ResponseEntity<>(
                new ResponseMessageWrapper("Недостаточно средств на счете"),
                HttpStatus.BAD_REQUEST
            );
        }
        for (OrderWithAddress order: orders) {
            if (!orderWithAddressService.checkOrderInfo(order)) {
                return new ResponseEntity<>(
                    new ResponseMessageWrapper("Передана неверная информация о заказе"),
                    HttpStatus.BAD_REQUEST
                );
            }
        }
        final boolean isUpdated = accountService.updateBalance(balanceEntity.getAccountId(), newAccountBalance);
        if (!isUpdated) {
            return new ResponseEntity<>(
                new ResponseMessageWrapper("Не получилось обновить баланс счета"),
                HttpStatus.BAD_REQUEST
            );
        }
        for (OrderWithAddress order: orders) {
            if (!orderWithAddressService.changeStatusOrderToPaid(order.getOrderId())) {
                return new ResponseEntity<>(
                    new ResponseMessageWrapper("Не получилось обновить статус заказа"),
                    HttpStatus.BAD_REQUEST
                );
            }
        }
        return new ResponseEntity<>(
            new ResponseMessageWrapper("Оплата прошла успешно"),
            HttpStatus.OK
        );
    }

    private OrderWithAddress mapOrderWithAddressDto(OrderWithAddressDto orderDto) {
        OrderWithAddress order = new OrderWithAddress();
        order.setOrderId(orderDto.getOrderId());
        order.setAccountId(orderDto.getAccountId());
        order.setOrderAddress(orderDto.getOrderAddress());
        order.setOrderAmount(orderDto.getOrderAmount());
        order.setOrderDatetime(orderDto.getOrderDatetime());
        order.setOrderType(OrderType.valueOf(orderDto.getOrderType()));
        order.setOrderStatus(OrderStatus.valueOf(orderDto.getOrderStatus()));
        order.setOrderNum(orderDto.getOrderNum());
        return order;
    }
}
