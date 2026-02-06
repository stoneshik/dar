package com.main.services.order.remove;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.main.ResponseMessageWrapper;
import com.main.entities.account.BalanceEntity;
import com.main.entities.order.OrderEntity;
import com.main.entities.order.OrderStatus;
import com.main.repositories.impls.AccountRepositoryImpl;
import com.main.repositories.impls.OrderRepositoryImpl;
import com.main.services.order.remove.strategy.OrderCompletedRemoveStrategy;
import com.main.services.order.remove.strategy.OrderNotPaidRemoveStrategy;
import com.main.services.order.remove.strategy.OrderPaidRemoveStrategy;
import com.main.services.order.remove.strategy.OrderRemoveStrategy;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class OrderRemoveService {
    private final OrderRepositoryImpl orderService;
    private final AccountRepositoryImpl accountService;

    private final OrderPaidRemoveStrategy orderPaidRemoveStrategy;
    private final OrderNotPaidRemoveStrategy orderNotPaidRemoveStrategy;
    private final OrderCompletedRemoveStrategy orderCompletedRemoveStrategy;

    @Transactional
    public ResponseEntity<ResponseMessageWrapper> removeOrder(
        String login,
        Long orderId
    ) {
        BalanceEntity balanceEntity = accountService.getBalance(login);
        OrderEntity orderEntity = orderService.getById(
            balanceEntity.getAccountId(),
            orderId
        );
        if (orderEntity == null) {
            return new ResponseEntity<>(
                new ResponseMessageWrapper("Заказ не найден"),
                HttpStatus.NOT_FOUND
            );
        }
        OrderRemoveStrategy orderRemoveStrategy = switchOrderRemoveStrategy(orderEntity.getOrderStatus());
        if (!orderRemoveStrategy.remove(orderEntity, balanceEntity)) {
            return new ResponseEntity<>(new ResponseMessageWrapper(
                "Не получилось удалить заказ"),
                HttpStatus.BAD_REQUEST
            );
        }
        return new ResponseEntity<>(
            new ResponseMessageWrapper("Успешное удаление"),
            HttpStatus.OK
        );
    }

    private OrderRemoveStrategy switchOrderRemoveStrategy(OrderStatus orderStatus) {
        OrderRemoveStrategy orderRemoveStrategy;
        switch (orderStatus) {
            case PAID -> orderRemoveStrategy = orderPaidRemoveStrategy;
            case NOT_PAID -> orderRemoveStrategy = orderNotPaidRemoveStrategy;
            case COMPLETED -> orderRemoveStrategy = orderCompletedRemoveStrategy;
            default -> orderRemoveStrategy = null;
        }
        return orderRemoveStrategy;
    }
}
