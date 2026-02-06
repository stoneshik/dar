package com.main.services.order.remove.strategy;

import org.springframework.stereotype.Component;

import com.main.entities.account.BalanceEntity;
import com.main.entities.order.OrderEntity;

@Component
public class OrderCompletedRemoveStrategy implements OrderRemoveStrategy {
    @Override
    public boolean remove(OrderEntity orderEntity, BalanceEntity balanceEntity) {
        return false;
    }
}
