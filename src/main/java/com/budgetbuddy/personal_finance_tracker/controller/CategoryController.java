package com.budgetbuddy.personal_finance_tracker.controller;
import com.budgetbuddy.personal_finance_tracker.dto.ApiResponse;
import com.budgetbuddy.personal_finance_tracker.dto.CategoryRequest;
import com.budgetbuddy.personal_finance_tracker.dto.CategoryResponse;
import com.budgetbuddy.personal_finance_tracker.entity.Category;
import com.budgetbuddy.personal_finance_tracker.service.CategoryService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/categories")
@RequiredArgsConstructor
@Slf4j
@CrossOrigin(origins = "*")
public class CategoryController {

    private final CategoryService categoryService;

    @PostMapping
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<ApiResponse<CategoryResponse>> createCategory(@Valid @RequestBody CategoryRequest categoryRequest) {
        try {
            Category category = mapToEntity(categoryRequest);
            Category createdCategory = categoryService.createCategory(category);
            CategoryResponse response = mapToResponse(createdCategory);

            return ResponseEntity.status(HttpStatus.CREATED)
                    .body(ApiResponse.success("Category created successfully", response));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest()
                    .body(ApiResponse.error(e.getMessage()));
        } catch (Exception e) {
            log.error("Error creating category", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.error("Failed to create category"));
        }
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<ApiResponse<CategoryResponse>> getCategoryById(@PathVariable Long id) {
        return categoryService.findById(id)
                .map(category -> ResponseEntity.ok(ApiResponse.success("Category found", mapToResponse(category))))
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/name/{name}")
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<ApiResponse<CategoryResponse>> getCategoryByName(@PathVariable String name) {
        return categoryService.findByName(name)
                .map(category -> ResponseEntity.ok(ApiResponse.success("Category found", mapToResponse(category))))
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/active")
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<ApiResponse<List<CategoryResponse>>> getActiveCategories() {
        List<Category> categories = categoryService.findAllActiveCategories();
        List<CategoryResponse> responses = categories.stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());

        return ResponseEntity.ok(ApiResponse.success("Active categories retrieved", responses));
    }

    @GetMapping
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<ApiResponse<List<CategoryResponse>>> getAllCategories() {
        List<Category> categories = categoryService.findAllCategories();
        List<CategoryResponse> responses = categories.stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());

        return ResponseEntity.ok(ApiResponse.success("All categories retrieved", responses));
    }

    @GetMapping("/search")
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<ApiResponse<List<CategoryResponse>>> searchCategories(@RequestParam String name) {
        List<Category> categories = categoryService.searchCategories(name);
        List<CategoryResponse> responses = categories.stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());

        return ResponseEntity.ok(ApiResponse.success("Categories found", responses));
    }

    @GetMapping("/by-usage")
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<ApiResponse<List<CategoryResponse>>> getCategoriesByUsage() {
        List<Category> categories = categoryService.findCategoriesByUsage();
        List<CategoryResponse> responses = categories.stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());

        return ResponseEntity.ok(ApiResponse.success("Categories by usage retrieved", responses));
    }

    @GetMapping("/unused")
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<ApiResponse<List<CategoryResponse>>> getUnusedCategories() {
        List<Category> categories = categoryService.findUnusedCategories();
        List<CategoryResponse> responses = categories.stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());

        return ResponseEntity.ok(ApiResponse.success("Unused categories retrieved", responses));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<ApiResponse<CategoryResponse>> updateCategory(
            @PathVariable Long id,
            @Valid @RequestBody CategoryRequest categoryRequest) {
        try {
            Category updatedCategory = mapToEntity(categoryRequest);
            Category category = categoryService.updateCategory(id, updatedCategory);
            CategoryResponse response = mapToResponse(category);

            return ResponseEntity.ok(ApiResponse.success("Category updated successfully", response));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest()
                    .body(ApiResponse.error(e.getMessage()));
        } catch (Exception e) {
            log.error("Error updating category", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.error("Failed to update category"));
        }
    }

    @PutMapping("/{id}/deactivate")
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<ApiResponse<Void>> deactivateCategory(@PathVariable Long id) {
        try {
            categoryService.deactivateCategory(id);
            return ResponseEntity.ok(ApiResponse.success("Category deactivated successfully", null));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest()
                    .body(ApiResponse.error(e.getMessage()));
        } catch (Exception e) {
            log.error("Error deactivating category", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.error("Failed to deactivate category"));
        }
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<ApiResponse<Void>> deleteCategory(@PathVariable Long id) {
        try {
            categoryService.deleteCategory(id);
            return ResponseEntity.ok(ApiResponse.success("Category deleted successfully", null));
        } catch (IllegalArgumentException | IllegalStateException e) {
            return ResponseEntity.badRequest()
                    .body(ApiResponse.error(e.getMessage()));
        } catch (Exception e) {
            log.error("Error deleting category", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.error("Failed to delete category"));
        }
    }

    @GetMapping("/stats/count")
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<ApiResponse<Long>> getActiveCategoryCount() {
        long count = categoryService.getActiveCategoryCount();
        return ResponseEntity.ok(ApiResponse.success("Active category count retrieved", count));
    }

    private Category mapToEntity(CategoryRequest request) {
        Category category = new Category();
        category.setName(request.getName());
        category.setDescription(request.getDescription());
        category.setColor(request.getColor());
        return category;
    }

    private CategoryResponse mapToResponse(Category category) {
        return CategoryResponse.builder()
                .id(category.getId())
                .name(category.getName())
                .description(category.getDescription())
                .color(category.getColor())
                .isActive(category.getIsActive())
                .createdAt(category.getCreatedAt())
                .transactionCount(category.getTransactions() != null ? category.getTransactions().size() : 0)
                .build();
    }
}
