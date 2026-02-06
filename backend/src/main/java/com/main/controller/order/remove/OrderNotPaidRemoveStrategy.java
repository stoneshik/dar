package com.main.controller.order.remove;

import com.main.entities.account.BalanceEntity;
import com.main.entities.order.OrderEntity;
import com.main.entities.order.OrderType;
import com.main.repositories.impls.FileRepositoryImpl;
import com.main.repositories.impls.OrderRepositoryImpl;

public class OrderNotPaidRemoveStrategy implements OrderRemoveStrategy {
    private final OrderRepositoryImpl orderService;
    private final FileRepositoryImpl fileService;

    public OrderNotPaidRemoveStrategy(OrderRepositoryImpl orderService, FileRepositoryImpl fileService) {
        this.orderService = orderService;
        this.fileService = fileService;
    }

    @Override
    public boolean remove(OrderEntity orderEntity, BalanceEntity balanceEntity) {
        if (orderEntity.getOrderType() == OrderType.PRINT) {
            final boolean isAttachedFilesRemoved = fileService.removeFilesByOrderId(orderEntity.getOrderId());
            if (!isAttachedFilesRemoved) {
                return false;
            }
        }
        return orderService.removeById(orderEntity.getOrderId());
    }
}
