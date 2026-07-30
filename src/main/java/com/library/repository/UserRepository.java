package com.library.repository;

import com.library.entity.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

/**
 * User repository for database operations on User entity.
 */
@Repository
public interface UserRepository extends JpaRepository<User, Long> {

    /**
     * Find user by username.
     */
    Optional<User> findByUsername(String username);

    /**
     * Find user by email.
     */
    Optional<User> findByEmail(String email);

    /**
     * Check if username exists.
     */
    boolean existsByUsername(String username);

    /**
     * Check if email exists.
     */
    boolean existsByEmail(String email);

    /**
     * Search users by username/email with optional status filtering.
     */
    @Query("""
        SELECT u FROM User u
        WHERE (:search IS NULL OR :search = ''
            OR LOWER(u.username) LIKE LOWER(CONCAT('%', :search, '%'))
            OR LOWER(u.email) LIKE LOWER(CONCAT('%', :search, '%')))
          AND (:status IS NULL OR u.status = :status)
        """)
    Page<User> searchUsers(
        @Param("search") String search,
        @Param("status") User.UserStatus status,
        Pageable pageable
    );

}
