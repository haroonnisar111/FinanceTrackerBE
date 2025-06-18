package com.budgetbuddy.personal_finance_tracker.exception;

public class TransactionNotFoundException extends RuntimeException {

    private final Long transactionId;

    public TransactionNotFoundException(Long transactionId) {
        super(String.format("Transaction with ID %d not found", transactionId));
        this.transactionId = transactionId;
    }

    public TransactionNotFoundException(String message) {
        super(message);
        this.transactionId = null;
    }

    public TransactionNotFoundException(String message, Throwable cause) {
        super(message, cause);
        this.transactionId = null;
    }

    public TransactionNotFoundException(Long transactionId, String customMessage) {
        super(customMessage);
        this.transactionId = transactionId;
    }

    public Long getTransactionId() {
        return transactionId;
    }
}

