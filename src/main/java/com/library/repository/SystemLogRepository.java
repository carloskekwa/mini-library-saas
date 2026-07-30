package com.library.repository;
import com.library.entity.SystemLog;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.time.LocalDateTime;

@Repository
public interface SystemLogRepository extends JpaRepository<SystemLog, Long> {
    Page<SystemLog> findByLevel(SystemLog.LogLevel level, Pageable pageable);
    Page<SystemLog> findByTimestampBetween(LocalDateTime start, LocalDateTime end, Pageable pageable);
}
