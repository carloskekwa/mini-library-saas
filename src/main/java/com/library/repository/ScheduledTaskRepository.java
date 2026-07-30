package com.library.repository;
import com.library.entity.ScheduledTask;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;

@Repository
public interface ScheduledTaskRepository extends JpaRepository<ScheduledTask, Long> {
    Optional<ScheduledTask> findByTaskName(String taskName);
    List<ScheduledTask> findByIsActiveTrue();
}
