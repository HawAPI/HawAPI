package com.lucasjosino.hawapi.enums.auth;

public enum RoleType {
    DEV,
    MOD,
    ADMIN;

    public static boolean isValid(String name) {
        for (RoleType role : values()) {
            if (role.name().equals(name)) return true;
        }
        return false;
    }
}
