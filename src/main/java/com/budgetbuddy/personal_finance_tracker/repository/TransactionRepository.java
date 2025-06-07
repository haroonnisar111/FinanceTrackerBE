package com.budgetbuddy.personal_finance_tracker.repository;
import com.budgetbuddy.personal_finance_tracker.entity.Category;
import com.budgetbuddy.personal_finance_tracker.entity.Transaction;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

@Repository
public interface TransactionRepository extends JpaRepository<Transaction, Long> {

    // Basic queries
    List<Transaction> findByTypeOrderByTransactionDateDesc(Transaction.TransactionType type);

    List<Transaction> findByCategoryOrderByTransactionDateDesc(Category category);

    Page<Transaction> findByOrderByTransactionDateDesc(Pageable pageable);

    // Date range queries
    List<Transaction> findByTransactionDateBetweenOrderByTransactionDateDesc(
            LocalDate startDate, LocalDate endDate);

    @Query("SELECT t FROM Transaction t WHERE t.transactionDate >= :startDate AND t.transactionDate <= :endDate AND t.type = :type ORDER BY t.transactionDate DESC")
    List<Transaction> findByDateRangeAndType(
            @Param("startDate") LocalDate startDate,
            @Param("endDate") LocalDate endDate,
            @Param("type") Transaction.TransactionType type);

    @Query("SELECT t FROM Transaction t WHERE t.category = :category AND t.transactionDate >= :startDate AND t.transactionDate <= :endDate ORDER BY t.transactionDate DESC")
    List<Transaction> findByCategoryAndDateRange(
            @Param("category") Category category,
            @Param("startDate") LocalDate startDate,
            @Param("endDate") LocalDate endDate);

    // Amount-based queries
    List<Transaction> findByAmountGreaterThanEqualOrderByAmountDesc(BigDecimal amount);

    List<Transaction> findByAmountBetweenOrderByAmountDesc(BigDecimal minAmount, BigDecimal maxAmount);

    // Search queries
    @Query("SELECT t FROM Transaction t WHERE LOWER(t.description) LIKE LOWER(CONCAT('%', :keyword, '%')) OR LOWER(t.notes) LIKE LOWER(CONCAT('%', :keyword, '%')) ORDER BY t.transactionDate DESC")
    List<Transaction> findByDescriptionOrNotesContaining(@Param("keyword") String keyword);

    // Aggregate queries
    @Query("SELECT SUM(t.amount) FROM Transaction t WHERE t.type = :type")
    BigDecimal sumAmountByType(@Param("type") Transaction.TransactionType type);

    @Query("SELECT SUM(t.amount) FROM Transaction t WHERE t.type = :type AND t.transactionDate >= :startDate AND t.transactionDate <= :endDate")
    BigDecimal sumAmountByTypeAndDateRange(
            @Param("type") Transaction.TransactionType type,
            @Param("startDate") LocalDate startDate,
            @Param("endDate") LocalDate endDate);

    @Query("SELECT SUM(t.amount) FROM Transaction t WHERE t.category = :category AND t.transactionDate >= :startDate AND t.transactionDate <= :endDate")
    BigDecimal sumAmountByCategoryAndDateRange(
            @Param("category") Category category,
            @Param("startDate") LocalDate startDate,
            @Param("endDate") LocalDate endDate);

    // Monthly summaries
    @Query("SELECT FUNCTION('YEAR', t.transactionDate) as year, FUNCTION('MONTH', t.transactionDate) as month, SUM(t.amount) as total FROM Transaction t WHERE t.type = :type GROUP BY FUNCTION('YEAR', t.transactionDate), FUNCTION('MONTH', t.transactionDate) ORDER BY year DESC, month DESC")
    List<Object[]> getMonthlySummaryByType(@Param("type") Transaction.TransactionType type);

    @Query("SELECT c.name, SUM(t.amount) FROM Transaction t JOIN t.category c WHERE t.type = :type AND t.transactionDate >= :startDate AND t.transactionDate <= :endDate GROUP BY c.name ORDER BY SUM(t.amount) DESC")
    List<Object[]> getCategorySummaryByTypeAndDateRange(
            @Param("type") Transaction.TransactionType type,
            @Param("startDate") LocalDate startDate,
            @Param("endDate") LocalDate endDate);

    // Recent transactions
    @Query("SELECT t FROM Transaction t ORDER BY t.createdAt DESC")
    List<Transaction> findRecentTransactions(Pageable pageable);

    // Count queries
    @Query("SELECT COUNT(t) FROM Transaction t WHERE t.transactionDate >= :startDate AND t.transactionDate <= :endDate")
    long countTransactionsByDateRange(
            @Param("startDate") LocalDate startDate,
            @Param("endDate") LocalDate endDate);

    @Query("SELECT COUNT(t) FROM Transaction t WHERE t.category = :category")
    long countTransactionsByCategory(@Param("category") Category category);

    // Statistics
    @Query("SELECT AVG(t.amount) FROM Transaction t WHERE t.type = :type")
    BigDecimal getAverageAmountByType(@Param("type") Transaction.TransactionType type);

    @Query("SELECT MAX(t.amount) FROM Transaction t WHERE t.type = :type")
    BigDecimal getMaxAmountByType(@Param("type") Transaction.TransactionType type);

    @Query("SELECT MIN(t.amount) FROM Transaction t WHERE t.type = :type")
    BigDecimal getMinAmountByType(@Param("type") Transaction.TransactionType type);
}
