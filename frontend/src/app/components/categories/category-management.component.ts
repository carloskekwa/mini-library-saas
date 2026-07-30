import { Component, OnDestroy, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { CategoryService } from '../../services/category.service';
import { AuthService } from '../../services/auth.service';
import { Category } from '../../models/index';
import { Subject } from 'rxjs';
import { takeUntil } from 'rxjs/operators';

@Component({
  selector: 'app-category-management',
  standalone: true,
  imports: [CommonModule, FormsModule],
  template: `
    <div class="container-main">
      <h1>Category Management</h1>
      <p class="lead">Manage category taxonomy used by books.</p>

      <div *ngIf="error" class="alert alert-danger">{{ error }}</div>
      <div *ngIf="successMessage" class="alert alert-success">{{ successMessage }}</div>

      <div class="card mb-4">
        <div class="card-header"><h5 class="mb-0">{{ editing ? 'Edit Category' : 'Create Category' }}</h5></div>
        <div class="card-body">
          <div class="row g-3">
            <div class="col-md-4">
              <input class="form-control" [(ngModel)]="name" placeholder="Name" />
            </div>
            <div class="col-md-6">
              <input class="form-control" [(ngModel)]="description" placeholder="Description" />
            </div>
            <div class="col-md-2">
              <button class="btn btn-primary w-100" (click)="save()">{{ editing ? 'Update' : 'Create' }}</button>
            </div>
          </div>
        </div>
      </div>

      <div class="card">
        <div class="table-responsive">
          <table class="table mb-0">
            <thead>
              <tr>
                <th>Name</th>
                <th>Description</th>
                <th class="text-end">Actions</th>
              </tr>
            </thead>
            <tbody>
              <tr *ngFor="let category of categories">
                <td>{{ category.name }}</td>
                <td>{{ category.description || '-' }}</td>
                <td class="text-end">
                  <button class="btn btn-sm btn-outline-primary me-2" (click)="startEdit(category)">Edit</button>
                  <button *ngIf="canDelete" class="btn btn-sm btn-outline-danger" (click)="remove(category)">Delete</button>
                </td>
              </tr>
              <tr *ngIf="categories.length === 0">
                <td colspan="3" class="text-center text-muted py-4">No categories available.</td>
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
export class CategoryManagementComponent implements OnInit, OnDestroy {
  categories: Category[] = [];
  editing: Category | null = null;
  name = '';
  description = '';
  error = '';
  successMessage = '';

  private readonly destroy$ = new Subject<void>();

  constructor(
    private readonly categoryService: CategoryService,
    private readonly authService: AuthService
  ) {}

  get canDelete(): boolean {
    return this.authService.hasRole('ADMIN');
  }

  ngOnInit(): void {
    this.load();
  }

  ngOnDestroy(): void {
    this.destroy$.next();
    this.destroy$.complete();
  }

  load(): void {
    this.categoryService.getCategories().pipe(takeUntil(this.destroy$)).subscribe({
      next: (categories) => {
        this.categories = categories;
      },
      error: (err) => {
        this.error = err.error?.message || 'Failed to load categories';
      }
    });
  }

  startEdit(category: Category): void {
    this.editing = category;
    this.name = category.name;
    this.description = category.description || '';
  }

  save(): void {
    if (!this.name.trim()) {
      this.error = 'Name is required';
      return;
    }

    this.error = '';
    if (this.editing) {
      this.categoryService.updateCategory(this.editing.id, { name: this.name, description: this.description })
        .pipe(takeUntil(this.destroy$))
        .subscribe({
          next: () => {
            this.successMessage = 'Category updated';
            this.resetForm();
            this.load();
          },
          error: (err) => {
            this.error = err.error?.message || 'Failed to update category';
          }
        });
      return;
    }

    this.categoryService.createCategory({ name: this.name, description: this.description })
      .pipe(takeUntil(this.destroy$))
      .subscribe({
        next: () => {
          this.successMessage = 'Category created';
          this.resetForm();
          this.load();
        },
        error: (err) => {
          this.error = err.error?.message || 'Failed to create category';
        }
      });
  }

  remove(category: Category): void {
    if (!confirm(`Delete category "${category.name}"?`)) {
      return;
    }

    this.categoryService.deleteCategory(category.id)
      .pipe(takeUntil(this.destroy$))
      .subscribe({
        next: () => {
          this.successMessage = 'Category deleted';
          this.load();
        },
        error: (err) => {
          this.error = err.error?.message || 'Failed to delete category';
        }
      });
  }

  private resetForm(): void {
    this.editing = null;
    this.name = '';
    this.description = '';
    setTimeout(() => this.successMessage = '', 3000);
  }
}
