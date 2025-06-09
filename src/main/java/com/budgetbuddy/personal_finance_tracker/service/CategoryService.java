package com.budgetbuddy.personal_finance_tracker.service;

import com.budgetbuddy.personal_finance_tracker.entity.Category;
import com.budgetbuddy.personal_finance_tracker.repository.CategoryRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class CategoryService {

    private final CategoryRepository categoryRepository;

    public CategoryService(CategoryRepository categoryRepository) {
        this.categoryRepository = categoryRepository;
    }

    public Category createCategory(Category category) {
        log.info("Creating new category: {}", category.getName());
        if (category == null || category.getName() == null) {
            throw new IllegalArgumentException("Category or category name cannot be null");
        }

        if (categoryRepository.existsByName(category.getName())) {
            throw new IllegalArgumentException("Category name already exists");
        }

        return categoryRepository.save(category);
    }

    @Transactional(readOnly = true)
    public Optional<Category> findById(Long id) {
        return categoryRepository.findById(id);
    }

    @Transactional(readOnly = true)
    public Optional<Category> findByName(String name) {
        return categoryRepository.findByName(name);
    }

    @Transactional(readOnly = true)
    public List<Category> findAllActiveCategories() {
        return categoryRepository.findByIsActiveTrueOrderByNameAsc();
    }

    @Transactional(readOnly = true)
    public List<Category> findAllCategories() {
        return categoryRepository.findByIsActiveOrderByNameAsc(null);
    }

    @Transactional(readOnly = true)
    public List<Category> searchCategories(String name) {
        return categoryRepository.findActiveCategoriesByNameContaining(name);
    }

    @Transactional(readOnly = true)
    public List<Category> findCategoriesByUsage() {
        return categoryRepository.findCategoriesOrderByTransactionCount();
    }

    @Transactional(readOnly = true)
    public List<Category> findUnusedCategories() {
        return categoryRepository.findUnusedActiveCategories();
    }

    public Category updateCategory(Long id, Category updatedCategory) {
        Category existingCategory = categoryRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Category not found"));

        if (!existingCategory.getName().equals(updatedCategory.getName()) &&
                categoryRepository.existsByName(updatedCategory.getName())) {
            throw new IllegalArgumentException("Category name already exists");
        }

        existingCategory.setName(updatedCategory.getName());
        existingCategory.setDescription(updatedCategory.getDescription());
        existingCategory.setColor(updatedCategory.getColor());

        return categoryRepository.save(existingCategory);
    }

    public void deactivateCategory(Long id) {
        Category category = categoryRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Category not found"));

        category.setIsActive(false);
        categoryRepository.save(category);
    }

    public void deleteCategory(Long id) {
        Category category = categoryRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Category not found"));

        if (!category.getTransactions().isEmpty()) {
            throw new IllegalStateException("Cannot delete category with existing transactions");
        }

        categoryRepository.delete(category);
    }

    @Transactional(readOnly = true)
    public long getActiveCategoryCount() {
        return categoryRepository.countActiveCategories();
    }
}