package com.main.entities.order;


public enum OrderType {
    PRINT("print"), SCAN("scan");
    private final String name;
    OrderType(String name) {
        this.name = name;
    }

    public final String getName() {
        return name;
    }

    public static OrderType getValueByName(String name) {
        for (final OrderType value : OrderType.values()) {
            if (value.getName().equals(name)) {
                return value;
            }
        }
        return null;
    }
}
