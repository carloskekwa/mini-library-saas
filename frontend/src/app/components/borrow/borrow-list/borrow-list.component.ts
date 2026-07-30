import { Component, OnInit, OnDestroy } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { BorrowService } from '../../../services/borrow.service';
import { AuthService } from '../../../services/auth.service';
import { BorrowRecord, ReturnRecord } from '../../../models/index';
import { Subject } from 'rxjs';
import { takeUntil } from 'rxjs/operators';

@Component({
  selector: 'app-borrow-list',
  standalone: true,
  imports: [CommonModule, FormsModule],
  templateUrl: './borrow-list.component.html',
  styleUrls: ['./borrow-list.component.css']
})
export class BorrowListComponent implements OnInit, OnDestroy {
  activeBorrows: BorrowRecord[] = [];
  borrowHistory: BorrowRecord[] = [];
  returnRecords: ReturnRecord[] = [];
  loading = false;
  error = '';
  successMessage = '';
  totalFines: number = 0;
  selectedCondition: Record<number, string> = {};
  damageNotes: Record<number, string> = {};
  payingFineId: number | null = null;
  private destroy$ = new Subject<void>();

  constructor(
    private borrowService: BorrowService,
    private authService: AuthService
  ) {}

  ngOnInit(): void {
    this.loadActiveBorrows();
    this.loadBorrowHistory();
    this.loadReturnRecords();
    this.loadTotalFines();
  }

  ngOnDestroy(): void {
    this.destroy$.next();
    this.destroy$.complete();
  }

  loadActiveBorrows(): void {
    this.loading = true;
    this.borrowService.getActiveBorrows()
      .pipe(takeUntil(this.destroy$))
      .subscribe({
        next: (response) => {
          this.activeBorrows = this.normalizeBorrowRecords(response);
          this.loading = false;
        },
        error: () => {
          this.error = 'Failed to load active borrows';
          this.loading = false;
        }
      });
  }

  loadBorrowHistory(): void {
    this.borrowService.getBorrowHistory()
      .pipe(takeUntil(this.destroy$))
      .subscribe({
        next: (response) => {
          this.borrowHistory = this.normalizeBorrowRecords(response);
        }
      });
  }

  private normalizeBorrowRecords(response: any): BorrowRecord[] {
    const records = Array.isArray(response) ? response : (response?.content || []);

    return records.map((record: any) => {
      if (record?.book) {
        return record as BorrowRecord;
      }

      return {
        ...record,
        renewalCount: record?.renewalCount ?? 0,
        isOverdue: record?.isOverdue ?? false,
        book: {
          id: record?.bookId ?? 0,
          title: record?.bookTitle ?? 'Unknown Title',
          author: record?.bookAuthor ?? 'Unknown Author'
        }
      } as BorrowRecord;
    });
  }

  loadTotalFines(): void {
    this.borrowService.getTotalUnpaidFines()
      .pipe(takeUntil(this.destroy$))
      .subscribe({
        next: (response) => {
          this.totalFines = Number(response?.totalUnpaidFines ?? 0);
        }
      });
  }

  loadReturnRecords(): void {
    this.borrowService.getReturnRecords()
      .pipe(takeUntil(this.destroy$))
      .subscribe({
        next: (response) => {
          this.returnRecords = response.content || [];
        },
        error: () => {
          this.error = 'Failed to load return records';
        }
      });
  }

  renewBook(borrow: BorrowRecord): void {
    this.borrowService.renewBorrow(borrow.id)
      .pipe(takeUntil(this.destroy$))
      .subscribe({
        next: () => {
          this.successMessage = 'Book renewed successfully!';
          this.loadActiveBorrows();
          setTimeout(() => this.successMessage = '', 3000);
        },
        error: (err) => {
          this.error = err.error?.message || 'Failed to renew book';
        }
      });
  }

  returnBook(borrow: BorrowRecord): void {
    const bookCondition = this.selectedCondition[borrow.id] || 'GOOD';
    const notes = this.damageNotes[borrow.id] || '';

    this.borrowService.returnBook({
      borrowRecordId: borrow.id,
      bookCondition,
      damageNotes: notes
    })
      .pipe(takeUntil(this.destroy$))
      .subscribe({
        next: () => {
          this.successMessage = 'Book returned successfully!';
          this.loadActiveBorrows();
          this.loadBorrowHistory();
          this.loadReturnRecords();
          this.loadTotalFines();
          setTimeout(() => this.successMessage = '', 3000);
        },
        error: (err) => {
          this.error = err.error?.message || 'Failed to return book';
        }
      });
  }

  payFine(record: ReturnRecord): void {
    this.payingFineId = record.id;
    this.borrowService.payFine(record.id)
      .pipe(takeUntil(this.destroy$))
      .subscribe({
        next: () => {
          this.payingFineId = null;
          this.successMessage = 'Fine paid successfully!';
          this.loadReturnRecords();
          this.loadTotalFines();
          setTimeout(() => this.successMessage = '', 3000);
        },
        error: (err) => {
          this.payingFineId = null;
          this.error = err.error?.message || 'Failed to pay fine';
        }
      });
  }

  getDaysUntilDue(dueDate: string): number {
    const due = new Date(dueDate);
    const today = new Date();
    const time = due.getTime() - today.getTime();
    return Math.ceil(time / (1000 * 3600 * 24));
  }

  isOverdue(dueDate: string): boolean {
    return new Date(dueDate) < new Date();
  }

  getBorrowStatusLabel(status: string): string {
    if (status === 'PENDING') {
      return 'Pending Approval';
    }
    if (status === 'BORROWED') {
      return 'Borrowed';
    }
    if (status === 'OVERDUE') {
      return 'Overdue';
    }
    return status;
  }

  getBorrowStatusBadgeClass(status: string): string {
    if (status === 'PENDING') {
      return 'bg-warning';
    }
    if (status === 'BORROWED') {
      return 'bg-primary';
    }
    if (status === 'OVERDUE') {
      return 'bg-danger';
    }
    if (status === 'RETURNED') {
      return 'bg-success';
    }
    return 'bg-secondary';
  }

  hasUnpaidFine(record: ReturnRecord): boolean {
    return Number(record.fineAmount || 0) > 0 && !record.finePaid;
  }

  getFineAmount(record: ReturnRecord): number {
    return Number(record.fineAmount || 0);
  }

  get canManageBorrowActions(): boolean {
    return this.authService.hasAnyRole(['LIBRARIAN', 'ADMIN']);
  }

  getBookCoverLetter(title: string | undefined): string {
    if (!title?.trim()) {
      return 'B';
    }
    return title.trim().charAt(0).toUpperCase();
  }
}
