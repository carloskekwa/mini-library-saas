package com.library.service;
import com.library.dto.PenaltyDTO;
import com.library.entity.Penalty;
import com.library.entity.User;
import com.library.exception.ResourceNotFoundException;
import com.library.repository.PenaltyRepository;
import com.library.repository.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Service for managing penalties (Phase 14).
 */
@Service
@Transactional
public class PenaltyService {
    private static final Logger logger = LoggerFactory.getLogger(PenaltyService.class);
    private final PenaltyRepository penaltyRepository;
    private final UserRepository userRepository;

    public PenaltyService(PenaltyRepository penaltyRepository, UserRepository userRepository) {
        this.penaltyRepository = penaltyRepository;
        this.userRepository = userRepository;
    }

    public PenaltyDTO createPenalty(Long userId, Penalty.PenaltyType type, String reason) {
        logger.info("Creating penalty: userId={}, type={}", userId, type);
        
        User user = userRepository.findById(userId)
            .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        Penalty penalty = new Penalty(user, type, reason);
        if (type == Penalty.PenaltyType.SUSPENSION) {
            penalty.setSuspendedUntil(LocalDateTime.now().plusDays(30));
        }
        
        Penalty saved = penaltyRepository.save(penalty);
        return PenaltyDTO.from(saved);
    }

    @Transactional(readOnly = true)
    public List<PenaltyDTO> getUserActivePenalties(Long userId) {
        return penaltyRepository.findActivePenaltiesByUserId(userId)
            .stream().map(PenaltyDTO::from).collect(Collectors.toList());
    }

    public void liftPenalty(Long penaltyId) {
        logger.info("Lifting penalty: {}", penaltyId);
        Penalty penalty = penaltyRepository.findById(penaltyId)
            .orElseThrow(() -> new ResourceNotFoundException("Penalty not found"));
        penalty.setStatus(Penalty.PenaltyStatus.LIFTED);
        penaltyRepository.save(penalty);
    }
}
