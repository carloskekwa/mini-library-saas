package com.library.service;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

/**
 * Service for scheduled tasks (Phase 17).
 */
@Service
@EnableScheduling
public class ScheduledTaskService {
    private static final Logger logger = LoggerFactory.getLogger(ScheduledTaskService.class);
    private final ReservationService reservationService;

    public ScheduledTaskService(ReservationService reservationService) {
        this.reservationService = reservationService;
    }

    @Scheduled(cron = "0 0 * * * *")
    public void expireReservations() {
        logger.info("Running scheduled task: expire reservations");
        reservationService.expireExpiredReservations();
    }

    @Scheduled(cron = "0 0 1 * * *")
    public void generateDailyReport() {
        logger.info("Running scheduled task: generate daily report");
    }

    @Scheduled(cron = "0 0 0 * * MON")
    public void weeklyMaintenance() {
        logger.info("Running scheduled task: weekly maintenance");
    }
}
