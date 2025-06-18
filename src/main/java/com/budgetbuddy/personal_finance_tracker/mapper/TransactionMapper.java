package com.budgetbuddy.personal_finance_tracker.mapper;

import com.budgetbuddy.personal_finance_tracker.dto.TransactionRequest;
import com.budgetbuddy.personal_finance_tracker.dto.TransactionResponse;
import com.budgetbuddy.personal_finance_tracker.entity.Transaction;
import com.budgetbuddy.personal_finance_tracker.entity.Category;
import org.springframework.stereotype.Component;

@Component
public class TransactionMapper {

    public Transaction toEntity(TransactionRequest request) {
        if (request == null) {
            return null;
        }

        Transaction transaction = new Transaction();
        transaction.setDescription(request.getDescription());
        transaction.setAmount(request.getAmount());
        transaction.setTransactionDate(request.getTransactionDate());
        transaction.setType(request.getType());
        transaction.setNotes(request.getNotes());

        // Set category
        if (request.getCategoryId() != null) {
            Category category = new Category();
            category.setId(request.getCategoryId());
            transaction.setCategory(category);
        }

        return transaction;
    }

    public TransactionResponse toResponse(Transaction transaction) {
        if (transaction == null) {
            return null;
        }

        return TransactionResponse.builder()
                .id(transaction.getId())
                .description(transaction.getDescription())
                .amount(transaction.getAmount())
                .transactionDate(transaction.getTransactionDate())
                .type(transaction.getType())
                .notes(transaction.getNotes())
                .categoryId(transaction.getCategory() != null ?
                        transaction.getCategory().getId() : null)
                .categoryName(transaction.getCategory() != null ?
                        transaction.getCategory().getName() : null)
                .createdAt(transaction.getCreatedAt())
                .updatedAt(transaction.getUpdatedAt())
                .build();
    }

    public Transaction updateEntity(Transaction existingTransaction, TransactionRequest request) {
        if (request == null || existingTransaction == null) {
            return existingTransaction;
        }

        if (request.getDescription() != null) {
            existingTransaction.setDescription(request.getDescription());
        }
        if (request.getAmount() != null) {
            existingTransaction.setAmount(request.getAmount());
        }
        if (request.getTransactionDate() != null) {
            existingTransaction.setTransactionDate(request.getTransactionDate());
        }
        if (request.getType() != null) {
            existingTransaction.setType(request.getType());
        }
        if (request.getNotes() != null) {
            existingTransaction.setNotes(request.getNotes());
        }
        if (request.getCategoryId() != null) {
            Category category = new Category();
            category.setId(request.getCategoryId());
            existingTransaction.setCategory(category);
        }

        return existingTransaction;
    }
}