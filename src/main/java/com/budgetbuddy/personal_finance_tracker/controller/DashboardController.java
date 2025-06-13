package com.budgetbuddy.personal_finance_tracker.controller;
import com.budgetbuddy.personal_finance_tracker.dto.ApiResponse;
import com.budgetbuddy.personal_finance_tracker.dto.DashboardStatsResponse;
import com.budgetbuddy.personal_finance_tracker.entity.Transaction.TransactionType;
import com.budgetbuddy.personal_finance_tracker.service.UserService;
import com.budgetbuddy.personal_finance_tracker.service.CategoryService;
import com.budgetbuddy.personal_finance_tracker.service.TransactionService;
import com.budgetbuddy.personal_finance_tracker.service.BudgetService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.YearMonth;

@RestController
@RequestMapping("/api/dashboard")
@RequiredArgsConstructor
@Slf4j
@CrossOrigin(origins = "*")
public class DashboardController {

    private final UserService userService;
    private final CategoryService categoryService;
    private final TransactionService transactionService;
    private final BudgetService budgetService;

    @GetMapping("/stats")
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<ApiResponse<DashboardStatsResponse>> getDashboardStats() {
        try {
            // Get current month date range
            YearMonth currentMonth = YearMonth.now();
            LocalDate startOfMonth = currentMonth.atDay(1);
            LocalDate endOfMonth = currentMonth.atEndOfMonth();

            // Calculate statistics
            long activeCategoryCount = categoryService.getActiveCategoryCount();
            long activeBudgetCount = budgetService.getActiveBudgetCount();

            BigDecimal monthlyIncome = transactionService.getTotalIncomeByDateRange(startOfMonth, endOfMonth);
            BigDecimal monthlyExpense = transactionService.getTotalExpenseByDateRange(startOfMonth, endOfMonth);
            BigDecimal monthlyNet = monthlyIncome.subtract(monthlyExpense);

            BigDecimal totalBudgetAmount = budgetService.getTotalActiveBudgetAmount();
            BigDecimal totalSpentAmount = budgetService.getTotalActiveSpentAmount();

            DashboardStatsResponse stats = DashboardStatsResponse.builder()
                    .activeCategoryCount(activeCategoryCount)
                    .activeBudgetCount(activeBudgetCount)
                    .monthlyIncome(monthlyIncome)
                    .monthlyExpense(monthlyExpense)
                    .monthlyNet(monthlyNet)
                    .totalBudgetAmount(totalBudgetAmount)
                    .totalSpentAmount(totalSpentAmount)
                    .budgetUtilizationPercentage(totalBudgetAmount.compareTo(BigDecimal.ZERO) > 0 ?
                            totalSpentAmount.divide(totalBudgetAmount, 4, BigDecimal.ROUND_HALF_UP).multiply(BigDecimal.valueOf(100)) :
                            BigDecimal.ZERO)
                    .currentMonth(currentMonth.toString())
                    .build();

            return ResponseEntity.ok(ApiResponse.success("Dashboard statistics retrieved", stats));
        } catch (Exception e) {
            log.error("Error retrieving dashboard statistics", e);
            return ResponseEntity.status(500)
                    .body(ApiResponse.error("Failed to retrieve dashboard statistics"));
        }
    }

    @GetMapping("/stats/admin")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<Object>> getAdminDashboardStats() {
        try {
            long activeUserCount = userService.getActiveUserCount();
            long activeCategoryCount = categoryService.getActiveCategoryCount();
            long activeBudgetCount = budgetService.getActiveBudgetCount();

            // Get current month totals
            YearMonth currentMonth = YearMonth.now();
            LocalDate startOfMonth = currentMonth.atDay(1);
            LocalDate endOfMonth = currentMonth.atEndOfMonth();

            BigDecimal monthlyIncome = transactionService.getTotalIncomeByDateRange(startOfMonth, endOfMonth);
            BigDecimal monthlyExpense = transactionService.getTotalExpenseByDateRange(startOfMonth, endOfMonth);

            Object adminStats = new Object() {
                public final long activeUsers = activeUserCount;
                public final long activeCategories = activeCategoryCount;
                public final long activeBudgets = activeBudgetCount;
                public final BigDecimal totalMonthlyIncome = monthlyIncome;
                public final BigDecimal totalMonthlyExpense = monthlyExpense;
                public final String currentMonth = currentMonth.toString();
            };

            return ResponseEntity.ok(ApiResponse.success("Admin dashboard statistics retrieved", adminStats));
        } catch (Exception e) {
            log.error("Error retrieving admin dashboard statistics", e);
            return ResponseEntity.status(500)
                    .body(ApiResponse.error("Failed to retrieve admin dashboard statistics"));
        }
    }
}
