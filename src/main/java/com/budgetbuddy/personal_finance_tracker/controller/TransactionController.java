package com.budgetbuddy.personal_finance_tracker.controller;
import com.budgetbuddy.personal_finance_tracker.dto.ApiResponse;
import com.budgetbuddy.personal_finance_tracker.dto.TransactionRequest;
import com.budgetbuddy.personal_finance_tracker.dto.TransactionResponse;
import com.budgetbuddy.personal_finance_tracker.dto.TransactionSummaryResponse;
import com.budgetbuddy.personal_finance_tracker.entity.Transaction;
import com.budgetbuddy.personal_finance_tracker.entity.Transaction.TransactionType;
import com.budgetbuddy.personal_finance_tracker.entity.Category;
import com.budgetbuddy.personal_finance_tracker.service.TransactionService;
import com.budgetbuddy.personal_finance_tracker.service.CategoryService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/transactions")
@RequiredArgsConstructor
@Slf4j
@CrossOrigin(origins = "*")
public class TransactionController {

    private final TransactionService transactionService;
    private final CategoryService categoryService;

    @PostMapping
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<ApiResponse<TransactionResponse>> createTransaction(@Valid @RequestBody TransactionRequest transactionRequest) {
        try {
            Transaction transaction = mapToEntity(transactionRequest);
            Transaction createdTransaction = transactionService.createTransaction(transaction);
            TransactionResponse response = mapToResponse(createdTransaction);

            return ResponseEntity.status(HttpStatus.CREATED)
                    .body(ApiResponse.success("Transaction created successfully", response));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest()
                    .body(ApiResponse.error(e.getMessage()));
        } catch (Exception e) {
            log.error("Error creating transaction", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.error("Failed to create transaction"));
        }
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<ApiResponse<TransactionResponse>> getTransactionById(@PathVariable Long id) {
        return transactionService.findById(id)
                .map(transaction -> ResponseEntity.ok(ApiResponse.success("Transaction found", mapToResponse(transaction))))
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<ApiResponse<Page<TransactionResponse>>> getAllTransactions(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {

        Pageable pageable = PageRequest.of(page, size);
        Page<Transaction> transactions = transactionService.findAllTransactions(pageable);
        Page<TransactionResponse> responses = transactions.map(this::mapToResponse);

        return ResponseEntity.ok(ApiResponse.success("Transactions retrieved", responses));
    }

    @GetMapping("/type/{type}")
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<ApiResponse<List<TransactionResponse>>> getTransactionsByType(@PathVariable TransactionType type) {
        List<Transaction> transactions = transactionService.findTransactionsByType(type);
        List<TransactionResponse> responses = transactions.stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());

        return ResponseEntity.ok(ApiResponse.success("Transactions by type retrieved", responses));
    }

    @GetMapping("/category/{categoryId}")
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<ApiResponse<List<TransactionResponse>>> getTransactionsByCategory(@PathVariable Long categoryId) {
        try {
            List<Transaction> transactions = transactionService.findTransactionsByCategory(categoryId);
            List<TransactionResponse> responses = transactions.stream()
                    .map(this::mapToResponse)
                    .collect(Collectors.toList());

            return ResponseEntity.ok(ApiResponse.success("Transactions by category retrieved", responses));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest()
                    .body(ApiResponse.error(e.getMessage()));
        }
    }

    @GetMapping("/date-range")
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<ApiResponse<List<TransactionResponse>>> getTransactionsByDateRange(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate) {

        List<Transaction> transactions = transactionService.findTransactionsByDateRange(startDate, endDate);
        List<TransactionResponse> responses = transactions.stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());

        return ResponseEntity.ok(ApiResponse.success("Transactions by date range retrieved", responses));
    }

    @GetMapping("/date-range-type")
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<ApiResponse<List<TransactionResponse>>> getTransactionsByDateRangeAndType(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate,
            @RequestParam TransactionType type) {

        List<Transaction> transactions = transactionService.findTransactionsByDateRangeAndType(startDate, endDate, type);
        List<TransactionResponse> responses = transactions.stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());

        return ResponseEntity.ok(ApiResponse.success("Transactions by date range and type retrieved", responses));
    }

