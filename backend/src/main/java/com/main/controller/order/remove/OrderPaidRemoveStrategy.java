package com.main.controller.order.remove;

import com.main.entities.account.BalanceEntity;
import com.main.entities.order.OrderEntity;
import com.main.entities.order.OrderType;
import com.main.services.AccountService;
import com.main.services.FileService;
import com.main.services.OrderService;

import java.math.BigDecimal;

public class OrderPaidRemoveStrategy implements OrderRemoveStrategy {
    private final OrderService orderService;
    private final FileService fileService;
    private final AccountService accountService;

    public OrderPaidRemoveStrategy(OrderService orderService, AccountService accountService, FileService fileService) {
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
