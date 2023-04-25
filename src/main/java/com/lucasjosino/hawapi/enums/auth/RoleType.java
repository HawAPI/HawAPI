package com.lucasjosino.hawapi.enums.auth;

public enum RoleType {
    ANONYMOUS,
    BASIC,
    DEV,
    ADMIN;

    public static boolean isValid(String name) {
        for (RoleType role : values()) {
            if (role.name().equalsIgnoreCase(name)) return true;
        }
        return false;
    }
}
