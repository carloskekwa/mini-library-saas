package com.library.service;
import com.library.dto.ReportDTO;
import com.library.entity.Report;
import com.library.repository.ReportRepository;
import com.library.repository.BorrowRecordRepository;
import com.library.repository.BookRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.time.LocalDateTime;
import java.util.stream.Collectors;

/**
 * Service for generating and managing reports (Phase 6).
 */
@Service
@Transactional
public class ReportService {
    private static final Logger logger = LoggerFactory.getLogger(ReportService.class);
    private final ReportRepository reportRepository;
    private final BorrowRecordRepository borrowRecordRepository;
    private final BookRepository bookRepository;

    public ReportService(ReportRepository reportRepository,
                        BorrowRecordRepository borrowRecordRepository,
                        BookRepository bookRepository) {
        this.reportRepository = reportRepository;
        this.borrowRecordRepository = borrowRecordRepository;
        this.bookRepository = bookRepository;
    }

    public ReportDTO generatePopularBooksReport() {
        logger.info("Generating popular books report");
        String content = "Popular books report generated at " + LocalDateTime.now();
        Report report = new Report(Report.ReportType.POPULAR_BOOKS, content);
        Report saved = reportRepository.save(report);
        return ReportDTO.from(saved);
    }

    public ReportDTO generateCirculationStatsReport(LocalDateTime start, LocalDateTime end) {
        logger.info("Generating circulation stats report from {} to {}", start, end);
        String content = "Circulation stats report for period " + start + " to " + end;
        Report report = new Report(Report.ReportType.CIRCULATION_STATS, content);
        report.setStartDate(start);
        report.setEndDate(end);
        Report saved = reportRepository.save(report);
        return ReportDTO.from(saved);
    }

    @Transactional(readOnly = true)
    public Page<ReportDTO> getReportsByType(Report.ReportType type, Pageable pageable) {
        return reportRepository.findByType(type, pageable).map(ReportDTO::from);
    }

    @Transactional(readOnly = true)
    public Page<ReportDTO> getAllReports(Pageable pageable) {
        return reportRepository.findAll(pageable).map(ReportDTO::from);
    }

    public void deleteReport(Long reportId) {
        logger.info("Deleting report: {}", reportId);
        reportRepository.deleteById(reportId);
    }
}
