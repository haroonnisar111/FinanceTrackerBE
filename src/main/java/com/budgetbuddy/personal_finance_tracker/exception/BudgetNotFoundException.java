package com.budgetbuddy.personal_finance_tracker.exception;

public class BudgetNotFoundException extends RuntimeException {

    private final Long budgetId;
    private final String categoryName;
    private final String period;

    public BudgetNotFoundException(Long budgetId) {
        super(String.format("Budget with ID %d not found", budgetId));
        this.budgetId = budgetId;
        this.categoryName = null;
        this.period = null;
    }

    public BudgetNotFoundException(String categoryName, String period) {
        super(String.format("Budget for category '%s' and period '%s' not found", categoryName, period));
        this.categoryName = categoryName;
        this.period = period;
        this.budgetId = null;
    }

    public BudgetNotFoundException(String message) {
        super(message);
        this.budgetId = null;
        this.categoryName = null;
        this.period = null;
    }

    public BudgetNotFoundException(String message, Throwable cause) {
        super(message, cause);
        this.budgetId = null;
        this.categoryName = null;
        this.period = null;
    }

    public Long getBudgetId() {
        return budgetId;
    }

    public String getCategoryName() {
        return categoryName;
    }

    public String getPeriod() {
        return period;
    }
}
