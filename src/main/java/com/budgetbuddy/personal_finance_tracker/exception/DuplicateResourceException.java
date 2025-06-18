package com.budgetbuddy.personal_finance_tracker.exception;

public class DuplicateResourceException extends RuntimeException {

    private final String resourceType;
    private final String field;
    private final Object value;

    public DuplicateResourceException(String resourceType, String field, Object value) {
        super(String.format("%s with %s '%s' already exists", resourceType, field, value));
        this.resourceType = resourceType;
        this.field = field;
        this.value = value;
    }

    public DuplicateResourceException(String message) {
        super(message);
        this.resourceType = null;
        this.field = null;
        this.value = null;
    }

    public DuplicateResourceException(String message, Throwable cause) {
        super(message, cause);
        this.resourceType = null;
        this.field = null;
        this.value = null;
    }

    public String getResourceType() {
        return resourceType;
    }

    public String getField() {
        return field;
    }

    public Object getValue() {
        return value;
    }
}
