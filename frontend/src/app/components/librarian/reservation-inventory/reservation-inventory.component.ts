import { Component, OnDestroy, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { ReservationService } from '../../../services/reservation.service';
import { Reservation } from '../../../models/index';
import { Subject } from 'rxjs';
import { takeUntil } from 'rxjs/operators';

@Component({
  selector: 'app-reservation-inventory',
  standalone: true,
  imports: [CommonModule, FormsModule],
  templateUrl: './reservation-inventory.component.html',
  styleUrls: ['./reservation-inventory.component.css']
})
export class ReservationInventoryComponent implements OnInit, OnDestroy {
  records: Reservation[] = [];
  loading = false;
  error = '';
  successMessage = '';
  searchTerm = '';
  statusFilter = '';
  currentPage = 0;
  pageSize = 20;
  totalElements = 0;
  processingId: number | null = null;

  private readonly destroy$ = new Subject<void>();

  constructor(private readonly reservationService: ReservationService) {}

  ngOnInit(): void {
    this.loadInventory();
  }

  ngOnDestroy(): void {
    this.destroy$.next();
    this.destroy$.complete();
  }

  loadInventory(): void {
    this.loading = true;
    this.error = '';

    this.reservationService.getAllReservations(this.currentPage, this.pageSize, this.statusFilter || undefined)
      .pipe(takeUntil(this.destroy$))
      .subscribe({
        next: (response) => {
          this.records = (response.content || []).map(r => this.normalize(r));
          this.totalElements = response.totalElements || this.records.length;
          this.loading = false;
        },
        error: (err) => {
          this.loading = false;
          this.error = this.extractErrorMessage(err, 'Failed to load reservation inventory');
        }
      });
  }

  applyFilters(): void {
    this.currentPage = 0;
    this.loadInventory();
  }

  clearFilters(): void {
    this.statusFilter = '';
    this.searchTerm = '';
    this.currentPage = 0;
    this.loadInventory();
  }

  changePage(p: number): void {
    this.currentPage = p;
    this.loadInventory();
  }

  fulfill(reservation: Reservation): void {
    this.processingId = reservation.id;
    this.reservationService.fulfillReservation(reservation.id)
      .pipe(takeUntil(this.destroy$))
      .subscribe({
        next: () => {
          this.processingId = null;
          this.successMessage = 'Reservation fulfilled and converted to borrowing';
          this.loadInventory();
          setTimeout(() => this.successMessage = '', 3000);
        },
        error: (err) => {
          this.processingId = null;
          this.error = this.extractErrorMessage(err, 'Failed to fulfill reservation');
        }
      });
  }

  cancel(reservation: Reservation): void {
    this.processingId = reservation.id;
    this.reservationService.cancelReservation(reservation.id)
      .pipe(takeUntil(this.destroy$))
      .subscribe({
        next: () => {
          this.processingId = null;
          this.successMessage = 'Reservation cancelled';
          this.loadInventory();
          setTimeout(() => this.successMessage = '', 3000);
        },
        error: (err) => {
          this.processingId = null;
          this.error = this.extractErrorMessage(err, 'Failed to cancel reservation');
        }
      });
  }

  get filteredRecords(): Reservation[] {
    const term = this.searchTerm.trim().toLowerCase();
    if (!term) {
      return this.records;
    }
    return this.records.filter(r =>
      (r.bookTitle || r.book?.title || '').toLowerCase().includes(term) ||
      (r.username || '').toLowerCase().includes(term)
    );
  }

  get totalPages(): number {
    return Math.ceil(this.totalElements / this.pageSize);
  }

  getStatusBadgeClass(status: string): string {
    switch (status) {
      case 'PENDING': return 'bg-warning text-dark';
      case 'NOTIFIED': return 'bg-info text-dark';
      case 'FULFILLED': return 'bg-success';
      case 'CANCELLED': return 'bg-secondary';
      case 'EXPIRED': return 'bg-danger';
      default: return 'bg-secondary';
    }
  }

  getDaysUntilExpiry(expiryDate: string): number {
    const diff = new Date(expiryDate).getTime() - Date.now();
    return Math.ceil(diff / (1000 * 3600 * 24));
  }

  private normalize(r: any): Reservation {
    if (r.book) {
      return r;
    }
    return {
      ...r,
      bookTitle: r.bookTitle,
      book: {
        id: r.bookId,
        title: r.bookTitle || 'Unknown',
        author: r.bookAuthor || '',
        isbn: '', publisher: '', publicationYear: 0,
        description: '', language: '', shelfLocation: '',
        totalCopies: 0, availableCopies: 0, coverImageUrl: '', status: ''
      }
    };
  }

  private extractErrorMessage(error: any, fallback: string): string {
    const apiMessage = error?.error?.message;
    const apiError = error?.error?.error;
    const rootMessage = error?.message;

    return apiMessage || apiError || rootMessage || fallback;
  }
}
