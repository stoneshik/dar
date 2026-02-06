package com.main.entities.vendingPoints;

import java.math.BigDecimal;
import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class VendingPointWithFunctionVariant {
    private Long vendingPointId;
    private String vendingPointAddress;
    private String vendingPointDescription;
    private Long vendingPointNumberMachines;
    private BigDecimal[] vendingPointCords;
    private List<FunctionVariantEntity> functionVariants;
}
