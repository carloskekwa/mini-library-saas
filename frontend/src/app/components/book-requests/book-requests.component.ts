import { Component, OnDestroy, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { BookRequestService } from '../../services/book-request.service';
import { AuthService } from '../../services/auth.service';
import { BookRequest } from '../../models/index';
import { Subject } from 'rxjs';
import { takeUntil } from 'rxjs/operators';

@Component({
  selector: 'app-book-requests',
  standalone: true,
  imports: [CommonModule, FormsModule],
  templateUrl: './book-requests.component.html',
  styleUrls: ['./book-requests.component.css']
})
export class BookRequestsComponent implements OnInit, OnDestroy {
  userRequests: BookRequest[] = [];
  staffRequests: BookRequest[] = [];
  staffStatusOptions: string[] = ['ALL', 'PENDING', 'APPROVED', 'ORDERED', 'REJECTED', 'FULFILLED'];
  selectedStaffStatus = 'ALL';

  loading = false;
  error = '';
  successMessage = '';

  bookTitle = '';
  author = '';
  isbn = '';
  justification = '';

  private readonly destroy$ = new Subject<void>();

  constructor(
    private readonly bookRequestService: BookRequestService,
    private readonly authService: AuthService
  ) {}

  ngOnInit(): void {
    this.loadUserRequests();
    if (this.canModerate) {
      this.loadStaffRequests();
    }
  }

  ngOnDestroy(): void {
    this.destroy$.next();
    this.destroy$.complete();
  }

  get canModerate(): boolean {
    return this.authService.hasAnyRole(['ADMIN', 'LIBRARIAN']);
  }

  get canApproveReject(): boolean {
    return this.authService.hasRole('ADMIN');
  }

  createRequest(): void {
    if (!this.bookTitle.trim() || !this.author.trim()) {
      this.error = 'Title and author are required.';
      return;
    }

    this.error = '';
    this.bookRequestService.createRequest({
      bookTitle: this.bookTitle,
      author: this.author,
      isbn: this.isbn,
      justification: this.justification
    }).pipe(takeUntil(this.destroy$)).subscribe({
      next: () => {
        this.successMessage = 'Book request submitted.';
        this.bookTitle = '';
        this.author = '';
        this.isbn = '';
        this.justification = '';
        this.loadUserRequests();
        if (this.canModerate) {
          this.loadStaffRequests();
        }
        setTimeout(() => this.successMessage = '', 3000);
      },
      error: (err) => {
        this.error = err.error?.message || 'Failed to submit request.';
      }
    });
  }

  loadUserRequests(): void {
    this.loading = true;
    this.bookRequestService.getUserRequests()
      .pipe(takeUntil(this.destroy$))
      .subscribe({
        next: (response) => {
          this.userRequests = response.content || [];
          this.loading = false;
        },
        error: (err) => {
          this.loading = false;
          this.error = err.error?.message || 'Failed to load your requests.';
        }
      });
  }

  loadStaffRequests(): void {
    this.bookRequestService.getAllRequests()
      .pipe(takeUntil(this.destroy$))
      .subscribe({
        next: (response) => {
          this.staffRequests = response.content || [];
        },
        error: (err) => {
          this.error = err.error?.message || 'Failed to load staff requests.';
        }
      });
  }

  approve(request: BookRequest): void {
    this.bookRequestService.approveRequest(request.id)
      .pipe(takeUntil(this.destroy$))
      .subscribe({
        next: () => {
          this.successMessage = 'Request approved.';
          this.loadStaffRequests();
          this.loadUserRequests();
          setTimeout(() => this.successMessage = '', 3000);
        },
        error: (err) => {
          this.error = err.error?.message || 'Failed to approve request.';
        }
      });
  }

  reject(request: BookRequest): void {
    this.bookRequestService.rejectRequest(request.id)
      .pipe(takeUntil(this.destroy$))
      .subscribe({
        next: () => {
          this.successMessage = 'Request rejected.';
          this.loadStaffRequests();
          this.loadUserRequests();
          setTimeout(() => this.successMessage = '', 3000);
        },
        error: (err) => {
          this.error = err.error?.message || 'Failed to reject request.';
        }
      });
  }

  order(request: BookRequest): void {
    this.bookRequestService.orderRequest(request.id)
      .pipe(takeUntil(this.destroy$))
      .subscribe({
        next: () => {
          this.successMessage = 'Request marked as ordered.';
          this.loadStaffRequests();
          this.loadUserRequests();
          setTimeout(() => this.successMessage = '', 3000);
        },
        error: (err) => {
          this.error = err.error?.message || 'Failed to order request.';
        }
      });
  }

  canOrder(request: BookRequest): boolean {
    return this.canModerate && request.status !== 'REJECTED' && request.status !== 'ORDERED' && request.status !== 'FULFILLED';
  }

  get filteredStaffRequests(): BookRequest[] {
    if (this.selectedStaffStatus === 'ALL') {
      return this.staffRequests;
    }
    return this.staffRequests.filter((request) => request.status === this.selectedStaffStatus);
  }
}
