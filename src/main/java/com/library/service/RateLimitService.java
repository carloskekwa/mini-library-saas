package com.library.service;
import com.library.entity.ApiRateLimit;
import com.library.entity.User;
import com.library.exception.ResourceNotFoundException;
import com.library.repository.ApiRateLimitRepository;
import com.library.repository.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.time.LocalDateTime;

/**
 * Service for API rate limiting (Phase 12).
 */
@Service
@Transactional
public class RateLimitService {
    private static final Logger logger = LoggerFactory.getLogger(RateLimitService.class);
    private final ApiRateLimitRepository rateLimitRepository;
    private final UserRepository userRepository;

    public RateLimitService(ApiRateLimitRepository rateLimitRepository, UserRepository userRepository) {
        this.rateLimitRepository = rateLimitRepository;
        this.userRepository = userRepository;
    }

    public boolean checkRateLimit(Long userId, String endpoint) {
        logger.info("Checking rate limit for user: {} on endpoint: {}", userId, endpoint);
        ApiRateLimit limit = rateLimitRepository.findByUserIdAndEndpoint(userId, endpoint)
            .orElse(new ApiRateLimit(userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found")), endpoint, 100));
        
        if (LocalDateTime.now().isAfter(limit.getResetAt())) {
            limit.setRequestCount(0);
            limit.setResetAt(LocalDateTime.now().plusHours(1));
        }
        
        if (limit.getRequestCount() >= limit.getLimit()) {
            return false;
        }
        
        limit.setRequestCount(limit.getRequestCount() + 1);
        rateLimitRepository.save(limit);
        return true;
    }
}
