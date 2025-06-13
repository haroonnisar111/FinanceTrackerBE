package com.budgetbuddy.personal_finance_tracker.controller;
import com.budgetbuddy.personal_finance_tracker.dto.ApiResponse;
import com.budgetbuddy.personal_finance_tracker.dto.BudgetRequest;
import com.budgetbuddy.personal_finance_tracker.dto.BudgetResponse;
import com.budgetbuddy.personal_finance_tracker.dto.BudgetStatsResponse;
import com.budgetbuddy.personal_finance_tracker.entity.Budget;
import com.budgetbuddy.personal_finance_tracker.entity.Budget.BudgetPeriod;
import com.budgetbuddy.personal_finance_tracker.entity.Category;
import com.budgetbuddy.personal_finance_tracker.service.BudgetService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/budgets")
@RequiredArgsConstructor
@Slf4j
@CrossOrigin(origins = "*")
public class BudgetController {

    private final BudgetService budgetService;

    @PostMapping
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<ApiResponse<BudgetResponse>> createBudget(@Valid @RequestBody BudgetRequest budgetRequest) {
        try {
            Budget budget = mapToEntity(budgetRequest);
            Budget createdBudget = budgetService.createBudget(budget);
            BudgetResponse response = mapToResponse(createdBudget);

            return ResponseEntity.status(HttpStatus.CREATED)
                    .body(ApiResponse.success("Budget created successfully", response));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest()
                    .body(ApiResponse.error(e.getMessage()));
        } catch (Exception e) {
            log.error("Error creating budget", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.error("Failed to create budget"));
        }
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<ApiResponse<BudgetResponse>> getBudgetById(@PathVariable Long id) {
        return budgetService.findById(id)
                .map(budget -> ResponseEntity.ok(ApiResponse.success("Budget found", mapToResponse(budget))))
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/active")
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<ApiResponse<List<BudgetResponse>>> getActiveBudgets() {
        List<Budget> budgets = budgetService.findAllActiveBudgets();
        List<BudgetResponse> responses = budgets.stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());

        return ResponseEntity.ok(ApiResponse.success("Active budgets retrieved", responses));
    }

    @GetMapping("/category/{categoryId}")
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<ApiResponse<List<BudgetResponse>>> getBudgetsByCategory(@PathVariable Long categoryId) {
        try {
            List<Budget> budgets = budgetService.findBudgetsByCategory(categoryId);
            List<BudgetResponse> responses = budgets.stream()
                    .map(this::mapToResponse)
                    .collect(Collectors.toList());

            return ResponseEntity.ok(ApiResponse.success("Budgets by category retrieved", responses));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest()
                    .body(ApiResponse.error(e.getMessage()));
        }
    }

    @GetMapping("/period/{period}")
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<ApiResponse<List<BudgetResponse>>> getBudgetsByPeriod(@PathVariable BudgetPeriod period) {
        List<Budget> budgets = budgetService.findBudgetsByPeriod(period);
        List<BudgetResponse> responses = budgets.stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());

        return ResponseEntity.ok(ApiResponse.success("Budgets by period retrieved", responses));
    }

    @GetMapping("/category/{categoryId}/active")
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<ApiResponse<BudgetResponse>> getActiveBudgetByCategory(@PathVariable Long categoryId) {
        try {
            return budgetService.findActiveBudgetByCategory(categoryId)
                    .map(budget -> ResponseEntity.ok(ApiResponse.success("Active budget found", mapToResponse(budget))))
                    .orElse(ResponseEntity.notFound().build());
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest()
                    .body(ApiResponse.error(e.getMessage()));
        }
    }

    @GetMapping("/exceeded")
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<ApiResponse<List<BudgetResponse>>> getExceededBudgets() {
        List<Budget> budgets = budgetService.findExceededBudgets();
        List<BudgetResponse> responses = budgets.stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());

