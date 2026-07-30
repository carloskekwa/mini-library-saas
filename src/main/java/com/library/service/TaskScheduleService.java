package com.library.service;
import com.library.dto.ScheduledTaskDTO;
import com.library.entity.ScheduledTask;
import com.library.exception.ResourceNotFoundException;
import com.library.repository.ScheduledTaskRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Service for task scheduling (Phase 17).
 */
@Service
@Transactional
public class TaskScheduleService {
    private static final Logger logger = LoggerFactory.getLogger(TaskScheduleService.class);
    private final ScheduledTaskRepository scheduledTaskRepository;

    public TaskScheduleService(ScheduledTaskRepository scheduledTaskRepository) {
        this.scheduledTaskRepository = scheduledTaskRepository;
    }

    public ScheduledTaskDTO createTask(String taskName, String description, String cronExpression) {
        logger.info("Creating scheduled task: {}", taskName);
        ScheduledTask task = new ScheduledTask(taskName, description, cronExpression);
        ScheduledTask saved = scheduledTaskRepository.save(task);
        return ScheduledTaskDTO.from(saved);
    }

    public void executeTask(Long taskId) {
        logger.info("Executing task: {}", taskId);
        ScheduledTask task = scheduledTaskRepository.findById(taskId)
            .orElseThrow(() -> new ResourceNotFoundException("Task not found"));
        task.setStatus(ScheduledTask.TaskStatus.RUNNING);
        task.setLastExecution(LocalDateTime.now());
        scheduledTaskRepository.save(task);
    }

    public void completeTask(Long taskId) {
        logger.info("Completing task: {}", taskId);
        ScheduledTask task = scheduledTaskRepository.findById(taskId)
            .orElseThrow(() -> new ResourceNotFoundException("Task not found"));
        task.setStatus(ScheduledTask.TaskStatus.COMPLETED);
        scheduledTaskRepository.save(task);
    }

    @Transactional(readOnly = true)
    public List<ScheduledTaskDTO> getActiveTasks() {
        return scheduledTaskRepository.findByIsActiveTrue()
            .stream().map(ScheduledTaskDTO::from).collect(Collectors.toList());
    }

    public void disableTask(Long taskId) {
        logger.info("Disabling task: {}", taskId);
        ScheduledTask task = scheduledTaskRepository.findById(taskId)
            .orElseThrow(() -> new ResourceNotFoundException("Task not found"));
        task.setIsActive(false);
        scheduledTaskRepository.save(task);
    }
}
