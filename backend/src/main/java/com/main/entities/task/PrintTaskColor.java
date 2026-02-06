package com.main.entities.task;

public enum PrintTaskColor {
    BLACK_WHITE("black_white"), COLOR("color");
    private final String name;
    PrintTaskColor(String name) {
        this.name = name;
    }

    public final String getName() {
        return name;
    }

    public static PrintTaskColor getValueByName(String name) {
        for (final PrintTaskColor value : PrintTaskColor.values()) {
            if (value.getName().equals(name)) {
                return value;
            }
        }
        return null;
    }
}
