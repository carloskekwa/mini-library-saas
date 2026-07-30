package com.library.service;
import com.library.dto.PenaltyDTO;
import com.library.entity.Penalty;
import com.library.entity.User;
import com.library.repository.PenaltyRepository;
import com.library.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class PenaltyServiceTest {
    @Mock
    private PenaltyRepository penaltyRepository;
    @Mock
    private UserRepository userRepository;
    
    private PenaltyService penaltyService;

    @BeforeEach
    void setUp() {
        penaltyService = new PenaltyService(penaltyRepository, userRepository);
    }

    @Test
    void testCreatePenalty() {
        User user = new User("user1", "user1@email.com", "hashed");
        Penalty penalty = new Penalty(user, Penalty.PenaltyType.SUSPENSION, "Test reason");
        penalty.setId(1L);

        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(penaltyRepository.save(any(Penalty.class))).thenReturn(penalty);

        PenaltyDTO result = penaltyService.createPenalty(1L, Penalty.PenaltyType.SUSPENSION, "Test reason");

        assertNotNull(result);
        assertEquals(1L, result.getId());
        verify(penaltyRepository, times(1)).save(any(Penalty.class));
    }

    @Test
    void testGetUserActivePenalties() {
        User user = new User("user1", "user1@email.com", "hashed");
        Penalty penalty = new Penalty(user, Penalty.PenaltyType.WARNING, "Test");
        penalty.setId(1L);
        
        when(penaltyRepository.findActivePenaltiesByUserId(1L)).thenReturn(Arrays.asList(penalty));

        List<PenaltyDTO> result = penaltyService.getUserActivePenalties(1L);

        assertEquals(1, result.size());
    }

    @Test
    void testLiftPenalty() {
        User user = new User("user1", "user1@email.com", "hashed");
        Penalty penalty = new Penalty(user, Penalty.PenaltyType.WARNING, "Test");
        penalty.setId(1L);
        penalty.setStatus(Penalty.PenaltyStatus.ACTIVE);

        when(penaltyRepository.findById(1L)).thenReturn(Optional.of(penalty));
        when(penaltyRepository.save(any(Penalty.class))).thenReturn(penalty);

        penaltyService.liftPenalty(1L);

        verify(penaltyRepository, times(1)).save(any(Penalty.class));
    }
}
