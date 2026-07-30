import { Component, OnDestroy, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { ConfigService } from '../../../services/config.service';
import { ConfigProperty } from '../../../models/index';
import { Subject } from 'rxjs';
import { takeUntil } from 'rxjs/operators';

@Component({
  selector: 'app-config-admin',
  standalone: true,
  imports: [CommonModule, FormsModule],
  template: `
    <div class="container-main">
      <h1>System Config</h1>
      <p class="lead">Manage editable configuration properties.</p>

      <div *ngIf="error" class="alert alert-danger">{{ error }}</div>
      <div *ngIf="successMessage" class="alert alert-success">{{ successMessage }}</div>

      <div class="card mb-4">
        <div class="card-header"><h5 class="mb-0">Create/Update Config</h5></div>
        <div class="card-body">
          <div class="row g-2">
            <div class="col-md-2"><input class="form-control" [(ngModel)]="key" placeholder="key" /></div>
            <div class="col-md-3"><input class="form-control" [(ngModel)]="value" placeholder="value" /></div>
            <div class="col-md-3"><input class="form-control" [(ngModel)]="description" placeholder="description" /></div>
            <div class="col-md-2">
              <select class="form-select" [(ngModel)]="type">
                <option value="STRING">STRING</option>
                <option value="INTEGER">INTEGER</option>
                <option value="BOOLEAN">BOOLEAN</option>
                <option value="DECIMAL">DECIMAL</option>
              </select>
            </div>
            <div class="col-md-2"><button class="btn btn-primary w-100" (click)="save()">Save</button></div>
          </div>
        </div>
      </div>

      <div class="card">
        <div class="table-responsive">
          <table class="table mb-0">
            <thead>
              <tr>
                <th>Key</th>
                <th>Value</th>
                <th>Type</th>
                <th>Editable</th>
                <th class="text-end">Action</th>
              </tr>
            </thead>
            <tbody>
              <tr *ngFor="let config of configs">
                <td>{{ config.key }}</td>
                <td>{{ config.value }}</td>
                <td>{{ config.type }}</td>
                <td>{{ config.isEditable ? 'Yes' : 'No' }}</td>
                <td class="text-end">
                  <button class="btn btn-sm btn-outline-primary me-2" (click)="use(config)">Load</button>
                  <button class="btn btn-sm btn-outline-danger" (click)="remove(config)">Delete</button>
                </td>
              </tr>
              <tr *ngIf="configs.length === 0"><td colspan="5" class="text-center text-muted py-4">No config properties.</td></tr>
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
export class ConfigAdminComponent implements OnInit, OnDestroy {
  configs: ConfigProperty[] = [];

  key = '';
  value = '';
  description = '';
  type = 'STRING';

  error = '';
  successMessage = '';

  private readonly destroy$ = new Subject<void>();

  constructor(private readonly configService: ConfigService) {}

  ngOnInit(): void {
    this.load();
  }

  ngOnDestroy(): void {
    this.destroy$.next();
    this.destroy$.complete();
  }

  load(): void {
    this.configService.getAllConfigs().pipe(takeUntil(this.destroy$)).subscribe({
      next: (configs) => {
        this.configs = configs || [];
      },
      error: (err) => {
        this.error = err.error?.message || 'Failed to load configs';
      }
    });
  }

  save(): void {
    if (!this.key.trim() || !this.value.trim()) {
      this.error = 'Key and value are required';
      return;
    }

    this.configService.updateConfig({
      key: this.key,
      value: this.value,
      description: this.description,
      type: this.type
    }).pipe(takeUntil(this.destroy$)).subscribe({
      next: () => {
        this.successMessage = 'Config saved';
        this.load();
      },
      error: (err) => {
        this.error = err.error?.message || 'Failed to save config';
      }
    });
  }

  use(config: ConfigProperty): void {
    this.key = config.key;
    this.value = config.value;
    this.description = config.description || '';
    this.type = config.type || 'STRING';
  }

  remove(config: ConfigProperty): void {
    if (!confirm(`Delete config "${config.key}"?`)) {
      return;
    }

    this.configService.deleteConfig(config.key).pipe(takeUntil(this.destroy$)).subscribe({
      next: () => {
        this.successMessage = 'Config deleted';
        this.load();
      },
      error: (err) => {
        this.error = err.error?.message || 'Failed to delete config';
      }
    });
  }
}