    @GetMapping("/search")
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<ApiResponse<List<TransactionResponse>>> searchTransactions(@RequestParam String keyword) {
        List<Transaction> transactions = transactionService.searchTransactions(keyword);
        List<TransactionResponse> responses = transactions.stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());

        return ResponseEntity.ok(ApiResponse.success("Transactions found", responses));
    }

    @GetMapping("/recent")
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<ApiResponse<List<TransactionResponse>>> getRecentTransactions(
            @RequestParam(defaultValue = "10") int limit) {

        List<Transaction> transactions = transactionService.findRecentTransactions(limit);
        List<TransactionResponse> responses = transactions.stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());

        return ResponseEntity.ok(ApiResponse.success("Recent transactions retrieved", responses));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<ApiResponse<TransactionResponse>> updateTransaction(
            @PathVariable Long id,
            @Valid @RequestBody TransactionRequest transactionRequest) {
        try {
            Transaction updatedTransaction = mapToEntity(transactionRequest);
            Transaction transaction = transactionService.updateTransaction(id, updatedTransaction);
            TransactionResponse response = mapToResponse(transaction);

            return ResponseEntity.ok(ApiResponse.success("Transaction updated successfully", response));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest()
                    .body(ApiResponse.error(e.getMessage()));
        } catch (Exception e) {
            log.error("Error updating transaction", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.error("Failed to update transaction"));
        }
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<ApiResponse<Void>> deleteTransaction(@PathVariable Long id) {
        try {
            transactionService.deleteTransaction(id);
            return ResponseEntity.ok(ApiResponse.success("Transaction deleted successfully", null));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest()
                    .body(ApiResponse.error(e.getMessage()));
        } catch (Exception e) {
            log.error("Error deleting transaction", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.error("Failed to delete transaction"));
        }
    }

    // Analytics endpoints
    @GetMapping("/analytics/summary")
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<ApiResponse<TransactionSummaryResponse>> getTransactionSummary(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate) {

        BigDecimal totalIncome = transactionService.getTotalIncomeByDateRange(startDate, endDate);
        BigDecimal totalExpense = transactionService.getTotalExpenseByDateRange(startDate, endDate);
        BigDecimal netAmount = transactionService.getNetAmountByDateRange(startDate, endDate);

        TransactionSummaryResponse summary = TransactionSummaryResponse.builder()
                .totalIncome(totalIncome)
                .totalExpense(totalExpense)
                .netAmount(netAmount)
                .startDate(startDate)
                .endDate(endDate)
                .build();

        return ResponseEntity.ok(ApiResponse.success("Transaction summary retrieved", summary));
    }

    @GetMapping("/analytics/monthly/{type}")
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<ApiResponse<List<Object[]>>> getMonthlySummary(@PathVariable TransactionType type) {
        List<Object[]> summary = transactionService.getMonthlySummary(type);
        return ResponseEntity.ok(ApiResponse.success("Monthly summary retrieved", summary));
    }

    @GetMapping("/analytics/category/{type}")
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<ApiResponse<List<Object[]>>> getCategorySummary(
            @PathVariable TransactionType type,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate) {

        List<Object[]> summary = transactionService.getCategorySummary(type, startDate, endDate);
        return ResponseEntity.ok(ApiResponse.success("Category summary retrieved", summary));
    }

    private Transaction mapToEntity(TransactionRequest request) {
        Transaction transaction = new Transaction();
        transaction.setDescription(request.getDescription());
        transaction.setAmount(request.getAmount());
        transaction.setTransactionDate(request.getTransactionDate());
        transaction.setType(request.getType());
        transaction.setNotes(request.getNotes());

        // Set category
        Category category = new Category();
        category.setId(request.getCategoryId());
        transaction.setCategory(category);

        return transaction;
    }

    private TransactionResponse mapToResponse(Transaction transaction) {
        return TransactionResponse.builder()
                .id(transaction.getId())
                .description(transaction.getDescription())
                .amount(transaction.getAmount())
                .transactionDate(transaction.getTransactionDate())
                .type(transaction.getType())
                .notes(transaction.getNotes())
                .categoryId(transaction.getCategory().getId())
                .categoryName(transaction.getCategory().getName())
                .createdAt(transaction.getCreatedAt())
                .updatedAt(transaction.getUpdatedAt())
                .build();
    }
}
