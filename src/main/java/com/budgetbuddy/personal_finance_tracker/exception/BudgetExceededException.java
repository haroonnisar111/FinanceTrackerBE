package com.budgetbuddy.personal_finance_tracker.exception;

import java.math.BigDecimal;

public class BudgetExceededException extends RuntimeException {

    private final BigDecimal budgetAmount;
    private final BigDecimal currentSpent;
    private final BigDecimal transactionAmount;
    private final String categoryName;

    public BudgetExceededException(String categoryName, BigDecimal budgetAmount,
                                   BigDecimal currentSpent, BigDecimal transactionAmount) {
        super(String.format(
                "Transaction amount of $%.2f would exceed the budget for category '%s'. " +
                        "Budget: $%.2f, Currently spent: $%.2f, Remaining: $%.2f",
                transactionAmount, categoryName, budgetAmount, currentSpent,
                budgetAmount.subtract(currentSpent)
        ));
        this.categoryName = categoryName;
        this.budgetAmount = budgetAmount;
        this.currentSpent = currentSpent;
        this.transactionAmount = transactionAmount;
    }

    public BudgetExceededException(String categoryName, BigDecimal budgetAmount,
                                   BigDecimal currentSpent, BigDecimal transactionAmount,
                                   String customMessage) {
        super(customMessage);
        this.categoryName = categoryName;
        this.budgetAmount = budgetAmount;
        this.currentSpent = currentSpent;
        this.transactionAmount = transactionAmount;
    }

    public BudgetExceededException(String message) {
        super(message);
        this.budgetAmount = null;
        this.currentSpent = null;
        this.transactionAmount = null;
        this.categoryName = null;
    }

    public BudgetExceededException(String message, Throwable cause) {
        super(message, cause);
        this.budgetAmount = null;
        this.currentSpent = null;
        this.transactionAmount = null;
        this.categoryName = null;
    }

    public BigDecimal getBudgetAmount() {
        return budgetAmount;
    }

    public BigDecimal getCurrentSpent() {
        return currentSpent;
    }

    public BigDecimal getTransactionAmount() {
        return transactionAmount;
    }

    public String getCategoryName() {
        return categoryName;
    }

    public BigDecimal getRemainingBudget() {
        if (budgetAmount != null && currentSpent != null) {
            return budgetAmount.subtract(currentSpent);
        }
        return null;
    }

    public BigDecimal getExcessAmount() {
        if (budgetAmount != null && currentSpent != null && transactionAmount != null) {
            BigDecimal totalAfterTransaction = currentSpent.add(transactionAmount);
            return totalAfterTransaction.subtract(budgetAmount);
        }
        return null;
    }
}