import { Component, OnDestroy, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { ActivatedRoute, Router } from '@angular/router';
import { PenaltyService } from '../../../services/penalty.service';
import { AuthService } from '../../../services/auth.service';
import { Penalty } from '../../../models/index';
import { Subject } from 'rxjs';
import { takeUntil } from 'rxjs/operators';

@Component({
  selector: 'app-penalties',
  standalone: true,
  imports: [CommonModule, FormsModule],
  template: `
    <div class="container-main">
      <h1>Penalties</h1>
      <p class="lead">Apply and review user penalties.</p>

      <div *ngIf="error" class="alert alert-danger">{{ error }}</div>
      <div *ngIf="successMessage" class="alert alert-success">{{ successMessage }}</div>

      <div class="card mb-4">
        <div class="card-header"><h5 class="mb-0">Create Penalty</h5></div>
        <div class="card-body">
          <div class="row g-3">
            <div class="col-md-2"><input type="number" class="form-control" [(ngModel)]="createUserId" placeholder="User ID" /></div>
            <div class="col-md-3">
              <select class="form-select" [(ngModel)]="createType">
                <option value="SUSPENSION">SUSPENSION</option>
                <option value="WARNING">WARNING</option>
                <option value="FINE_DEFAULT">FINE_DEFAULT</option>
                <option value="DAMAGE_CHARGE">DAMAGE_CHARGE</option>
              </select>
            </div>
            <div class="col-md-5"><input class="form-control" [(ngModel)]="createReason" placeholder="Reason" /></div>
            <div class="col-md-2"><button class="btn btn-primary w-100" (click)="createPenalty()">Create</button></div>
          </div>
        </div>
      </div>

      <div class="card" *ngIf="canAdminActions">
        <div class="card-header"><h5 class="mb-0">Active Penalties by User</h5></div>
        <div class="card-body">
          <div class="d-flex gap-2 mb-3">
            <input type="number" class="form-control" [(ngModel)]="lookupUserId" placeholder="Enter user ID" />
            <button class="btn btn-outline-primary" (click)="loadUserPenalties()">Load</button>
          </div>
          <div class="table-responsive">
            <table class="table mb-0">
              <thead>
                <tr>
                  <th>ID</th>
                  <th>User</th>
                  <th>Type</th>
                  <th>Reason</th>
                  <th>Status</th>
                  <th>Created</th>
                  <th class="text-end">Action</th>
                </tr>
              </thead>
              <tbody>
                <tr *ngFor="let item of penalties">
                  <td>{{ item.id }}</td>
                  <td>{{ item.userId }}</td>
                  <td>{{ item.type }}</td>
                  <td>{{ item.reason }}</td>
                  <td>{{ item.status }}</td>
                  <td>{{ item.createdAt | date: 'short' }}</td>
                  <td class="text-end">
                    <button class="btn btn-sm btn-outline-danger" [disabled]="item.status !== 'ACTIVE'" (click)="lift(item)">Lift</button>
                  </td>
                </tr>
                <tr *ngIf="penalties.length === 0">
                  <td colspan="7" class="text-center text-muted py-4">No penalties loaded.</td>
                </tr>
              </tbody>
            </table>
          </div>
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
export class PenaltiesComponent implements OnInit, OnDestroy {
  createUserId: number | null = null;
  createType = 'SUSPENSION';
  createReason = '';

  lookupUserId: number | null = null;
  penalties: Penalty[] = [];

  error = '';
  successMessage = '';

  private readonly destroy$ = new Subject<void>();

  constructor(
    private readonly penaltyService: PenaltyService,
    private readonly authService: AuthService,
    private readonly route: ActivatedRoute,
    private readonly router: Router
  ) {}

  get canAdminActions(): boolean {
    return this.authService.hasRole('ADMIN');
  }

  ngOnInit(): void {
    const queryUserId = this.route.snapshot.queryParams['userId'];
    if (queryUserId) {
      this.lookupUserId = Number(queryUserId);
      if (this.canAdminActions) {
        this.loadUserPenalties();
      }
    }
  }

  ngOnDestroy(): void {
    this.destroy$.next();
    this.destroy$.complete();
  }

  createPenalty(): void {
    if (!this.createUserId || !this.createReason.trim()) {
      this.error = 'User ID and reason are required';
      return;
    }

    this.penaltyService.createPenalty({
      userId: this.createUserId,
      type: this.createType,
      reason: this.createReason
    }).pipe(takeUntil(this.destroy$)).subscribe({
      next: () => {
        this.successMessage = 'Penalty created';
        this.createReason = '';
      },
      error: (err) => {
        this.error = err.error?.message || 'Failed to create penalty';
      }
    });
  }

  loadUserPenalties(): void {
    if (!this.lookupUserId) {
      this.error = 'User ID is required';
      return;
    }

    this.penaltyService.getUserActivePenalties(this.lookupUserId)
      .pipe(takeUntil(this.destroy$))
      .subscribe({
        next: (items) => {
          this.penalties = items || [];
          this.syncUrl();
        },
        error: (err) => {
          this.error = err.error?.message || 'Failed to load penalties';
        }
      });
  }

  lift(item: Penalty): void {
    this.penaltyService.liftPenalty(item.id)
      .pipe(takeUntil(this.destroy$))
      .subscribe({
        next: () => {
          this.successMessage = 'Penalty lifted';
          this.loadUserPenalties();
        },
        error: (err) => {
          this.error = err.error?.message || 'Failed to lift penalty';
        }
      });
  }

  private syncUrl(): void {
    this.router.navigate([], {
      relativeTo: this.route,
      queryParams: { userId: this.lookupUserId || null },
      queryParamsHandling: 'merge'
    });
  }
}
