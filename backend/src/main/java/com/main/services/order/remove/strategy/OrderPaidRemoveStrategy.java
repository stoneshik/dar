package com.main.services.order.remove.strategy;

import java.math.BigDecimal;

import org.springframework.stereotype.Component;

import com.main.entities.account.BalanceEntity;
import com.main.entities.order.OrderEntity;
import com.main.entities.order.OrderType;
import com.main.repositories.impls.AccountRepositoryImpl;
import com.main.repositories.impls.FileRepositoryImpl;
import com.main.repositories.impls.OrderRepositoryImpl;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class OrderPaidRemoveStrategy implements OrderRemoveStrategy {
    private final OrderRepositoryImpl orderRepository;
    private final FileRepositoryImpl fileRepository;
    private final AccountRepositoryImpl accountRepository;

    @Override
    public boolean remove(OrderEntity orderEntity, BalanceEntity balanceEntity) {
        if (orderEntity.getOrderType() == OrderType.PRINT) {
            final boolean isAttachedFilesRemoved = fileRepository.removeFilesByOrderId(
                orderEntity.getOrderId()
            );
            if (!isAttachedFilesRemoved) {
                return false;
            }
        }
        final BigDecimal newAccountBalance = balanceEntity.getAccountBalance().add(
            orderEntity.getOrderAmount()
        );
        final boolean isAccountBalanceUpdate = accountRepository.updateBalance(
            balanceEntity.getAccountId(),
            newAccountBalance
        );
        if (!isAccountBalanceUpdate) {
            return false;
        }
        return orderRepository.removeById(orderEntity.getOrderId());
    }
}
