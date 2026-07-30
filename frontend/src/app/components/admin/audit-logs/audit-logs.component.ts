import { Component, OnDestroy } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { ActivatedRoute, Router } from '@angular/router';
import { AuditLogService } from '../../../services/audit-log.service';
import { AuditLog } from '../../../models/index';
import { Subject } from 'rxjs';
import { takeUntil } from 'rxjs/operators';

@Component({
  selector: 'app-audit-logs',
  standalone: true,
  imports: [CommonModule, FormsModule],
  template: `
    <div class="container-main">
      <h1>Audit Logs</h1>
      <p class="lead">Inspect system actions by user or date range.</p>

      <div *ngIf="error" class="alert alert-danger">{{ error }}</div>

      <div class="card mb-4">
        <div class="card-body">
          <div class="row g-2 align-items-end">
            <div class="col-md-3">
              <label class="form-label">User ID</label>
              <input type="number" class="form-control" [(ngModel)]="userId" />
            </div>
            <div class="col-md-2">
              <button class="btn btn-primary w-100" (click)="searchByUser()">Search User</button>
            </div>
            <div class="col-md-3">
              <label class="form-label">Start</label>
              <input type="datetime-local" class="form-control" [(ngModel)]="start" />
            </div>
            <div class="col-md-3">
              <label class="form-label">End</label>
              <input type="datetime-local" class="form-control" [(ngModel)]="end" />
            </div>
            <div class="col-md-1">
              <button class="btn btn-outline-primary w-100" (click)="searchByRange()">Go</button>
            </div>
          </div>
        </div>
      </div>

      <div class="card">
        <div class="table-responsive">
          <table class="table mb-0">
            <thead>
              <tr>
                <th>Time</th>
                <th>User</th>
                <th>Action</th>
                <th>Entity</th>
                <th>Details</th>
                <th>IP</th>
              </tr>
            </thead>
            <tbody>
              <tr *ngFor="let log of logs">
                <td>{{ log.timestamp | date: 'short' }}</td>
                <td>{{ log.userId || '-' }}</td>
                <td>{{ log.action }}</td>
                <td>{{ log.entityType }}#{{ log.entityId }}</td>
                <td>{{ log.details || '-' }}</td>
                <td>{{ log.ipAddress || '-' }}</td>
              </tr>
              <tr *ngIf="logs.length === 0">
                <td colspan="6" class="text-center text-muted py-4">No audit logs found.</td>
              </tr>
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
export class AuditLogsComponent implements OnDestroy {
  logs: AuditLog[] = [];
  userId: number | null = null;
  start = '';
  end = '';
  error = '';

  private readonly destroy$ = new Subject<void>();

  constructor(
    private readonly auditLogService: AuditLogService,
    private readonly route: ActivatedRoute,
    private readonly router: Router
  ) {
    const query = this.route.snapshot.queryParams;
    this.userId = query['userId'] ? Number(query['userId']) : null;
    this.start = query['start'] || '';
    this.end = query['end'] || '';
    const mode = query['mode'] || '';

    if (mode === 'user' && this.userId) {
      this.searchByUser();
    } else if (mode === 'range' && this.start && this.end) {
      this.searchByRange();
    }
  }

  ngOnDestroy(): void {
    this.destroy$.next();
    this.destroy$.complete();
  }

  searchByUser(): void {
    if (!this.userId) {
      this.error = 'User ID is required';
      return;
    }

    this.auditLogService.getUserAuditLogs(this.userId)
      .pipe(takeUntil(this.destroy$))
      .subscribe({
        next: (response) => {
          this.logs = response.content || [];
          this.syncUrl('user');
        },
        error: (err) => {
          this.error = err.error?.message || 'Failed to fetch user logs';
        }
      });
  }

  searchByRange(): void {
    if (!this.start || !this.end) {
      this.error = 'Start and end are required';
      return;
    }

    this.auditLogService.getAuditLogsByDateRange(this.toBackendDateTime(this.start), this.toBackendDateTime(this.end))
      .pipe(takeUntil(this.destroy$))
      .subscribe({
        next: (response) => {
          this.logs = response.content || [];
          this.syncUrl('range');
        },
        error: (err) => {
          this.error = err.error?.message || 'Failed to fetch date-range logs';
        }
      });
  }

  private toBackendDateTime(value: string): string {
    return `${value}:00`;
  }

  private syncUrl(mode: 'user' | 'range'): void {
    this.router.navigate([], {
      relativeTo: this.route,
      queryParams: {
        mode,
        userId: mode === 'user' ? (this.userId || null) : null,
        start: mode === 'range' ? (this.start || null) : null,
        end: mode === 'range' ? (this.end || null) : null
      },
      queryParamsHandling: 'merge'
    });
  }
}
