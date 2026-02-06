package com.main.entities.replenish;

import java.math.BigDecimal;
import java.util.Date;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class ReplenishEntity {
    private Long replenishId;
    private Long accountId;
    private BigDecimal replenishAmount;
    private Date replenishDatetime;
}
