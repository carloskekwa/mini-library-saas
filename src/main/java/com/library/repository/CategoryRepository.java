package com.library.repository;

import com.library.entity.Category;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

/**
 * Category repository for database operations on Category entity.
 */
@Repository
public interface CategoryRepository extends JpaRepository<Category, Long> {

    /**
     * Find category by name.
     */
    Optional<Category> findByName(String name);

    /**
     * Check if category name exists.
     */
    boolean existsByName(String name);

}
