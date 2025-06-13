package com.budgetbuddy.personal_finance_tracker.dto;

import com.budgetbuddy.personal_finance_tracker.entity.Transaction.TransactionType;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import com.fasterxml.jackson.annotation.JsonFormat;

import java.math.BigDecimal;
import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TransactionRequest {
    @NotBlank(message = "Description is required")
    @Size(max = 255, message = "Description must not exceed 255 characters")
    private String description;

    @NotNull(message = "Amount is required")
    @DecimalMin(value = "0.01", message = "Amount must be greater than 0")
    private BigDecimal amount;

    @NotNull(message = "Transaction date is required")
    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate transactionDate;

    @NotNull(message = "Transaction type is required")
    private TransactionType type;

    @Size(max = 500, message = "Notes must not exceed 500 characters")
    private String notes;

    @NotNull(message = "Category ID is required")
    private Long categoryId;
}
