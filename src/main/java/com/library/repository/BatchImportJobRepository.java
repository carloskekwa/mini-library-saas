package com.library.repository;
import com.library.entity.BatchImportJob;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface BatchImportJobRepository extends JpaRepository<BatchImportJob, Long> {
    Page<BatchImportJob> findByUserId(Long userId, Pageable pageable);
    Page<BatchImportJob> findByStatus(BatchImportJob.JobStatus status, Pageable pageable);
}
