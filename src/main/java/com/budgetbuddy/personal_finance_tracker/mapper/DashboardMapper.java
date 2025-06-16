package com.budgetbuddy.personal_finance_tracker.mapper;

import com.budgetbuddy.personal_finance_tracker.dto.DashboardStatsResponse;
import com.budgetbuddy.personal_finance_tracker.dto.TransactionSummaryResponse;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.time.LocalDate;

@Component
public class DashboardMapper {

    public TransactionSummaryResponse toTransactionSummaryResponse(
            BigDecimal totalIncome,
            BigDecimal totalExpense,
            BigDecimal netAmount,
            LocalDate startDate,
            LocalDate endDate) {

        return TransactionSummaryResponse.builder()
                .totalIncome(totalIncome != null ? totalIncome : BigDecimal.ZERO)
                .totalExpense(totalExpense != null ? totalExpense : BigDecimal.ZERO)
                .netAmount(netAmount != null ? netAmount : BigDecimal.ZERO)
                .startDate(startDate)
                .endDate(endDate)
                .build();
    }

    public DashboardStatsResponse toDashboardStatsResponse(
            long activeCategoryCount,
            long activeBudgetCount,
            BigDecimal monthlyIncome,
            BigDecimal monthlyExpense,
            BigDecimal totalBudgetAmount,
            BigDecimal totalSpentAmount,
            String currentMonth) {

        BigDecimal safeMonthlyIncome = monthlyIncome != null ? monthlyIncome : BigDecimal.ZERO;
        BigDecimal safeMonthlyExpense = monthlyExpense != null ? monthlyExpense : BigDecimal.ZERO;
        BigDecimal safeTotalBudgetAmount = totalBudgetAmount != null ? totalBudgetAmount : BigDecimal.ZERO;
        BigDecimal safeTotalSpentAmount = totalSpentAmount != null ? totalSpentAmount : BigDecimal.ZERO;

        BigDecimal budgetUtilizationPercentage = BigDecimal.ZERO;
        if (safeTotalBudgetAmount.compareTo(BigDecimal.ZERO) > 0) {
            budgetUtilizationPercentage = safeTotalSpentAmount
                    .divide(safeTotalBudgetAmount, 4, BigDecimal.ROUND_HALF_UP)
                    .multiply(BigDecimal.valueOf(100));
        }

        return DashboardStatsResponse.builder()
                .activeCategoryCount(activeCategoryCount)
                .activeBudgetCount(activeBudgetCount)
                .monthlyIncome(safeMonthlyIncome)
                .monthlyExpense(safeMonthlyExpense)
                .monthlyNet(safeMonthlyIncome.subtract(safeMonthlyExpense))
                .totalBudgetAmount(safeTotalBudgetAmount)
                .totalSpentAmount(safeTotalSpentAmount)
                .budgetUtilizationPercentage(budgetUtilizationPercentage)
                .currentMonth(currentMonth)
                .build();
    }
}
