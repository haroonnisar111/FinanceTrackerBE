package com.budgetbuddy.personal_finance_tracker.exception;

import java.util.Map;

public class InvalidDataException extends RuntimeException {

    private final String field;
    private final Object value;
    private final Map<String, Object> additionalInfo;

    public InvalidDataException(String message) {
        super(message);
        this.field = null;
        this.value = null;
        this.additionalInfo = null;
    }

    public InvalidDataException(String field, Object value, String message) {
        super(String.format("Invalid value for field '%s': %s. %s", field, value, message));
        this.field = field;
        this.value = value;
        this.additionalInfo = null;
    }

    public InvalidDataException(String field, Object value, String message, Map<String, Object> additionalInfo) {
        super(String.format("Invalid value for field '%s': %s. %s", field, value, message));
        this.field = field;
        this.value = value;
        this.additionalInfo = additionalInfo;
    }

    public InvalidDataException(String message, Throwable cause) {
        super(message, cause);
        this.field = null;
        this.value = null;
        this.additionalInfo = null;
    }

    public InvalidDataException(String field, Object value, String message, Throwable cause) {
        super(String.format("Invalid value for field '%s': %s. %s", field, value, message), cause);
        this.field = field;
        this.value = value;
        this.additionalInfo = null;
    }

    public String getField() {
        return field;
    }

    public Object getValue() {
        return value;
    }

    public Map<String, Object> getAdditionalInfo() {
        return additionalInfo;
    }
}
