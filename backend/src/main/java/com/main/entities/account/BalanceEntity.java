package com.main.entities.account;

import java.math.BigDecimal;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class BalanceEntity {
    private Long accountId;
    private String userLogin;
    private BigDecimal accountBalance;
}
