package com.main.entities.order;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class OrderScanWithNumberPages {
    private OrderWithAddress orderInfo;
    private Long numberPages;
}
