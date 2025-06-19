package com.budgetbuddy.personal_finance_tracker.exception;

import java.util.Map;

public class BusinessRuleViolationException extends RuntimeException {

    private final String rule;
    private final String violationType;
    private final Map<String, Object> context;

    public BusinessRuleViolationException(String rule, String message) {
        super(String.format("Business rule violation - %s: %s", rule, message));
        this.rule = rule;
        this.violationType = null;
        this.context = null;
    }

    public BusinessRuleViolationException(String rule, String violationType, String message) {
        super(String.format("Business rule violation - %s (%s): %s", rule, violationType, message));
        this.rule = rule;
        this.violationType = violationType;
        this.context = null;
    }

    public BusinessRuleViolationException(String rule, String violationType, String message,
                                          Map<String, Object> context) {
        super(String.format("Business rule violation - %s (%s): %s", rule, violationType, message));
        this.rule = rule;
        this.violationType = violationType;
        this.context = context;
    }

    public BusinessRuleViolationException(String message) {
        super(message);
        this.rule = null;
        this.violationType = null;
        this.context = null;
    }

    public BusinessRuleViolationException(String message, Throwable cause) {
        super(message, cause);
        this.rule = null;
        this.violationType = null;
        this.context = null;
    }

    public String getRule() {
        return rule;
    }

    public String getViolationType() {
        return violationType;
    }

    public Map<String, Object> getContext() {
        return context;
    }
}
