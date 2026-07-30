package com.library.service;
import com.library.dto.AuditLogDTO;
import com.library.entity.AuditLog;
import com.library.entity.User;
import com.library.repository.AuditLogRepository;
import com.library.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Optional;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AuditServiceTest {
    @Mock
    private AuditLogRepository auditLogRepository;
    @Mock
    private UserRepository userRepository;
    
    private AuditService auditService;

    @BeforeEach
    void setUp() {
        auditService = new AuditService(auditLogRepository, userRepository);
    }

    @Test
    void testLogAction() {
        User user = new User("user1", "user1@email.com", "hashed");
        AuditLog log = new AuditLog(user, "CREATE", "Book", 1L, "Book created", "127.0.0.1");
        log.setId(1L);

        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(auditLogRepository.save(any(AuditLog.class))).thenReturn(log);

        auditService.logAction(1L, "CREATE", "Book", 1L, "Book created", "127.0.0.1");

        verify(auditLogRepository, times(1)).save(any(AuditLog.class));
    }

    @Test
    void testGetUserAuditLogs() {
        User user = new User("user1", "user1@email.com", "hashed");
        AuditLog log = new AuditLog(user, "UPDATE", "Book", 1L, "Book updated", "127.0.0.1");
        log.setId(1L);
        Page<AuditLog> page = new PageImpl<>(Arrays.asList(log));

        when(auditLogRepository.findByUserId(1L, PageRequest.of(0, 20))).thenReturn(page);

        Page<AuditLogDTO> result = auditService.getUserAuditLogs(1L, PageRequest.of(0, 20));

        assertEquals(1, result.getContent().size());
    }

    @Test
    void testGetAuditLogsByDateRange() {
        LocalDateTime start = LocalDateTime.now().minusDays(7);
        LocalDateTime end = LocalDateTime.now();
        User user = new User("user1", "user1@email.com", "hashed");
        AuditLog log = new AuditLog(user, "DELETE", "Book", 1L, "Book deleted", "127.0.0.1");
        log.setId(1L);
        Page<AuditLog> page = new PageImpl<>(Arrays.asList(log));

        when(auditLogRepository.findByTimestampBetween(start, end, PageRequest.of(0, 20))).thenReturn(page);

        Page<AuditLogDTO> result = auditService.getAuditLogsByDateRange(start, end, PageRequest.of(0, 20));

        assertEquals(1, result.getContent().size());
    }
}
