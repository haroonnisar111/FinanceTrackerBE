package com.budgetbuddy.personal_finance_tracker.exception;

public class UserNotFoundException extends RuntimeException {

    private final Long userId;
    private final String username;
    private final String email;

    public UserNotFoundException(Long userId) {
        super(String.format("User with ID %d not found", userId));
        this.userId = userId;
        this.username = null;
        this.email = null;
    }

    public UserNotFoundException(String identifier, String type) {
        super(String.format("User with %s '%s' not found", type, identifier));
        this.userId = null;
        if ("username".equals(type)) {
            this.username = identifier;
            this.email = null;
        } else if ("email".equals(type)) {
            this.email = identifier;
            this.username = null;
        } else {
            this.username = null;
            this.email = null;
        }
    }

    public UserNotFoundException(String message) {
        super(message);
        this.userId = null;
        this.username = null;
        this.email = null;
    }

    public UserNotFoundException(String message, Throwable cause) {
        super(message, cause);
        this.userId = null;
        this.username = null;
        this.email = null;
    }

    public Long getUserId() {
        return userId;
    }

    public String getUsername() {
        return username;
    }

    public String getEmail() {
        return email;
    }
}