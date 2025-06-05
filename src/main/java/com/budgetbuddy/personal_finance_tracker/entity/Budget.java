package com.budgetbuddy.personal_finance_tracker.entity;
import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "budgets")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@EqualsAndHashCode(exclude = {"category"})
@ToString(exclude = {"category"})
public class Budget {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "Budget name is required")
    @Size(max = 100, message = "Budget name must not exceed 100 characters")
    @Column(nullable = false, length = 100)
    private String name;

    @NotNull(message = "Budget amount is required")
    @DecimalMin(value = "0.01", message = "Budget amount must be greater than 0")
    @Digits(integer = 10, fraction = 2, message = "Invalid budget amount format")
    @Column(name = "budget_amount", nullable = false, precision = 12, scale = 2)
    private BigDecimal budgetAmount;

    @Builder.Default
    @Column(name = "spent_amount", nullable = false, precision = 12, scale = 2)
    private BigDecimal spentAmount = BigDecimal.ZERO;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "category_id", nullable = false)
    @NotNull(message = "Category is required")
    private Category category;

    @NotNull(message = "Start date is required")
    @Column(name = "start_date", nullable = false)
    private LocalDate startDate;

    @NotNull(message = "End date is required")
    @Column(name = "end_date", nullable = false)
    private LocalDate endDate;

    @NotNull(message = "Budget period is required")
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private BudgetPeriod period;

    @Builder.Default
    @Column(name = "is_active", nullable = false)
    private Boolean isActive = true;

    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    public enum BudgetPeriod {
        WEEKLY("Weekly"),
        MONTHLY("Monthly"),
        QUARTERLY("Quarterly"),
        YEARLY("Yearly");

        private final String displayName;

        BudgetPeriod(String displayName) {
            this.displayName = displayName;
        }

        public String getDisplayName() {
            return displayName;
        }
    }

    // Business logic methods
    public BigDecimal getRemainingAmount() {
        return budgetAmount.subtract(spentAmount);
    }

    public double getSpentPercentage() {
        if (budgetAmount.compareTo(BigDecimal.ZERO) == 0) {
            return 0.0;
        }
        return spentAmount.divide(budgetAmount, 4, BigDecimal.ROUND_HALF_UP)
                .multiply(BigDecimal.valueOf(100))
                .doubleValue();
    }

    public boolean isExceeded() {
        return spentAmount.compareTo(budgetAmount) > 0;
    }

    public boolean isActive() {
        LocalDate now = LocalDate.now();
        return Boolean.TRUE.equals(isActive) &&
                !now.isBefore(startDate) &&
                !now.isAfter(endDate);
    }
}

