package com.main.entities.account;

import lombok.Data;

import java.math.BigDecimal;


@Data
public class BalanceEntity {
    private Long accountId;
    private String userLogin;
    private BigDecimal accountBalance;

    public BalanceEntity(Long accountId, String userLogin, BigDecimal accountBalance) {
        this.accountId = accountId;
        this.userLogin = userLogin;
        this.accountBalance = accountBalance;
    }
}
