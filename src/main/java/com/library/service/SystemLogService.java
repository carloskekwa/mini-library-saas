package com.library.service;
import com.library.dto.SystemLogDTO;
import com.library.entity.SystemLog;
import com.library.repository.SystemLogRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.time.LocalDateTime;

/**
 * Service for system logging (Phase 7).
 */
@Service
@Transactional
public class SystemLogService {
    private static final Logger logger = LoggerFactory.getLogger(SystemLogService.class);
    private final SystemLogRepository systemLogRepository;

    public SystemLogService(SystemLogRepository systemLogRepository) {
        this.systemLogRepository = systemLogRepository;
    }

    public void log(SystemLog.LogLevel level, String message, String source) {
        SystemLog log = new SystemLog(level, message, source);
        systemLogRepository.save(log);
    }

    @Transactional(readOnly = true)
    public Page<SystemLogDTO> getLogsByLevel(SystemLog.LogLevel level, Pageable pageable) {
        return systemLogRepository.findByLevel(level, pageable).map(SystemLogDTO::from);
    }

    @Transactional(readOnly = true)
    public Page<SystemLogDTO> getLogsByDateRange(LocalDateTime start, LocalDateTime end, Pageable pageable) {
        return systemLogRepository.findByTimestampBetween(start, end, pageable).map(SystemLogDTO::from);
    }
}
