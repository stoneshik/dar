package com.main.entities.replenish;

import lombok.Data;

import java.math.BigDecimal;
import java.util.Date;

@Data
public class ReplenishEntity {
    private Long replenishId;
    private Long accountId;
    private BigDecimal replenishAmount;
    private Date replenishDatetime;
    public ReplenishEntity(Long replenishId, Long accountId, BigDecimal replenishAmount, Date replenishDatetime) {
        this.replenishId = replenishId;
        this.accountId = accountId;
        this.replenishAmount = replenishAmount;
        this.replenishDatetime = replenishDatetime;
    }
}
