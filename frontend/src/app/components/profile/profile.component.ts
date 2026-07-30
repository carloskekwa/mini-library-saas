import { Component, OnDestroy, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { Subject } from 'rxjs';
import { takeUntil } from 'rxjs/operators';
import { User } from '../../models/index';
import { AuthService } from '../../services/auth.service';
import { ProfileService } from '../../services/profile.service';

@Component({
  selector: 'app-profile',
  standalone: true,
  imports: [CommonModule, FormsModule],
  template: `
    <div class="container-main">
      <h1>Profile</h1>
      <p class="lead">Manage your account details based on your role permissions.</p>

      <div *ngIf="error" class="alert alert-danger alert-dismissible fade show">
        {{ error }}
        <button type="button" class="btn-close" (click)="error = ''"></button>
      </div>

      <div *ngIf="successMessage" class="alert alert-success alert-dismissible fade show">
        {{ successMessage }}
        <button type="button" class="btn-close" (click)="successMessage = ''"></button>
      </div>

      <div class="card">
        <div class="card-body">
          <div class="row g-3">
            <div class="col-md-6">
              <label class="form-label">First Name</label>
              <input class="form-control" [(ngModel)]="form.firstName" />
            </div>

            <div class="col-md-6">
              <label class="form-label">Last Name</label>
              <input class="form-control" [(ngModel)]="form.lastName" />
            </div>

            <div class="col-md-6">
              <label class="form-label">Email</label>
              <input class="form-control" [(ngModel)]="form.email" [disabled]="!canEditEmail" />
              <small class="text-muted" *ngIf="!canEditEmail">Members cannot update email.</small>
            </div>

            <div class="col-md-6">
              <label class="form-label">Username</label>
              <input class="form-control" [(ngModel)]="form.username" [disabled]="!canEditUsername" />
              <small class="text-muted" *ngIf="!canEditUsername">Only admins can update username.</small>
            </div>

            <div class="col-md-6">
              <label class="form-label">Role(s)</label>
              <input class="form-control" [value]="form.rolesLabel" disabled />
            </div>

            <div class="col-md-6">
              <label class="form-label">Status</label>
              <input class="form-control" [value]="form.status" disabled />
            </div>
          </div>

          <div class="mt-4 d-flex gap-2">
            <button class="btn btn-primary" (click)="save()" [disabled]="saving || loading">
              {{ saving ? 'Saving...' : 'Save Changes' }}
            </button>
            <button class="btn btn-outline-secondary" (click)="loadProfile()" [disabled]="saving || loading">
              Reload
            </button>
          </div>
        </div>
      </div>
    </div>
  `,
  styles: [
    'h1 { color: #153d64; font-weight: 800; }',
    '.lead { color: #5f7892; }',
    '.card { border: 1px solid #dbe8f6; border-radius: 16px; box-shadow: 0 10px 24px rgba(20,52,85,0.08); }'
  ]
})
export class ProfileComponent implements OnInit, OnDestroy {
  loading = false;
  saving = false;
  error = '';
  successMessage = '';

  form = {
    username: '',
    email: '',
    firstName: '',
    lastName: '',
    rolesLabel: '',
    status: ''
  };

  private readonly destroy$ = new Subject<void>();

  constructor(
    private readonly profileService: ProfileService,
    private readonly authService: AuthService
  ) {}

  ngOnInit(): void {
    this.loadProfile();
  }

  ngOnDestroy(): void {
    this.destroy$.next();
    this.destroy$.complete();
  }

  get canEditEmail(): boolean {
    return this.authService.hasAnyRole(['ADMIN', 'LIBRARIAN']);
  }

  get canEditUsername(): boolean {
    return this.authService.hasRole('ADMIN');
  }

  loadProfile(): void {
    this.loading = true;
    this.error = '';

    this.profileService.getMyProfile()
      .pipe(takeUntil(this.destroy$))
      .subscribe({
        next: (profile: User) => {
          this.form.username = profile.username || '';
          this.form.email = profile.email || '';
          this.form.firstName = profile.firstName || '';
          this.form.lastName = profile.lastName || '';
          this.form.rolesLabel = (profile.roles || []).join(', ');
          this.form.status = profile.status || '';
          this.loading = false;
        },
        error: (err) => {
          this.error = err.error?.message || 'Failed to load profile.';
          this.loading = false;
        }
      });
  }

  save(): void {
    this.saving = true;
    this.error = '';

    const payload: { username?: string; email?: string; firstName?: string; lastName?: string } = {
      firstName: this.form.firstName,
      lastName: this.form.lastName
    };

    if (this.canEditEmail) {
      payload.email = this.form.email;
    }

    if (this.canEditUsername) {
      payload.username = this.form.username;
    }

    this.profileService.updateMyProfile(payload)
      .pipe(takeUntil(this.destroy$))
      .subscribe({
        next: (updated: User) => {
          this.authService.updateCurrentUserProfile(updated);
          this.successMessage = 'Profile updated successfully.';
          this.saving = false;
          this.loadProfile();
          setTimeout(() => this.successMessage = '', 3000);
        },
        error: (err) => {
          this.error = err.error?.message || 'Failed to update profile.';
          this.saving = false;
        }
      });
  }
}
