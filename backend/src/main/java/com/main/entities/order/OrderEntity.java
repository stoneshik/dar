package com.main.entities.order;

import lombok.Data;

import java.math.BigDecimal;
import java.util.Date;

@Data
public class OrderEntity {
    private Long orderId;
    private Long accountId;
    private Long vendingPointId;
    private BigDecimal orderAmount;
    private Date orderDatetime;
    private OrderType orderType;
    private OrderStatus orderStatus;
    private Long orderNum;

    public OrderEntity(
            Long orderId,
            Long accountId,
            Long vendingPointId,
            BigDecimal orderAmount,
            Date orderDatetime,
            String orderType,
            String orderStatus,
            Long orderNum) {
        this.orderId = orderId;
        this.accountId = accountId;
        this.vendingPointId = vendingPointId;
        this.orderAmount = orderAmount;
        this.orderDatetime = orderDatetime;
        this.orderType = OrderType.getValueByName(orderType);
        this.orderStatus = OrderStatus.getValueByName(orderStatus);
        this.orderNum = orderNum;
    }
}