        return ResponseEntity.ok(ApiResponse.success("Exceeded budgets retrieved", responses));
    }

    @GetMapping("/near-limit")
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<ApiResponse<List<BudgetResponse>>> getBudgetsNearLimit(
            @RequestParam(defaultValue = "80.0") double thresholdPercentage) {

        List<Budget> budgets = budgetService.findBudgetsNearLimit(thresholdPercentage);
        List<BudgetResponse> responses = budgets.stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());

        return ResponseEntity.ok(ApiResponse.success("Budgets near limit retrieved", responses));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<ApiResponse<BudgetResponse>> updateBudget(
            @PathVariable Long id,
            @Valid @RequestBody BudgetRequest budgetRequest) {
        try {
            Budget updatedBudget = mapToEntity(budgetRequest);
            Budget budget = budgetService.updateBudget(id, updatedBudget);
            BudgetResponse response = mapToResponse(budget);

            return ResponseEntity.ok(ApiResponse.success("Budget updated successfully", response));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest()
                    .body(ApiResponse.error(e.getMessage()));
        } catch (Exception e) {
            log.error("Error updating budget", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.error("Failed to update budget"));
        }
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<ApiResponse<Void>> deleteBudget(@PathVariable Long id) {
        try {
            budgetService.deleteBudget(id);
            return ResponseEntity.ok(ApiResponse.success("Budget deleted successfully", null));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest()
                    .body(ApiResponse.error(e.getMessage()));
        } catch (Exception e) {
            log.error("Error deleting budget", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.error("Failed to delete budget"));
        }
    }

    @PutMapping("/{id}/deactivate")
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<ApiResponse<Void>> deactivateBudget(@PathVariable Long id) {
        try {
            budgetService.deactivateBudget(id);
            return ResponseEntity.ok(ApiResponse.success("Budget deactivated successfully", null));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest()
                    .body(ApiResponse.error(e.getMessage()));
        } catch (Exception e) {
            log.error("Error deactivating budget", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.error("Failed to deactivate budget"));
        }
    }

    @PutMapping("/recalculate-all")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<Void>> recalculateAllBudgetSpending() {
        try {
            budgetService.recalculateAllBudgetSpending();
            return ResponseEntity.ok(ApiResponse.success("All budget spending recalculated successfully", null));
        } catch (Exception e) {
            log.error("Error recalculating budget spending", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.error("Failed to recalculate budget spending"));
        }
    }

    @GetMapping("/analytics/stats")
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<ApiResponse<BudgetStatsResponse>> getBudgetStats() {
        BigDecimal totalBudgetAmount = budgetService.getTotalActiveBudgetAmount();
        BigDecimal totalSpentAmount = budgetService.getTotalActiveSpentAmount();
        long activeBudgetCount = budgetService.getActiveBudgetCount();

        BudgetStatsResponse stats = BudgetStatsResponse.builder()
                .totalBudgetAmount(totalBudgetAmount)
                .totalSpentAmount(totalSpentAmount)
                .remainingAmount(totalBudgetAmount.subtract(totalSpentAmount))
                .activeBudgetCount(activeBudgetCount)
                .utilizationPercentage(totalBudgetAmount.compareTo(BigDecimal.ZERO) > 0 ?
                        totalSpentAmount.divide(totalBudgetAmount, 4, BigDecimal.ROUND_HALF_UP).multiply(BigDecimal.valueOf(100)) :
                        BigDecimal.ZERO)
                .build();

        return ResponseEntity.ok(ApiResponse.success("Budget statistics retrieved", stats));
    }

    @GetMapping("/analytics/utilization")
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<ApiResponse<List<Object[]>>> getBudgetUtilizationStats() {
        List<Object[]> stats = budgetService.getBudgetUtilizationStats();
        return ResponseEntity.ok(ApiResponse.success("Budget utilization statistics retrieved", stats));
    }

    private Budget mapToEntity(BudgetRequest request) {
        Budget budget = new Budget();
        budget.setName(request.getName());
        budget.setBudgetAmount(request.getBudgetAmount());
        budget.setStartDate(request.getStartDate());
        budget.setEndDate(request.getEndDate());
        budget.setPeriod(request.getPeriod());

        // Set category
        Category category = new Category();
        category.setId(request.getCategoryId());
        budget.setCategory(category);

        return budget;
    }

    private BudgetResponse mapToResponse(Budget budget) {
        BigDecimal utilizationPercentage = BigDecimal.ZERO;
        if (budget.getBudgetAmount().compareTo(BigDecimal.ZERO) > 0) {
            utilizationPercentage = budget.getSpentAmount()
                    .divide(budget.getBudgetAmount(), 4, BigDecimal.ROUND_HALF_UP)
                    .multiply(BigDecimal.valueOf(100));
        }

        return BudgetResponse.builder()
                .id(budget.getId())
                .name(budget.getName())
                .budgetAmount(budget.getBudgetAmount())
                .spentAmount(budget.getSpentAmount())
                .remainingAmount(budget.getBudgetAmount().subtract(budget.getSpentAmount()))
                .utilizationPercentage(utilizationPercentage)
                .startDate(budget.getStartDate())
                .endDate(budget.getEndDate())
                .period(budget.getPeriod())
                .isActive(budget.getIsActive())
                .categoryId(budget.getCategory().getId())
                .categoryName(budget.getCategory().getName())
                .createdAt(budget.getCreatedAt())
                .updatedAt(budget.getUpdatedAt())
                .build();
    }
}