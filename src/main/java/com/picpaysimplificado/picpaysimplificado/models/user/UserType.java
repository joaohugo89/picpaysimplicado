package com.picpaysimplificado.picpaysimplificado.models.user;

public enum UserType {
    ADMIN,
    COMMON,
    MERCHANT;

    public static UserType fromString(String type) {
        for (UserType userType : UserType.values()) {
            if (userType.name().equalsIgnoreCase(type)) {
                return userType;
            }
        }
        throw new IllegalArgumentException("Unknown user type: " + type);
    }
}
