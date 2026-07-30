package com.library.repository;
import com.library.entity.Report;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface ReportRepository extends JpaRepository<Report, Long> {
    List<Report> findByType(Report.ReportType type);
    Page<Report> findByType(Report.ReportType type, Pageable pageable);
    Page<Report> findByGeneratedDateBetween(LocalDateTime start, LocalDateTime end, Pageable pageable);
    
    @Query("SELECT r FROM Report r WHERE r.type = :type ORDER BY r.generatedDate DESC")
    List<Report> findLatestByType(@Param("type") Report.ReportType type);
}
