package com.main.entities.vendingPoints;


import lombok.Data;

@Data
public class FunctionVariantEntity {
    private Long functionVariantId;
    private Long vendingPointId;
    private Long machineId;
    private FunctionVariantEnum functionVariant;

    public FunctionVariantEntity(
            Long functionVariantId,
            Long vendingPointId,
            Long machineId,
            String functionVariant) {
        this.functionVariantId = functionVariantId;
        this.vendingPointId = vendingPointId;
        this.machineId = machineId;
        this.functionVariant = FunctionVariantEnum.getValueByName(functionVariant);
    }
}
