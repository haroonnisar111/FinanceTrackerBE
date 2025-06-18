package com.budgetbuddy.personal_finance_tracker.exception;
public class CategoryNotFoundException extends RuntimeException {

    private final Long categoryId;
    private final String categoryName;

    public CategoryNotFoundException(Long categoryId) {
        super(String.format("Category with ID %d not found", categoryId));
        this.categoryId = categoryId;
        this.categoryName = null;
    }

    public CategoryNotFoundException(String categoryName) {
        super(String.format("Category with name '%s' not found", categoryName));
        this.categoryId = null;
        this.categoryName = categoryName;
    }

    public CategoryNotFoundException(String message, Throwable cause) {
        super(message, cause);
        this.categoryId = null;
        this.categoryName = null;
    }

    public CategoryNotFoundException(Long categoryId, String customMessage) {
        super(customMessage);
        this.categoryId = categoryId;
        this.categoryName = null;
    }

    public Long getCategoryId() {
        return categoryId;
    }

    public String getCategoryName() {
        return categoryName;
    }
}
