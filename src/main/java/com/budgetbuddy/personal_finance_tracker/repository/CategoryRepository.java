package com.budgetbuddy.personal_finance_tracker.repository;
import com.budgetbuddy.personal_finance_tracker.entity.Category;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface CategoryRepository extends JpaRepository<Category, Long> {

    Optional<Category> findByName(String name);

    List<Category> findByIsActiveTrueOrderByNameAsc();

    List<Category> findByIsActiveOrderByNameAsc(Boolean isActive);

    boolean existsByName(String name);

    @Query("SELECT c FROM Category c WHERE c.name LIKE %:name% AND c.isActive = true")
    List<Category> findActiveCategoriesByNameContaining(@Param("name") String name);

    @Query("SELECT c FROM Category c LEFT JOIN c.transactions t GROUP BY c ORDER BY COUNT(t) DESC")
    List<Category> findCategoriesOrderByTransactionCount();

    @Query("SELECT COUNT(c) FROM Category c WHERE c.isActive = true")
    long countActiveCategories();

    @Query("SELECT c FROM Category c WHERE c.isActive = true AND SIZE(c.transactions) = 0")
    List<Category> findUnusedActiveCategories();
}
