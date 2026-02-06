package com.main.entities.vendingPoints;

import lombok.Data;

import java.math.BigDecimal;
import java.util.List;


@Data
public class VendingPointWithFunctionVariant {
    private Long vendingPointId;
    private String vendingPointAddress;
    private String vendingPointDescription;
    private Long vendingPointNumberMachines;
    private BigDecimal[] vendingPointCords;
    private List<FunctionVariantEntity> functionVariants;

    public VendingPointWithFunctionVariant(
            Long vendingPointId,
            String vendingPointAddress,
            String vendingPointDescription,
            Long vendingPointNumberMachines,
            BigDecimal[] vendingPointCords,
            List<FunctionVariantEntity> functionVariants) {
        this.vendingPointId = vendingPointId;
        this.vendingPointAddress = vendingPointAddress;
        this.vendingPointDescription = vendingPointDescription;
        this.vendingPointNumberMachines = vendingPointNumberMachines;
        this.vendingPointCords = vendingPointCords;
        this.functionVariants = functionVariants;
    }
}
