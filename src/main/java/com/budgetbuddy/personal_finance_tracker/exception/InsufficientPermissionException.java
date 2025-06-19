package com.budgetbuddy.personal_finance_tracker.exception;

public class InsufficientPermissionException extends RuntimeException {

    private final String action;
    private final String resource;
    private final String userId;

    public InsufficientPermissionException(String action, String resource) {
        super(String.format("Insufficient permission to perform '%s' on '%s'", action, resource));
        this.action = action;
        this.resource = resource;
        this.userId = null;
    }

    public InsufficientPermissionException(String action, String resource, String userId) {
        super(String.format("User '%s' has insufficient permission to perform '%s' on '%s'",
                userId, action, resource));
        this.action = action;
        this.resource = resource;
        this.userId = userId;
    }

    public InsufficientPermissionException(String message) {
        super(message);
        this.action = null;
        this.resource = null;
        this.userId = null;
    }

    public InsufficientPermissionException(String message, Throwable cause) {
        super(message, cause);
        this.action = null;
        this.resource = null;
        this.userId = null;
    }

    public String getAction() {
        return action;
    }

    public String getResource() {
        return resource;
    }

    public String getUserId() {
        return userId;
    }
}
