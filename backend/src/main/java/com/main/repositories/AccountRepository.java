package com.main.repositories;

import com.main.entities.account.BalanceEntity;

import java.math.BigDecimal;

public interface AccountRepository {
    Long getAccountId(String login);
    BalanceEntity getBalance(String login);
    boolean updateBalance(Long accountId, BigDecimal newAccountBalance);
}
