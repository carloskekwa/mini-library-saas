package com.library.service;
import com.library.dto.ReportDTO;
import com.library.entity.Report;
import com.library.repository.ReportRepository;
import com.library.repository.BorrowRecordRepository;
import com.library.repository.BookRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import java.time.LocalDateTime;
import java.util.Arrays;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ReportServiceTest {
    @Mock
    private ReportRepository reportRepository;
    @Mock
    private BorrowRecordRepository borrowRecordRepository;
    @Mock
    private BookRepository bookRepository;
    
    private ReportService reportService;

    @BeforeEach
    void setUp() {
        reportService = new ReportService(reportRepository, borrowRecordRepository, bookRepository);
    }

    @Test
    void testGeneratePopularBooksReport() {
        Report report = new Report(Report.ReportType.POPULAR_BOOKS, "test");
        report.setId(1L);
        when(reportRepository.save(any(Report.class))).thenReturn(report);

        ReportDTO result = reportService.generatePopularBooksReport();

        assertNotNull(result);
        assertEquals(1L, result.getId());
        verify(reportRepository, times(1)).save(any(Report.class));
    }

    @Test
    void testGenerateCirculationStatsReport() {
        LocalDateTime start = LocalDateTime.now().minusDays(30);
        LocalDateTime end = LocalDateTime.now();
        Report report = new Report(Report.ReportType.CIRCULATION_STATS, "test");
        report.setId(1L);
        report.setStartDate(start);
        report.setEndDate(end);
        when(reportRepository.save(any(Report.class))).thenReturn(report);

        ReportDTO result = reportService.generateCirculationStatsReport(start, end);

        assertNotNull(result);
        assertEquals(Report.ReportType.CIRCULATION_STATS.name(), result.getType());
        verify(reportRepository, times(1)).save(any(Report.class));
    }

    @Test
    void testGetAllReports() {
        Report report1 = new Report(Report.ReportType.POPULAR_BOOKS, "test1");
        report1.setId(1L);
        Page<Report> page = new PageImpl<>(Arrays.asList(report1));
        when(reportRepository.findAll(any(Pageable.class))).thenReturn(page);

        Page<ReportDTO> result = reportService.getAllReports(PageRequest.of(0, 10));

        assertNotNull(result);
        assertEquals(1, result.getContent().size());
        verify(reportRepository, times(1)).findAll(any(Pageable.class));
    }

    @Test
    void testDeleteReport() {
        Long reportId = 1L;
        reportService.deleteReport(reportId);
        verify(reportRepository, times(1)).deleteById(reportId);
    }
}
