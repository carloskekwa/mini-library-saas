package com.library.repository;
import com.library.entity.ApiRateLimit;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.Optional;

@Repository
public interface ApiRateLimitRepository extends JpaRepository<ApiRateLimit, Long> {
    Optional<ApiRateLimit> findByUserIdAndEndpoint(Long userId, String endpoint);
}
