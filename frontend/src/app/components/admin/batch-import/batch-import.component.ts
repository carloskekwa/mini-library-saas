import { Component, OnDestroy, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { ActivatedRoute, Router } from '@angular/router';
import { BatchImportService } from '../../../services/batch-import.service';
import { AuthService } from '../../../services/auth.service';
import { BatchImportJob } from '../../../models/index';
import { Subject } from 'rxjs';
import { takeUntil } from 'rxjs/operators';

@Component({
  selector: 'app-batch-import',
  standalone: true,
  imports: [CommonModule, FormsModule],
  template: `
    <div class="container-main">
      <h1>Batch Import</h1>
      <p class="lead">Create import jobs and monitor progress.</p>

      <div *ngIf="error" class="alert alert-danger">{{ error }}</div>
      <div *ngIf="successMessage" class="alert alert-success">{{ successMessage }}</div>

      <div class="card mb-4">
        <div class="card-header"><h5 class="mb-0">Create Import Job</h5></div>
        <div class="card-body">
          <div class="input-group">
            <input class="form-control" [(ngModel)]="filePath" placeholder="/absolute/path/to/file.csv" />
            <button class="btn btn-primary" (click)="createJob()">Create Job</button>
          </div>
        </div>
      </div>

      <div class="card">
        <div class="table-responsive">
          <table class="table mb-0">
            <thead>
              <tr>
                <th>ID</th>
                <th>File</th>
                <th>Status</th>
                <th>Processed</th>
                <th>Started</th>
                <th class="text-end">Actions</th>
              </tr>
            </thead>
            <tbody>
              <tr *ngFor="let job of jobs">
                <td>{{ job.id }}</td>
                <td>{{ job.filePath }}</td>
                <td>{{ job.status }}</td>
                <td>{{ job.processedRecords || 0 }}/{{ job.totalRecords || 0 }} (failed: {{ job.failedRecords || 0 }})</td>
                <td>{{ job.startedAt ? (job.startedAt | date: 'short') : '-' }}</td>
                <td class="text-end">
                  <button *ngIf="canAdminActions" class="btn btn-sm btn-outline-primary me-2" (click)="start(job)">Start</button>
                  <button *ngIf="canAdminActions" class="btn btn-sm btn-outline-success" (click)="complete(job)">Complete</button>
                </td>
              </tr>
              <tr *ngIf="jobs.length === 0"><td colspan="6" class="text-center text-muted py-4">No jobs found.</td></tr>
            </tbody>
          </table>
        </div>
        <div class="card-footer d-flex justify-content-between align-items-center" *ngIf="totalPages > 1">
          <button class="btn btn-sm btn-outline-primary" [disabled]="currentPage === 0" (click)="changePage(currentPage - 1)">Previous</button>
          <span>Page {{ currentPage + 1 }} / {{ totalPages }}</span>
          <button class="btn btn-sm btn-outline-primary" [disabled]="currentPage + 1 >= totalPages" (click)="changePage(currentPage + 1)">Next</button>
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
export class BatchImportComponent implements OnInit, OnDestroy {
  filePath = '';
  jobs: BatchImportJob[] = [];
  currentPage = 0;
  pageSize = 20;
  totalElements = 0;
  error = '';
  successMessage = '';

  private readonly destroy$ = new Subject<void>();

  constructor(
    private readonly batchImportService: BatchImportService,
    private readonly authService: AuthService,
    private readonly route: ActivatedRoute,
    private readonly router: Router
  ) {}

  get canAdminActions(): boolean {
    return this.authService.hasRole('ADMIN');
  }

  ngOnInit(): void {
    this.currentPage = Number(this.route.snapshot.queryParams['page'] || 0);
    this.loadJobs();
  }

  ngOnDestroy(): void {
    this.destroy$.next();
    this.destroy$.complete();
  }

  loadJobs(): void {
    this.batchImportService.getUserJobs(this.currentPage, this.pageSize).pipe(takeUntil(this.destroy$)).subscribe({
      next: (response) => {
        this.jobs = response.content || [];
        this.totalElements = response.totalElements || this.jobs.length;
        this.syncUrl();
      },
      error: (err) => {
        this.error = err.error?.message || 'Failed to load jobs';
      }
    });
  }

  createJob(): void {
    if (!this.filePath.trim()) {
      this.error = 'filePath is required';
      return;
    }

    this.batchImportService.createImportJob(this.filePath).pipe(takeUntil(this.destroy$)).subscribe({
      next: () => {
        this.successMessage = 'Import job created';
        this.filePath = '';
        this.loadJobs();
      },
      error: (err) => {
        this.error = err.error?.message || 'Failed to create import job';
      }
    });
  }

  start(job: BatchImportJob): void {
    this.batchImportService.startImport(job.id).pipe(takeUntil(this.destroy$)).subscribe({
      next: () => {
        this.successMessage = `Job #${job.id} started`;
        this.loadJobs();
      },
      error: (err) => {
        this.error = err.error?.message || 'Failed to start import job';
      }
    });
  }

  complete(job: BatchImportJob): void {
    this.batchImportService.completeImport(job.id).pipe(takeUntil(this.destroy$)).subscribe({
      next: () => {
        this.successMessage = `Job #${job.id} completed`;
        this.loadJobs();
      },
      error: (err) => {
        this.error = err.error?.message || 'Failed to complete import job';
      }
    });
  }

  changePage(page: number): void {
    this.currentPage = page;
    this.loadJobs();
  }

  get totalPages(): number {
    return Math.ceil(this.totalElements / this.pageSize);
  }

  private syncUrl(): void {
    this.router.navigate([], {
      relativeTo: this.route,
      queryParams: { page: this.currentPage || null },
      queryParamsHandling: 'merge'
    });
  }
}
