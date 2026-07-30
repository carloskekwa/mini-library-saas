package com.library.service;
import com.library.dto.AuditLogDTO;
import com.library.entity.AuditLog;
import com.library.entity.User;
import com.library.repository.AuditLogRepository;
import com.library.repository.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.time.LocalDateTime;
import java.util.Optional;

/**
 * Service for audit logging (Phase 13).
 */
@Service
@Transactional
public class AuditService {
    private static final Logger logger = LoggerFactory.getLogger(AuditService.class);
    private final AuditLogRepository auditLogRepository;
    private final UserRepository userRepository;

    public AuditService(AuditLogRepository auditLogRepository, UserRepository userRepository) {
        this.auditLogRepository = auditLogRepository;
        this.userRepository = userRepository;
    }

    public void logAction(Long userId, String action, String entityType, Long entityId, String details, String ipAddress) {
        logger.info("Logging action: action={}, entityType={}, entityId={}", action, entityType, entityId);
        
        Optional<User> user = userRepository.findById(userId);
        AuditLog auditLog = new AuditLog(
            user.orElse(null),
            action,
            entityType,
            entityId,
            details,
            ipAddress
        );
        auditLogRepository.save(auditLog);
    }

    @Transactional(readOnly = true)
    public Page<AuditLogDTO> getUserAuditLogs(Long userId, Pageable pageable) {
        return auditLogRepository.findByUserId(userId, pageable).map(AuditLogDTO::from);
    }

    @Transactional(readOnly = true)
    public Page<AuditLogDTO> getAuditLogsByDateRange(LocalDateTime start, LocalDateTime end, Pageable pageable) {
        return auditLogRepository.findByTimestampBetween(start, end, pageable).map(AuditLogDTO::from);
    }
}
