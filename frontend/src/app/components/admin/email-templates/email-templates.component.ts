import { Component } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { EmailTemplateService } from '../../../services/email-template.service';

@Component({
  selector: 'app-email-templates',
  standalone: true,
  imports: [CommonModule, FormsModule],
  template: `
    <div class="container-main">
      <h1>Email Templates</h1>
      <p class="lead">Load and update template content by type.</p>

      <div *ngIf="error" class="alert alert-danger">{{ error }}</div>
      <div *ngIf="successMessage" class="alert alert-success">{{ successMessage }}</div>

      <div class="card mb-4">
        <div class="card-body">
          <div class="row g-3">
            <div class="col-md-3">
              <label class="form-label">Template Type</label>
              <select class="form-select" [(ngModel)]="type">
                <option value="WELCOME">WELCOME</option>
                <option value="PASSWORD_RESET">PASSWORD_RESET</option>
                <option value="OVERDUE_REMINDER">OVERDUE_REMINDER</option>
                <option value="FINE_NOTICE">FINE_NOTICE</option>
                <option value="RESERVATION_READY">RESERVATION_READY</option>
              </select>
            </div>
            <div class="col-md-2 d-flex align-items-end"><button class="btn btn-outline-primary w-100" (click)="load()">Load</button></div>
            <div class="col-md-7"></div>
            <div class="col-md-4"><label class="form-label">Name</label><input class="form-control" [(ngModel)]="name" /></div>
            <div class="col-md-8"><label class="form-label">Subject</label><input class="form-control" [(ngModel)]="subject" /></div>
            <div class="col-12"><label class="form-label">Body</label><textarea class="form-control" rows="8" [(ngModel)]="body"></textarea></div>
            <div class="col-12"><button class="btn btn-primary" (click)="save()">Save Template</button></div>
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
export class EmailTemplatesComponent {
  type = 'WELCOME';
  name = '';
  subject = '';
  body = '';
  error = '';
  successMessage = '';

  constructor(private readonly emailTemplateService: EmailTemplateService) {}

  load(): void {
    this.emailTemplateService.getTemplate(this.type).subscribe({
      next: (template) => {
        this.name = template.name || '';
        this.subject = template.subject || '';
        this.body = template.body || '';
      },
      error: (err) => {
        this.error = err.error?.message || 'Failed to load template';
      }
    });
  }

  save(): void {
    if (!this.name.trim() || !this.subject.trim() || !this.body.trim()) {
      this.error = 'Name, subject, and body are required';
      return;
    }

    this.emailTemplateService.saveTemplate({
      type: this.type,
      name: this.name,
      subject: this.subject,
      body: this.body
    }).subscribe({
      next: () => {
        this.successMessage = 'Template saved';
      },
      error: (err) => {
        this.error = err.error?.message || 'Failed to save template';
      }
    });
  }
}
