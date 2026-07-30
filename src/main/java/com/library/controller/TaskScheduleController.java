package com.library.controller;
import com.library.dto.ScheduledTaskDTO;
import com.library.service.TaskScheduleService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import java.util.List;

/**
 * REST controller for scheduled tasks (Phase 17).
 */
@RestController
@RequestMapping("/api/scheduled-tasks")
@Tag(name = "Task Scheduling", description = "APIs for scheduled task management")
public class TaskScheduleController {
    private final TaskScheduleService taskScheduleService;

    public TaskScheduleController(TaskScheduleService taskScheduleService) {
        this.taskScheduleService = taskScheduleService;
    }

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Create scheduled task")
    public ResponseEntity<ScheduledTaskDTO> createTask(
        @RequestParam String taskName,
        @RequestParam String description,
        @RequestParam String cronExpression) {
        ScheduledTaskDTO task = taskScheduleService.createTask(taskName, description, cronExpression);
        return ResponseEntity.status(201).body(task);
    }

    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Get active tasks")
    public ResponseEntity<List<ScheduledTaskDTO>> getActiveTasks() {
        List<ScheduledTaskDTO> tasks = taskScheduleService.getActiveTasks();
        return ResponseEntity.ok(tasks);
    }

    @PostMapping("/{taskId}/disable")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Disable task")
    public ResponseEntity<Void> disableTask(@PathVariable Long taskId) {
        taskScheduleService.disableTask(taskId);
        return ResponseEntity.noContent().build();
    }
}
