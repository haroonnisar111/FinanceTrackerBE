package com.budgetbuddy.personal_finance_tracker.mapper;
import com.budgetbuddy.personal_finance_tracker.dto.BudgetRequest;
import com.budgetbuddy.personal_finance_tracker.dto.BudgetResponse;
import com.budgetbuddy.personal_finance_tracker.entity.Budget;
import com.budgetbuddy.personal_finance_tracker.entity.Category;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;

@Component
public class BudgetMapper {

    public Budget toEntity(BudgetRequest request) {
        if (request == null) {
            return null;
        }

        Budget budget = new Budget();
        budget.setName(request.getName());
        budget.setBudgetAmount(request.getBudgetAmount());
        budget.setStartDate(request.getStartDate());
        budget.setEndDate(request.getEndDate());
        budget.setPeriod(request.getPeriod());

        // Set category
        if (request.getCategoryId() != null) {
            Category category = new Category();
            category.setId(request.getCategoryId());
            budget.setCategory(category);
        }

        return budget;
    }

    public BudgetResponse toResponse(Budget budget) {
        if (budget == null) {
            return null;
        }

        BigDecimal utilizationPercentage = BigDecimal.ZERO;
        if (budget.getBudgetAmount() != null &&
                budget.getBudgetAmount().compareTo(BigDecimal.ZERO) > 0) {
            BigDecimal spentAmount = budget.getSpentAmount() != null ?
                    budget.getSpentAmount() : BigDecimal.ZERO;
            utilizationPercentage = spentAmount
                    .divide(budget.getBudgetAmount(), 4, BigDecimal.ROUND_HALF_UP)
                    .multiply(BigDecimal.valueOf(100));
        }

        BigDecimal spentAmount = budget.getSpentAmount() != null ?
                budget.getSpentAmount() : BigDecimal.ZERO;
        BigDecimal budgetAmount = budget.getBudgetAmount() != null ?
                budget.getBudgetAmount() : BigDecimal.ZERO;

        return BudgetResponse.builder()
                .id(budget.getId())
                .name(budget.getName())
                .budgetAmount(budgetAmount)
                .spentAmount(spentAmount)
                .remainingAmount(budgetAmount.subtract(spentAmount))
                .utilizationPercentage(utilizationPercentage)
                .startDate(budget.getStartDate())
                .endDate(budget.getEndDate())
                .period(budget.getPeriod())
                .isActive(budget.getIsActive())
                .categoryId(budget.getCategory() != null ?
                        budget.getCategory().getId() : null)
                .categoryName(budget.getCategory() != null ?
                        budget.getCategory().getName() : null)
                .createdAt(budget.getCreatedAt())
                .updatedAt(budget.getUpdatedAt())
                .build();
    }

    public Budget updateEntity(BudgetRequest request) {
        Budget existingBudget = null;
        if (request == null || existingBudget == null) {
            return existingBudget;
        }

        if (request.getName() != null) {
            existingBudget.setName(request.getName());
        }
        if (request.getBudgetAmount() != null) {
            existingBudget.setBudgetAmount(request.getBudgetAmount());
        }
        if (request.getStartDate() != null) {
            existingBudget.setStartDate(request.getStartDate());
        }
        if (request.getEndDate() != null) {
            existingBudget.setEndDate(request.getEndDate());
        }
        if (request.getPeriod() != null) {
            existingBudget.setPeriod(request.getPeriod());
        }
        if (request.getCategoryId() != null) {
            Category category = new Category();
            category.setId(request.getCategoryId());
            existingBudget.setCategory(category);
        }

        return existingBudget;
    }
}
