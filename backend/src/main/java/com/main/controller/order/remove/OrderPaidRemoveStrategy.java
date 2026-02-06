package com.main.controller.order.remove;

import java.math.BigDecimal;

import com.main.entities.account.BalanceEntity;
import com.main.entities.order.OrderEntity;
import com.main.entities.order.OrderType;
import com.main.repositories.impls.AccountRepositoryImpl;
import com.main.repositories.impls.FileRepositoryImpl;
import com.main.repositories.impls.OrderRepositoryImpl;

public class OrderPaidRemoveStrategy implements OrderRemoveStrategy {
    private final OrderRepositoryImpl orderService;
    private final FileRepositoryImpl fileService;
    private final AccountRepositoryImpl accountService;

    public OrderPaidRemoveStrategy(OrderRepositoryImpl orderService, AccountRepositoryImpl accountService, FileRepositoryImpl fileService) {
        this.orderService = orderService;
        this.accountService = accountService;
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
        final BigDecimal newAccountBalance = balanceEntity.getAccountBalance().add(
                orderEntity.getOrderAmount()
        );
        final boolean isAccountBalanceUpdate = accountService.updateBalance(
                balanceEntity.getAccountId(), newAccountBalance
        );
        if (!isAccountBalanceUpdate) {
            return false;
        }
        return orderService.removeById(orderEntity.getOrderId());
    }
}
