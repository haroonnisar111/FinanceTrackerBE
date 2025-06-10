package com.budgetbuddy.personal_finance_tracker.service;
import com.budgetbuddy.personal_finance_tracker.entity.Budget;
import com.budgetbuddy.personal_finance_tracker.entity.Budget.BudgetPeriod;
import com.budgetbuddy.personal_finance_tracker.entity.Category;
import com.budgetbuddy.personal_finance_tracker.repository.BudgetRepository;
import com.budgetbuddy.personal_finance_tracker.repository.CategoryRepository;
import com.budgetbuddy.personal_finance_tracker.repository.TransactionRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
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
public class BudgetService {

    private final BudgetRepository budgetRepository;
    private final CategoryRepository categoryRepository;
    private final TransactionRepository transactionRepository;

    public BudgetService(BudgetRepository budgetRepository, CategoryRepository categoryRepository, TransactionRepository transactionRepository) {
        this.budgetRepository = budgetRepository;
        this.categoryRepository = categoryRepository;
        this.transactionRepository = transactionRepository;
    }

    public Budget createBudget(Budget budget) {
        log.info("Creating new budget: {}", budget.getName());

        Category category = categoryRepository.findById(budget.getCategory().getId())
                .orElseThrow(() -> new IllegalArgumentException("Category not found"));

        // Check for overlapping budgets
        List<Budget> overlappingBudgets = budgetRepository.findOverlappingBudgets(
                category, 0L, budget.getStartDate(), budget.getEndDate());

        if (!overlappingBudgets.isEmpty()) {
            throw new IllegalArgumentException("Budget period overlaps with existing budget for this category");
        }

        budget.setCategory(category);
        Budget savedBudget = budgetRepository.save(budget);

        // Calculate and set initial spent amount
        recalculateBudgetSpending(savedBudget);

        return savedBudget;
    }

    @Transactional(readOnly = true)
    public Optional<Budget> findById(Long id) {
        return budgetRepository.findById(id);
    }

    @Transactional(readOnly = true)
    public List<Budget> findAllActiveBudgets() {
        return budgetRepository.findActiveBudgets(LocalDate.now());
    }

    @Transactional(readOnly = true)
    public List<Budget> findBudgetsByCategory(Long categoryId) {
        Category category = categoryRepository.findById(categoryId)
                .orElseThrow(() -> new IllegalArgumentException("Category not found"));
        return budgetRepository.findByCategoryOrderByStartDateDesc(category);
    }

    @Transactional(readOnly = true)
    public List<Budget> findBudgetsByPeriod(BudgetPeriod period) {
        return budgetRepository.findByPeriodAndIsActiveTrueOrderByStartDateDesc(period);
    }

    @Transactional(readOnly = true)
    public Optional<Budget> findActiveBudgetByCategory(Long categoryId) {
        Category category = categoryRepository.findById(categoryId)
                .orElseThrow(() -> new IllegalArgumentException("Category not found"));
        return budgetRepository.findActiveBudgetByCategory(category, LocalDate.now());
    }

    @Transactional(readOnly = true)
    public List<Budget> findExceededBudgets() {
        return budgetRepository.findExceededBudgets();
    }

    @Transactional(readOnly = true)
    public List<Budget> findBudgetsNearLimit(double thresholdPercentage) {
        BigDecimal threshold = BigDecimal.valueOf(thresholdPercentage / 100.0);
        return budgetRepository.findBudgetsNearLimit(threshold);
    }

    public Budget updateBudget(Long id, Budget updatedBudget) {
        Budget existingBudget = budgetRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Budget not found"));

        Category category = categoryRepository.findById(updatedBudget.getCategory().getId())
                .orElseThrow(() -> new IllegalArgumentException("Category not found"));

        // Check for overlapping budgets (excluding current budget)
        List<Budget> overlappingBudgets = budgetRepository.findOverlappingBudgets(
                category, id, updatedBudget.getStartDate(), updatedBudget.getEndDate());

        if (!overlappingBudgets.isEmpty()) {
            throw new IllegalArgumentException("Budget period overlaps with existing budget for this category");
        }

        existingBudget.setName(updatedBudget.getName());
        existingBudget.setBudgetAmount(updatedBudget.getBudgetAmount());
        existingBudget.setCategory(category);
        existingBudget.setStartDate(updatedBudget.getStartDate());
        existingBudget.setEndDate(updatedBudget.getEndDate());
        existingBudget.setPeriod(updatedBudget.getPeriod());

        Budget savedBudget = budgetRepository.save(existingBudget);

        // Recalculate spent amount
        recalculateBudgetSpending(savedBudget);

        return savedBudget;
    }

    public void deleteBudget(Long id) {
        Budget budget = budgetRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Budget not found"));

        budgetRepository.delete(budget);
    }

    public void deactivateBudget(Long id) {
        Budget budget = budgetRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Budget not found"));

        budget.setIsActive(false);
        budgetRepository.save(budget);
    }

    public void updateBudgetSpending(Category category, BigDecimal amount, LocalDate transactionDate) {
        Optional<Budget> activeBudget = budgetRepository.findActiveBudgetByCategory(category, transactionDate);

        if (activeBudget.isPresent()) {
            budgetRepository.addToSpentAmount(activeBudget.get().getId(), amount);
        }
    }

    public void recalculateBudgetSpending(Budget budget) {
        BigDecimal totalSpent = transactionRepository.sumAmountByCategoryAndDateRange(
                budget.getCategory(), budget.getStartDate(), budget.getEndDate());

        budget.setSpentAmount(totalSpent != null ? totalSpent : BigDecimal.ZERO);
        budgetRepository.save(budget);
    }

    public void recalculateAllBudgetSpending() {
        List<Budget> allBudgets = budgetRepository.findAll();
        allBudgets.forEach(this::recalculateBudgetSpending);
    }

    // Analytics methods
    @Transactional(readOnly = true)
    public BigDecimal getTotalActiveBudgetAmount() {
        BigDecimal total = budgetRepository.getTotalActiveBudgetAmount(LocalDate.now());
        return total != null ? total : BigDecimal.ZERO;
    }

    @Transactional(readOnly = true)
    public BigDecimal getTotalActiveSpentAmount() {
        BigDecimal total = budgetRepository.getTotalActiveSpentAmount(LocalDate.now());
        return total != null ? total : BigDecimal.ZERO;
    }

    @Transactional(readOnly = true)
    public List<Object[]> getBudgetUtilizationStats() {
        return budgetRepository.getCategoryBudgetUtilizationStats();
    }

    @Transactional(readOnly = true)
    public long getActiveBudgetCount() {
        return budgetRepository.countActiveBudgets(LocalDate.now());
    }
}
