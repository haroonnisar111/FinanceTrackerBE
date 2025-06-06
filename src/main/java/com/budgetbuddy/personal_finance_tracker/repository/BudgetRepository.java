package com.budgetbuddy.personal_finance_tracker.repository;
import com.budgetbuddy.personal_finance_tracker.entity.Budget;
import com.budgetbuddy.personal_finance_tracker.entity.Category;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public interface BudgetRepository extends JpaRepository<Budget, Long> {

    // Basic queries
    List<Budget> findByIsActiveTrueOrderByStartDateDesc();

    List<Budget> findByCategoryOrderByStartDateDesc(Category category);

    List<Budget> findByPeriodAndIsActiveTrueOrderByStartDateDesc(Budget.BudgetPeriod period);

    // Active budgets
    @Query("SELECT b FROM Budget b WHERE b.isActive = true AND :currentDate >= b.startDate AND :currentDate <= b.endDate")
    List<Budget> findActiveBudgets(@Param("currentDate") LocalDate currentDate);

    @Query("SELECT b FROM Budget b WHERE b.category = :category AND b.isActive = true AND :currentDate >= b.startDate AND :currentDate <= b.endDate")
    Optional<Budget> findActiveBudgetByCategory(@Param("category") Category category, @Param("currentDate") LocalDate currentDate);

    // Date range queries
    List<Budget> findByStartDateGreaterThanEqualAndEndDateLessThanEqual(LocalDate startDate, LocalDate endDate);

    @Query("SELECT b FROM Budget b WHERE b.startDate <= :date AND b.endDate >= :date")
    List<Budget> findBudgetsActiveOnDate(@Param("date") LocalDate date);

    // Overlapping budgets
    @Query("SELECT b FROM Budget b WHERE b.category = :category AND b.id != :budgetId AND ((b.startDate <= :endDate AND b.endDate >= :startDate))")
    List<Budget> findOverlappingBudgets(
            @Param("category") Category category,
            @Param("budgetId") Long budgetId,
            @Param("startDate") LocalDate startDate,
            @Param("endDate") LocalDate endDate);

    // Budget status queries
    @Query("SELECT b FROM Budget b WHERE b.isActive = true AND b.spentAmount > b.budgetAmount")
    List<Budget> findExceededBudgets();

    @Query("SELECT b FROM Budget b WHERE b.isActive = true AND (b.spentAmount / b.budgetAmount) >= :threshold")
    List<Budget> findBudgetsNearLimit(@Param("threshold") BigDecimal threshold);

    @Query("SELECT b FROM Budget b WHERE b.isActive = true AND :currentDate >= b.startDate AND :currentDate <= b.endDate AND (b.spentAmount / b.budgetAmount) >= 0.8")
    List<Budget> findBudgetsOver80Percent(@Param("currentDate") LocalDate currentDate);

    // Update spent amount
    @Modifying
    @Query("UPDATE Budget b SET b.spentAmount = :spentAmount WHERE b.id = :budgetId")
    void updateSpentAmount(@Param("budgetId") Long budgetId, @Param("spentAmount") BigDecimal spentAmount);

    @Modifying
    @Query("UPDATE Budget b SET b.spentAmount = b.spentAmount + :amount WHERE b.id = :budgetId")
    void addToSpentAmount(@Param("budgetId") Long budgetId, @Param("amount") BigDecimal amount);

    @Modifying
    @Query("UPDATE Budget b SET b.spentAmount = b.spentAmount - :amount WHERE b.id = :budgetId")
    void subtractFromSpentAmount(@Param("budgetId") Long budgetId, @Param("amount") BigDecimal amount);

    // Summary queries
    @Query("SELECT SUM(b.budgetAmount) FROM Budget b WHERE b.isActive = true AND :currentDate >= b.startDate AND :currentDate <= b.endDate")
    BigDecimal getTotalActiveBudgetAmount(@Param("currentDate") LocalDate currentDate);

    @Query("SELECT SUM(b.spentAmount) FROM Budget b WHERE b.isActive = true AND :currentDate >= b.startDate AND :currentDate <= b.endDate")
    BigDecimal getTotalActiveSpentAmount(@Param("currentDate") LocalDate currentDate);

    // Period-based queries
    @Query("SELECT b FROM Budget b WHERE b.period = :period AND FUNCTION('YEAR', b.startDate) = :year AND FUNCTION('MONTH', b.startDate) = :month")
    List<Budget> findByPeriodAndYearMonth(
            @Param("period") Budget.BudgetPeriod period,
            @Param("year") int year,
            @Param("month") int month);

    // Count queries
    @Query("SELECT COUNT(b) FROM Budget b WHERE b.isActive = true AND :currentDate >= b.startDate AND :currentDate <= b.endDate")
    long countActiveBudgets(@Param("currentDate") LocalDate currentDate);

    @Query("SELECT COUNT(b) FROM Budget b WHERE b.category = :category")
    long countBudgetsByCategory(@Param("category") Category category);

    // Statistics
    @Query("SELECT AVG(b.budgetAmount) FROM Budget b WHERE b.isActive = true")
    BigDecimal getAverageBudgetAmount();

    @Query("SELECT c.name, AVG(b.spentAmount / b.budgetAmount * 100) FROM Budget b JOIN b.category c WHERE b.isActive = true GROUP BY c.name ORDER BY AVG(b.spentAmount / b.budgetAmount * 100) DESC")
    List<Object[]> getCategoryBudgetUtilizationStats();
}