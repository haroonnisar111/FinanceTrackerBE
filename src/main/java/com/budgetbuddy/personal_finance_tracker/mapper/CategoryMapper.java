package com.budgetbuddy.personal_finance_tracker.mapper;

import com.budgetbuddy.personal_finance_tracker.dto.CategoryRequest;
import com.budgetbuddy.personal_finance_tracker.dto.CategoryResponse;
import com.budgetbuddy.personal_finance_tracker.entity.Category;
import lombok.Data;
import org.springframework.stereotype.Component;

@Component
public class CategoryMapper {

    public Category toEntity(CategoryRequest request) {
        if (request == null) {
            return null;
        }

        Category category = new Category();
        category.setName(request.getName());
        category.setDescription(request.getDescription());
        category.setColor(request.getColor());
        return category;
    }

    public CategoryResponse toResponse(Category category) {
        if (category == null) {
            return null;
        }

        return CategoryResponse.builder()
                .id(category.getId())
                .name(category.getName())
                .description(category.getDescription())
                .color(category.getColor())
                .isActive(category.getIsActive())
                .createdAt(category.getCreatedAt())
                .transactionCount(category.getTransactions() != null ?
                        category.getTransactions().size() : 0)
                .build();
    }

    public Category updateEntity(Category existingCategory, CategoryRequest request) {
        if (request == null || existingCategory == null) {
            return existingCategory;
        }

        if (request.getName() != null) {
            existingCategory.setName(request.getName());
        }
        if (request.getDescription() != null) {
            existingCategory.setDescription(request.getDescription());
        }
        if (request.getColor() != null) {
            existingCategory.setColor(request.getColor());
        }

        return existingCategory;
    }
}

