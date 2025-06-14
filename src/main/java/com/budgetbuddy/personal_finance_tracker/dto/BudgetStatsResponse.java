package com.budgetbuddy.personal_finance_tracker.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import com.fasterxml.jackson.annotation.JsonProperty;
import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * Response DTO for budget statistics
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BudgetStatsResponse {

    @JsonProperty("total_budget_amount")
    private BigDecimal totalBudgetAmount;

    @JsonProperty("total_spent_amount")
    private BigDecimal totalSpentAmount;

    @JsonProperty("remaining_amount")
    private BigDecimal remainingAmount;

    @JsonProperty("active_budget_count")
    private Long activeBudgetCount;

    @JsonProperty("utilization_percentage")
    private BigDecimal utilizationPercentage;

    @JsonProperty("timestamp")
    @Builder.Default
    private LocalDateTime timestamp = LocalDateTime.now();
}

