package com.main.entities.user;


public enum UserStatus {
    UNVERIFIED("unverified"),
    VERIFIED("verified"),
    BANNED("banned");
    private final String name;
    UserStatus(String name) {
        this.name = name;
    }

    public final String getName() {
        return name;
    }

    public static UserStatus getValueByName(String name) {
        for (final UserStatus value : UserStatus.values()) {
            if (value.getName().equals(name)) {
                return value;
            }
        }
        return null;
    }
}
