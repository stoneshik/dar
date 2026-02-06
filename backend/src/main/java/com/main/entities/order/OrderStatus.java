package com.main.entities.order;

public enum OrderStatus {
    NOT_PAID("not_paid"), PAID("paid"), COMPLETED("completed");
    private final String name;
    OrderStatus(String name) {
        this.name = name;
    }

    public final String getName() {
        return name;
    }

    public static OrderStatus getValueByName(String name) {
        for (final OrderStatus value : OrderStatus.values()) {
            if (value.getName().equals(name)) {
                return value;
            }
        }
        return null;
    }
}
