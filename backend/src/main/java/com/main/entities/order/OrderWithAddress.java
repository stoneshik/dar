package com.main.entities.order;

import lombok.Data;

import java.math.BigDecimal;
import java.util.Date;
import java.util.Objects;

@Data
public class OrderWithAddress {
    private Long orderId;
    private Long accountId;
    private String orderAddress;
    private BigDecimal orderAmount;
    private Date orderDatetime;
    private OrderType orderType;
    private OrderStatus orderStatus;
    private Long orderNum;

    public OrderWithAddress() {
    }

    public OrderWithAddress(
            Long orderId,
            Long accountId,
            String orderAddress,
            BigDecimal orderAmount,
            Date orderDatetime,
            String orderType,
            String orderStatus,
            Long orderNum) {
        this.orderId = orderId;
        this.accountId = accountId;
        this.orderAddress = orderAddress;
        this.orderAmount = orderAmount;
        this.orderDatetime = orderDatetime;
        this.orderType = OrderType.getValueByName(orderType);
        this.orderStatus = OrderStatus.getValueByName(orderStatus);
        this.orderNum = orderNum;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null || getClass() != obj.getClass()) {
            return false;
        }
        OrderWithAddress myClass = (OrderWithAddress) obj;
        return Objects.equals(orderId, myClass.orderId) ||
                Objects.equals(accountId, myClass.accountId) ||
                Objects.equals(orderAddress, myClass.orderAddress) ||
                Objects.equals(orderAmount, myClass.orderAmount) ||
                Objects.equals(orderDatetime, myClass.orderDatetime) ||
                Objects.equals(orderType, myClass.orderType) ||
                Objects.equals(orderStatus, myClass.orderStatus) ||
                Objects.equals(orderNum, myClass.orderNum);
    }
}
