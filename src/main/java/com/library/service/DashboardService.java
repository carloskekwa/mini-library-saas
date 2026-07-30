package com.library.service;
import com.library.dto.DashboardDTO;
import com.library.entity.Dashboard;
import com.library.entity.User;
import com.library.exception.ResourceNotFoundException;
import com.library.repository.DashboardRepository;
import com.library.repository.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.time.LocalDateTime;

/**
 * Service for dashboard management (Phase 7).
 */
@Service
@Transactional
public class DashboardService {
    private static final Logger logger = LoggerFactory.getLogger(DashboardService.class);
    private final DashboardRepository dashboardRepository;
    private final UserRepository userRepository;

    public DashboardService(DashboardRepository dashboardRepository, UserRepository userRepository) {
        this.dashboardRepository = dashboardRepository;
        this.userRepository = userRepository;
    }

    public DashboardDTO getUserDashboard(Long userId) {
        logger.info("Getting dashboard for user: {}", userId);
        Dashboard dashboard = dashboardRepository.findByUserId(userId)
            .orElse(createDefaultDashboard(userId));
        return DashboardDTO.from(dashboard);
    }

    private Dashboard createDefaultDashboard(Long userId) {
        User user = userRepository.findById(userId)
            .orElseThrow(() -> new ResourceNotFoundException("User not found"));
        Dashboard dashboard = new Dashboard(user, "{}");
        return dashboardRepository.save(dashboard);
    }

    public DashboardDTO updateDashboard(Long userId, String widgetConfig) {
        logger.info("Updating dashboard for user: {}", userId);
        Dashboard dashboard = dashboardRepository.findByUserId(userId)
            .orElse(createDefaultDashboard(userId));
        dashboard.setWidgetConfig(widgetConfig);
        dashboard.setUpdatedAt(LocalDateTime.now());
        Dashboard saved = dashboardRepository.save(dashboard);
        return DashboardDTO.from(saved);
    }
}
