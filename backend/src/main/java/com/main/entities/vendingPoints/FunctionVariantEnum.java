package com.main.entities.vendingPoints;


public enum FunctionVariantEnum {
    BLACK_WHITE_PRINT("black_white_print"),
    COLOR_PRINT("color_print"),
    SCAN("scan");
    private final String name;
    FunctionVariantEnum(String name) {
        this.name = name;
    }

    public final String getName() {
        return name;
    }

    public static FunctionVariantEnum getValueByName(String name) {
        for (final FunctionVariantEnum value : FunctionVariantEnum.values()) {
            if (value.getName().equals(name)) {
                return value;
            }
        }
        return null;
    }
}
