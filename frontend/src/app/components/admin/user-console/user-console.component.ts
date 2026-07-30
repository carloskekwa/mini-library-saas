import { Component, OnDestroy, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { ActivatedRoute, Router, RouterModule } from '@angular/router';
import { Subject } from 'rxjs';
import { takeUntil } from 'rxjs/operators';
import { User } from '../../../models/index';
import { AuthService } from '../../../services/auth.service';
import { UserAdminService } from '../../../services/user-admin.service';

@Component({
  selector: 'app-user-console',
  standalone: true,
  imports: [CommonModule, FormsModule, RouterModule],
  template: `
    <div class="container-main">
      <h1>User Console</h1>
      <p class="lead" *ngIf="isAdmin">Create new user accounts, update account status, and open moderation workflows.</p>
      <p class="lead" *ngIf="!isAdmin">Search users and open moderation workflows.</p>

      <div *ngIf="error" class="alert alert-danger">{{ error }}</div>
      <div *ngIf="successMessage" class="alert alert-success">{{ successMessage }}</div>

      <div class="card mb-4" *ngIf="isAdmin">
        <div class="card-body">
          <h5 class="mb-3">Create User</h5>
          <div class="row g-3">
            <div class="col-md-6">
              <label class="form-label">Username</label>
              <input class="form-control" [(ngModel)]="newUsername" placeholder="username" />
            </div>
            <div class="col-md-6">
              <label class="form-label">Email</label>
              <input class="form-control" [(ngModel)]="newEmail" placeholder="email" />
            </div>
            <div class="col-md-6">
              <label class="form-label">Password</label>
              <input type="password" class="form-control" [(ngModel)]="newPassword" placeholder="Minimum 8 characters" />
            </div>
            <div class="col-md-6">
              <label class="form-label">Confirm Password</label>
              <input type="password" class="form-control" [(ngModel)]="newPasswordConfirm" placeholder="Repeat password" />
            </div>
            <div class="col-md-6">
              <label class="form-label">First Name (optional)</label>
              <input class="form-control" [(ngModel)]="newFirstName" placeholder="First name" />
            </div>
            <div class="col-md-6">
              <label class="form-label">Last Name (optional)</label>
              <input class="form-control" [(ngModel)]="newLastName" placeholder="Last name" />
            </div>
            <div class="col-12 d-flex gap-2">
              <button class="btn btn-primary" (click)="createUser()" [disabled]="creatingUser">
                {{ creatingUser ? 'Creating...' : 'Create User' }}
              </button>
            </div>
          </div>
        </div>
      </div>

      <div class="card mb-4">
        <div class="card-body">
          <div class="row g-3 align-items-end">
            <div class="col-md-4">
              <label class="form-label">Search</label>
              <input class="form-control" [(ngModel)]="search" placeholder="username or email" />
            </div>
            <div class="col-md-3" *ngIf="isAdmin">
              <label class="form-label">Status</label>
              <select class="form-select" [(ngModel)]="statusFilter">
                <option value="">All</option>
                <option value="ACTIVE">ACTIVE</option>
                <option value="INACTIVE">INACTIVE</option>
                <option value="SUSPENDED">SUSPENDED</option>
              </select>
            </div>
            <div class="col-md-5 d-flex gap-2">
              <button class="btn btn-primary" (click)="applyFilters()">Apply</button>
              <button class="btn btn-outline-secondary" (click)="clearFilters()">Clear</button>
            </div>
          </div>
        </div>
      </div>

      <div class="card">
        <div class="card-body">
          <div class="table-responsive">
            <table class="table mb-0">
              <thead>
                <tr>
                  <th>ID</th>
                  <th>Username</th>
                  <th>Email</th>
                  <th *ngIf="isAdmin">Status</th>
                  <th>Roles</th>
                  <th class="text-end">Actions</th>
                </tr>
              </thead>
              <tbody>
                <tr *ngFor="let user of users" [class.table-active]="selectedUserId === user.id">
                  <td>{{ user.id }}</td>
                  <td>{{ user.username }}</td>
                  <td>{{ user.email }}</td>
                  <td *ngIf="isAdmin"><span class="badge" [class]="'bg-' + (user.status === 'ACTIVE' ? 'success' : (user.status === 'SUSPENDED' ? 'danger' : 'secondary'))">{{ user.status }}</span></td>
                  <td>{{ user.roles.join(', ') }}</td>
                  <td class="text-end">
                    <div class="btn-group btn-group-sm">
                      <button class="btn btn-outline-primary" (click)="selectUser(user)">Select</button>
                      <button *ngIf="isAdmin" class="btn btn-outline-success" [disabled]="user.status === 'ACTIVE'" (click)="setStatus(user, 'ACTIVE')">Activate</button>
                      <button *ngIf="isAdmin" class="btn btn-outline-warning" [disabled]="user.status === 'INACTIVE'" (click)="setStatus(user, 'INACTIVE')">Inactivate</button>
                      <button *ngIf="isAdmin" class="btn btn-outline-danger" [disabled]="user.status === 'SUSPENDED'" (click)="setStatus(user, 'SUSPENDED')">Suspend</button>
                    </div>
                  </td>
                </tr>
                <tr *ngIf="!loading && users.length === 0">
                  <td [attr.colspan]="isAdmin ? 6 : 5" class="text-center text-muted py-4">No users found.</td>
                </tr>
              </tbody>
            </table>
          </div>

          <div class="d-flex justify-content-between align-items-center mt-3" *ngIf="totalPages > 1">
            <button class="btn btn-sm btn-outline-primary" [disabled]="currentPage === 0" (click)="changePage(currentPage - 1)">Previous</button>
            <span>Page {{ currentPage + 1 }} / {{ totalPages }}</span>
            <button class="btn btn-sm btn-outline-primary" [disabled]="currentPage + 1 >= totalPages" (click)="changePage(currentPage + 1)">Next</button>
          </div>

          <div class="row g-3 align-items-end mt-2">
            <div class="col-md-4">
              <label class="form-label">Selected User ID</label>
              <select class="form-select" [(ngModel)]="selectedUserId" [disabled]="users.length === 0">
                <option [ngValue]="null">Select from table view</option>
                <option *ngFor="let user of users" [ngValue]="user.id">{{ user.id }} - {{ user.username }}</option>
              </select>
            </div>
            <div class="col-md-8 d-flex gap-2">
              <button class="btn btn-primary" (click)="openPenalties()">Open Penalties</button>
              <button class="btn btn-outline-secondary" (click)="openBookRequests()">Open Book Requests</button>
            </div>
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
export class UserConsoleComponent implements OnInit, OnDestroy {
  users: User[] = [];
  selectedUserId: number | null = null;
  search = '';
  statusFilter = '';
  currentPage = 0;
  pageSize = 20;
  totalElements = 0;
  loading = false;
  creatingUser = false;
  error = '';
  successMessage = '';
  newUsername = '';
  newEmail = '';
  newPassword = '';
  newPasswordConfirm = '';
  newFirstName = '';
  newLastName = '';
  private readonly destroy$ = new Subject<void>();

  constructor(
    private readonly router: Router,
    private readonly route: ActivatedRoute,
    private readonly authService: AuthService,
    private readonly userAdminService: UserAdminService
  ) {}

  get isAdmin(): boolean {
    return this.authService.hasRole('ADMIN');
  }

  get isLibrarian(): boolean {
    return this.authService.hasRole('LIBRARIAN');
  }

  ngOnInit(): void {
    const query = this.route.snapshot.queryParams;
    this.search = query['search'] || '';
    this.statusFilter = this.isAdmin ? (query['status'] || '') : '';
    this.currentPage = Number(query['page'] || 0);
    this.selectedUserId = query['userId'] ? Number(query['userId']) : null;
    this.loadUsers();
  }

  ngOnDestroy(): void {
    this.destroy$.next();
    this.destroy$.complete();
  }

  loadUsers(): void {
    this.loading = true;
    this.error = '';
    this.userAdminService.getUsers({
      page: this.currentPage,
      pageSize: this.pageSize,
      search: this.search,
      status: this.statusFilter
    }).pipe(takeUntil(this.destroy$)).subscribe({
      next: (response) => {
        const fetchedUsers = response.content || [];
        this.users = this.isLibrarian
          ? fetchedUsers.filter((user) => user.roles?.includes('MEMBER'))
          : fetchedUsers;
        this.totalElements = this.isLibrarian
          ? this.users.length
          : (response.totalElements || this.users.length);
        this.loading = false;
        this.syncUrl();
      },
      error: (err) => {
        this.loading = false;
        this.error = err.error?.message || 'Failed to load users';
      }
    });
  }

  applyFilters(): void {
    this.currentPage = 0;
    this.loadUsers();
  }

  clearFilters(): void {
    this.search = '';
    this.statusFilter = this.isAdmin ? '' : this.statusFilter;
    this.currentPage = 0;
    this.loadUsers();
  }

  changePage(page: number): void {
    this.currentPage = page;
    this.loadUsers();
  }

  createUser(): void {
    if (!this.isAdmin) {
      return;
    }

    if (!this.newUsername.trim() || !this.newEmail.trim() || !this.newPassword) {
      this.error = 'Username, email, and password are required.';
      return;
    }

    if (this.newPassword !== this.newPasswordConfirm) {
      this.error = 'Password and confirmation do not match.';
      return;
    }

    this.creatingUser = true;
    this.error = '';

    this.userAdminService.createUser({
      username: this.newUsername.trim(),
      email: this.newEmail.trim(),
      password: this.newPassword,
      passwordConfirm: this.newPasswordConfirm,
      firstName: this.newFirstName.trim() || undefined,
      lastName: this.newLastName.trim() || undefined
    })
      .pipe(takeUntil(this.destroy$))
      .subscribe({
        next: (user) => {
          this.creatingUser = false;
          this.successMessage = `User ${user.username} created successfully.`;
          this.newUsername = '';
          this.newEmail = '';
          this.newPassword = '';
          this.newPasswordConfirm = '';
          this.newFirstName = '';
          this.newLastName = '';
          setTimeout(() => this.successMessage = '', 3000);
        },
        error: (err) => {
          this.creatingUser = false;
          this.error = err.error?.message || 'Failed to create user';
        }
      });
  }

  setStatus(user: User, status: string): void {
    if (!this.isAdmin) {
      return;
    }

    this.userAdminService.updateUserStatus(user.id, status)
      .pipe(takeUntil(this.destroy$))
      .subscribe({
        next: () => {
          this.successMessage = `Updated ${user.username} to ${status}`;
          this.loadUsers();
          setTimeout(() => this.successMessage = '', 3000);
        },
        error: (err) => {
          this.error = err.error?.message || 'Failed to update user status';
        }
      });
  }

  openPenalties(): void {
    if (!this.selectedUserId) {
      return;
    }
    this.router.navigate(['/admin/penalties'], { queryParams: { userId: this.selectedUserId } });
  }

  openBookRequests(): void {
    this.router.navigate(['/book-requests'], {
      queryParams: {
        userId: this.selectedUserId || null,
        staffStatus: 'OPEN'
      }
    });
  }

  selectUser(user: User): void {
    this.selectedUserId = user.id;
    this.syncUrl();
  }

  get totalPages(): number {
    return Math.ceil(this.totalElements / this.pageSize);
  }

  private syncUrl(): void {
    this.router.navigate([], {
      relativeTo: this.route,
      queryParams: {
        page: this.currentPage || null,
        search: this.search || null,
        status: this.isAdmin ? (this.statusFilter || null) : null,
        userId: this.selectedUserId || null
      },
      queryParamsHandling: 'merge'
    });
  }
}
