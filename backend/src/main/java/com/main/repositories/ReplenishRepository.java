package com.main.repositories;

import com.main.entities.replenish.ReplenishEntity;

import java.math.BigDecimal;
import java.util.List;

public interface ReplenishRepository {
    List<ReplenishEntity> getAllReplenishesByAccountId(Long accountId);

    boolean createNewReplenish(Long accountId, BigDecimal replenishAmount);
}
