import { Component, OnDestroy, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { ScheduledTaskService } from '../../../services/scheduled-task.service';
import { ScheduledTask } from '../../../models/index';
import { Subject } from 'rxjs';
import { takeUntil } from 'rxjs/operators';

@Component({
  selector: 'app-scheduled-tasks',
  standalone: true,
  imports: [CommonModule, FormsModule],
  template: `
    <div class="container-main">
      <h1>Scheduled Tasks</h1>
      <p class="lead">Create and disable scheduled jobs.</p>

      <div *ngIf="error" class="alert alert-danger">{{ error }}</div>
      <div *ngIf="successMessage" class="alert alert-success">{{ successMessage }}</div>

      <div class="card mb-4">
        <div class="card-header"><h5 class="mb-0">Create Task</h5></div>
        <div class="card-body">
          <div class="row g-2">
            <div class="col-md-3"><input class="form-control" [(ngModel)]="taskName" placeholder="taskName" /></div>
            <div class="col-md-4"><input class="form-control" [(ngModel)]="description" placeholder="description" /></div>
            <div class="col-md-3"><input class="form-control" [(ngModel)]="cronExpression" placeholder="cron expression" /></div>
            <div class="col-md-2"><button class="btn btn-primary w-100" (click)="create()">Create</button></div>
          </div>
        </div>
      </div>

      <div class="card">
        <div class="table-responsive">
          <table class="table mb-0">
            <thead>
              <tr>
                <th>Name</th>
                <th>Status</th>
                <th>Cron</th>
                <th>Next</th>
                <th class="text-end">Action</th>
              </tr>
            </thead>
            <tbody>
              <tr *ngFor="let task of tasks">
                <td>{{ task.taskName }}</td>
                <td>{{ task.status }}</td>
                <td>{{ task.cronExpression }}</td>
                <td>{{ task.nextExecution ? (task.nextExecution | date: 'short') : '-' }}</td>
                <td class="text-end">
                  <button class="btn btn-sm btn-outline-danger" [disabled]="!task.isActive" (click)="disable(task)">Disable</button>
                </td>
              </tr>
              <tr *ngIf="tasks.length === 0"><td colspan="5" class="text-center text-muted py-4">No tasks.</td></tr>
            </tbody>
          </table>
        </div>
      </div>
    </div>
  `,
  styles: [
    'h1 { color: #0d47a1; font-weight: 700; }',
    '.lead { color: #5f6b7a; }',
    '.card { border: none; box-shadow: 0 2px 10px rgba(0,0,0,0.08); }'
  ]
})
export class ScheduledTasksComponent implements OnInit, OnDestroy {
  tasks: ScheduledTask[] = [];
  taskName = '';
  description = '';
  cronExpression = '';
  error = '';
  successMessage = '';

  private readonly destroy$ = new Subject<void>();

  constructor(private readonly scheduledTaskService: ScheduledTaskService) {}

  ngOnInit(): void {
    this.load();
  }

  ngOnDestroy(): void {
    this.destroy$.next();
    this.destroy$.complete();
  }

  load(): void {
    this.scheduledTaskService.getActiveTasks().pipe(takeUntil(this.destroy$)).subscribe({
      next: (tasks) => {
        this.tasks = tasks || [];
      },
      error: (err) => {
        this.error = err.error?.message || 'Failed to load tasks';
      }
    });
  }

  create(): void {
    if (!this.taskName.trim() || !this.description.trim() || !this.cronExpression.trim()) {
      this.error = 'taskName, description, and cronExpression are required';
      return;
    }

    this.scheduledTaskService.createTask({
      taskName: this.taskName,
      description: this.description,
      cronExpression: this.cronExpression
    }).pipe(takeUntil(this.destroy$)).subscribe({
      next: () => {
        this.successMessage = 'Task created';
        this.load();
      },
      error: (err) => {
        this.error = err.error?.message || 'Failed to create task';
      }
    });
  }

  disable(task: ScheduledTask): void {
    this.scheduledTaskService.disableTask(task.id).pipe(takeUntil(this.destroy$)).subscribe({
      next: () => {
        this.successMessage = 'Task disabled';
        this.load();
      },
      error: (err) => {
        this.error = err.error?.message || 'Failed to disable task';
      }
    });
  }
}
