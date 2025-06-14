package com.budgetbuddy.personal_finance_tracker.dto;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class DashboardStatsResponse {
    private Long activeCategoryCount;
    private Long activeBudgetCount;
    private BigDecimal monthlyIncome;
    private BigDecimal monthlyExpense;
    private BigDecimal monthlyNet;
    private BigDecimal totalBudgetAmount;
    private BigDecimal totalSpentAmount;
    private BigDecimal budgetUtilizationPercentage;
    private String currentMonth;
}
