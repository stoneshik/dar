package com.main.entities.order;

import lombok.Data;

@Data
public class OrderScanWithNumberPages {
    private OrderWithAddress orderInfo;
    private Long numberPages;
    public OrderScanWithNumberPages(OrderWithAddress orderInfo, Long numberPages) {
        this.orderInfo = orderInfo;
        this.numberPages = numberPages;
    }
}
