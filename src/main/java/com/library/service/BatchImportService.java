package com.library.service;
import com.library.dto.BatchImportJobDTO;
import com.library.entity.BatchImportJob;
import com.library.entity.User;
import com.library.exception.ResourceNotFoundException;
import com.library.repository.BatchImportJobRepository;
import com.library.repository.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.time.LocalDateTime;

/**
 * Service for batch operations (Phase 16).
 */
@Service
@Transactional
public class BatchImportService {
    private static final Logger logger = LoggerFactory.getLogger(BatchImportService.class);
    private final BatchImportJobRepository batchImportJobRepository;
    private final UserRepository userRepository;

    public BatchImportService(BatchImportJobRepository batchImportJobRepository, UserRepository userRepository) {
        this.batchImportJobRepository = batchImportJobRepository;
        this.userRepository = userRepository;
    }

    public BatchImportJobDTO createImportJob(Long userId, String filePath) {
        logger.info("Creating batch import job for user: {}", userId);
        User user = userRepository.findById(userId)
            .orElseThrow(() -> new ResourceNotFoundException("User not found"));
        
        BatchImportJob job = new BatchImportJob(user, filePath);
        BatchImportJob saved = batchImportJobRepository.save(job);
        return BatchImportJobDTO.from(saved);
    }

    public void startImport(Long jobId) {
        logger.info("Starting import job: {}", jobId);
        BatchImportJob job = batchImportJobRepository.findById(jobId)
            .orElseThrow(() -> new ResourceNotFoundException("Job not found"));
        job.setStatus(BatchImportJob.JobStatus.PROCESSING);
        job.setStartedAt(LocalDateTime.now());
        batchImportJobRepository.save(job);
    }

    public void completeImport(Long jobId) {
        logger.info("Completing import job: {}", jobId);
        BatchImportJob job = batchImportJobRepository.findById(jobId)
            .orElseThrow(() -> new ResourceNotFoundException("Job not found"));
        job.setStatus(BatchImportJob.JobStatus.COMPLETED);
        job.setCompletedAt(LocalDateTime.now());
        batchImportJobRepository.save(job);
    }

    @Transactional(readOnly = true)
    public Page<BatchImportJobDTO> getUserJobs(Long userId, Pageable pageable) {
        return batchImportJobRepository.findByUserId(userId, pageable).map(BatchImportJobDTO::from);
    }
}
