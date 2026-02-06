package com.main.controller.order.remove;

import com.main.entities.account.BalanceEntity;
import com.main.entities.order.OrderEntity;

public class OrderCompletedRemoveStrategy implements OrderRemoveStrategy {
    public  OrderCompletedRemoveStrategy() {
    }

    @Override
    public boolean remove(OrderEntity orderEntity, BalanceEntity balanceEntity) {
        return false;
    }
}
