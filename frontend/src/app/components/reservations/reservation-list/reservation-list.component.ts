import { Component, OnInit, OnDestroy } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { ReservationService } from '../../../services/reservation.service';
import { AuthService } from '../../../services/auth.service';
import { Reservation } from '../../../models/index';
import { Subject } from 'rxjs';
import { takeUntil } from 'rxjs/operators';

@Component({
  selector: 'app-reservation-list',
  standalone: true,
  imports: [CommonModule, FormsModule],
  templateUrl: './reservation-list.component.html',
  styleUrls: ['./reservation-list.component.css']
})
export class ReservationListComponent implements OnInit, OnDestroy {
  reservations: Reservation[] = [];
  queueReservations: Reservation[] = [];
  selectedBookId: number | null = null;
  loadingQueue = false;
  loading = false;
  error = '';
  successMessage = '';
  private destroy$ = new Subject<void>();

  constructor(
    private reservationService: ReservationService,
    private authService: AuthService
  ) {}

  ngOnInit(): void {
    this.loadReservations();
  }

  ngOnDestroy(): void {
    this.destroy$.next();
    this.destroy$.complete();
  }

  loadReservations(): void {
    this.loading = true;
    this.reservationService.getActiveReservations()
      .pipe(takeUntil(this.destroy$))
      .subscribe({
        next: (response) => {
          this.reservations = (response || []).map((r: any) => this.normalizeReservation(r));
          this.loading = false;
        },
        error: () => {
          this.error = 'Failed to load reservations';
          this.loading = false;
        }
      });
  }

  get canManageQueue(): boolean {
    return this.authService.hasAnyRole(['LIBRARIAN', 'ADMIN']);
  }

  viewQueue(bookId: number): void {
    this.selectedBookId = bookId;
    this.loadingQueue = true;
    this.reservationService.getBookReservationQueue(bookId)
      .pipe(takeUntil(this.destroy$))
      .subscribe({
        next: (queue) => {
          this.queueReservations = queue || [];
          this.loadingQueue = false;
        },
        error: (err) => {
          this.error = err.error?.message || 'Failed to load reservation queue';
          this.loadingQueue = false;
        }
      });
  }

  fulfillReservation(reservation: Reservation): void {
    this.reservationService.fulfillReservation(reservation.id)
      .pipe(takeUntil(this.destroy$))
      .subscribe({
        next: () => {
          this.successMessage = 'Reservation fulfilled successfully';
          if (this.selectedBookId) {
            this.viewQueue(this.selectedBookId);
          }
          this.loadReservations();
          setTimeout(() => this.successMessage = '', 3000);
        },
        error: (err) => {
          this.error = err.error?.message || 'Failed to fulfill reservation';
        }
      });
  }

  cancelReservation(reservation: Reservation): void {
    this.reservationService.cancelReservation(reservation.id)
      .pipe(takeUntil(this.destroy$))
      .subscribe({
        next: () => {
          this.successMessage = 'Reservation cancelled';
          this.loadReservations();
          setTimeout(() => this.successMessage = '', 3000);
        },
        error: (err) => {
          this.error = err.error?.message || 'Failed to cancel reservation';
        }
      });
  }

  getDaysUntilExpiry(expiryDate: string): number {
    const expiry = new Date(expiryDate);
    const today = new Date();
    const time = expiry.getTime() - today.getTime();
    return Math.ceil(time / (1000 * 3600 * 24));
  }

  getBookCoverLetter(title: string | undefined): string {
    if (!title?.trim()) {
      return 'B';
    }
    return title.trim().charAt(0).toUpperCase();
  }

  private normalizeReservation(r: any) {
    if (r.book) {
      return r;
    }
    return {
      ...r,
      book: {
        id: r.bookId,
        title: r.bookTitle || 'Unknown',
        author: r.bookAuthor || '',
        isbn: '',
        publisher: '',
        publicationYear: 0,
        description: '',
        language: '',
        shelfLocation: '',
        totalCopies: 0,
        availableCopies: 0,
        coverImageUrl: '',
        status: ''
      }
    };
  }
}
