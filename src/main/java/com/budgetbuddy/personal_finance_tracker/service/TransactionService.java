package com.budgetbuddy.personal_finance_tracker.service;
import com.budgetbuddy.personal_finance_tracker.entity.Transaction;
import com.budgetbuddy.personal_finance_tracker.entity.Transaction.TransactionType;
import com.budgetbuddy.personal_finance_tracker.entity.Category;
import com.budgetbuddy.personal_finance_tracker.repository.TransactionRepository;
import com.budgetbuddy.personal_finance_tracker.repository.CategoryRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class TransactionService {

    private final TransactionRepository transactionRepository;
    private final CategoryRepository categoryRepository;
    private final BudgetService budgetService;


    public Transaction createTransaction(Transaction transaction) {
        log.info("Creating new transaction: {}", transaction.getDescription());

        Category category = categoryRepository.findById(transaction.getCategory().getId())
                .orElseThrow(() -> new IllegalArgumentException("Category not found"));

        transaction.setCategory(category);
        Transaction savedTransaction = transactionRepository.save(transaction);

        // Update budget if transaction is an expense
        if (transaction.getType() == TransactionType.EXPENSE) {
            budgetService.updateBudgetSpending(category, transaction.getAmount(), transaction.getTransactionDate());
        }

        return savedTransaction;
    }

    @Transactional(readOnly = true)
    public Optional<Transaction> findById(Long id) {
        return transactionRepository.findById(id);
    }

    @Transactional(readOnly = true)
    public Page<Transaction> findAllTransactions(Pageable pageable) {
        return transactionRepository.findByOrderByTransactionDateDesc(pageable);
    }

    @Transactional(readOnly = true)
    public List<Transaction> findTransactionsByType(TransactionType type) {
        return transactionRepository.findByTypeOrderByTransactionDateDesc(type);
    }

    @Transactional(readOnly = true)
    public List<Transaction> findTransactionsByCategory(Long categoryId) {
        Category category = categoryRepository.findById(categoryId)
                .orElseThrow(() -> new IllegalArgumentException("Category not found"));
        return transactionRepository.findByCategoryOrderByTransactionDateDesc(category);
    }

    @Transactional(readOnly = true)
    public List<Transaction> findTransactionsByDateRange(LocalDate startDate, LocalDate endDate) {
        return transactionRepository.findByTransactionDateBetweenOrderByTransactionDateDesc(startDate, endDate);
    }

    @Transactional(readOnly = true)
    public List<Transaction> findTransactionsByDateRangeAndType(LocalDate startDate, LocalDate endDate, TransactionType type) {
        return transactionRepository.findByDateRangeAndType(startDate, endDate, type);
    }

    @Transactional(readOnly = true)
    public List<Transaction> searchTransactions(String keyword) {
        return transactionRepository.findByDescriptionOrNotesContaining(keyword);
    }

    @Transactional(readOnly = true)
    public List<Transaction> findRecentTransactions(int limit) {
        return transactionRepository.findRecentTransactions(PageRequest.of(0, limit));
    }

    public Transaction updateTransaction(Long id, Transaction updatedTransaction) {
        Transaction existingTransaction = transactionRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Transaction not found"));

        // Handle budget updates if amount or category changed
        if (existingTransaction.getType() == TransactionType.EXPENSE) {
            budgetService.updateBudgetSpending(existingTransaction.getCategory(),
                    existingTransaction.getAmount().negate(), existingTransaction.getTransactionDate());
        }

        Category category = categoryRepository.findById(updatedTransaction.getCategory().getId())
                .orElseThrow(() -> new IllegalArgumentException("Category not found"));

        existingTransaction.setDescription(updatedTransaction.getDescription());
        existingTransaction.setAmount(updatedTransaction.getAmount());
        existingTransaction.setTransactionDate(updatedTransaction.getTransactionDate());
        existingTransaction.setType(updatedTransaction.getType());
        existingTransaction.setCategory(category);
        existingTransaction.setNotes(updatedTransaction.getNotes());

        Transaction savedTransaction = transactionRepository.save(existingTransaction);

        // Update budget with new amount
        if (savedTransaction.getType() == TransactionType.EXPENSE) {
            budgetService.updateBudgetSpending(category, savedTransaction.getAmount(), savedTransaction.getTransactionDate());
        }

        return savedTransaction;
    }

    public void deleteTransaction(Long id) {
        Transaction transaction = transactionRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Transaction not found"));

        // Update budget if deleting an expense
        if (transaction.getType() == TransactionType.EXPENSE) {
            budgetService.updateBudgetSpending(transaction.getCategory(),
                    transaction.getAmount().negate(), transaction.getTransactionDate());
        }

        transactionRepository.delete(transaction);
    }

    // Analytics methods
    @Transactional(readOnly = true)
    public BigDecimal getTotalIncomeByDateRange(LocalDate startDate, LocalDate endDate) {
        BigDecimal total = transactionRepository.sumAmountByTypeAndDateRange(TransactionType.INCOME, startDate, endDate);
        return total != null ? total : BigDecimal.ZERO;
    }

    @Transactional(readOnly = true)
    public BigDecimal getTotalExpenseByDateRange(LocalDate startDate, LocalDate endDate) {
        BigDecimal total = transactionRepository.sumAmountByTypeAndDateRange(TransactionType.EXPENSE, startDate, endDate);
        return total != null ? total : BigDecimal.ZERO;
    }

    @Transactional(readOnly = true)
    public BigDecimal getNetAmountByDateRange(LocalDate startDate, LocalDate endDate) {
        BigDecimal income = getTotalIncomeByDateRange(startDate, endDate);
        BigDecimal expense = getTotalExpenseByDateRange(startDate, endDate);
        return income.subtract(expense);
    }

    @Transactional(readOnly = true)
    public List<Object[]> getMonthlySummary(TransactionType type) {
        return transactionRepository.getMonthlySummaryByType(type);
    }

    @Transactional(readOnly = true)
    public List<Object[]> getCategorySummary(TransactionType type, LocalDate startDate, LocalDate endDate) {
        return transactionRepository.getCategorySummaryByTypeAndDateRange(type, startDate, endDate);
    }
}
