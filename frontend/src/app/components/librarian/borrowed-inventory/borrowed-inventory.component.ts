import { Component, OnDestroy, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { BorrowService } from '../../../services/borrow.service';
import { BorrowRecord } from '../../../models/index';
import { Subject } from 'rxjs';
import { takeUntil } from 'rxjs/operators';

@Component({
  selector: 'app-borrowed-inventory',
  standalone: true,
  imports: [CommonModule, FormsModule],
  templateUrl: './borrowed-inventory.component.html',
  styleUrls: ['./borrowed-inventory.component.css']
})
export class BorrowedInventoryComponent implements OnInit, OnDestroy {
  records: BorrowRecord[] = [];
  filteredRecords: BorrowRecord[] = [];
  bookBorrowedCounts: Record<number, number> = {};
  loading = false;
  error = '';
  successMessage = '';
  searchTerm = '';
  statusFilter: 'ALL' | 'PENDING' | 'ACTIVE' = 'ALL';
  currentPage = 0;
  pageSize = 20;
  totalElements = 0;
  selectedCondition: Record<number, string> = {};
  damageNotes: Record<number, string> = {};
  processingId: number | null = null;

  private readonly destroy$ = new Subject<void>();

  constructor(private readonly borrowService: BorrowService) {}

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

    this.borrowService.getBorrowInventory(this.currentPage, this.pageSize)
      .pipe(takeUntil(this.destroy$))
      .subscribe({
        next: (response) => {
          this.records = (response.content || []).map((record) => this.normalizeRecord(record));
          this.bookBorrowedCounts = this.calculateBorrowCountsByBook(this.records);
          this.filteredRecords = [...this.records];
          this.totalElements = response.totalElements || this.records.length;
          this.applyFilter();
          this.loading = false;
        },
        error: (err) => {
          this.loading = false;
          this.error = err.error?.message || 'Failed to load borrowed inventory';
        }
      });
  }

  applyFilter(): void {
    const term = this.searchTerm.trim().toLowerCase();

    this.filteredRecords = this.records.filter((record) => {
      const matchesTerm = !term ||
        (record.bookTitle || '').toLowerCase().includes(term) ||
        (record.bookAuthor || '').toLowerCase().includes(term) ||
        (record.username || '').toLowerCase().includes(term);

      const matchesStatus =
        this.statusFilter === 'ALL' ||
        (this.statusFilter === 'PENDING' && record.status === 'PENDING') ||
        (this.statusFilter === 'ACTIVE' && (record.status === 'BORROWED' || record.status === 'OVERDUE'));

      return matchesTerm && matchesStatus;
    });
  }

  changePage(nextPage: number): void {
    this.currentPage = nextPage;
    this.loadInventory();
  }

  renewRecord(record: BorrowRecord): void {
    this.processingId = record.id;
    this.borrowService.renewBorrow(record.id)
      .pipe(takeUntil(this.destroy$))
      .subscribe({
        next: () => {
          this.processingId = null;
          this.successMessage = 'Borrow renewed successfully';
          this.loadInventory();
          setTimeout(() => this.successMessage = '', 3000);
        },
        error: (err) => {
          this.processingId = null;
          this.error = err.error?.message || 'Failed to renew borrow';
        }
      });
  }

  approveDemand(record: BorrowRecord): void {
    this.processingId = record.id;
    this.borrowService.approveBorrowDemand(record.id)
      .pipe(takeUntil(this.destroy$))
      .subscribe({
        next: () => {
          this.processingId = null;
          this.successMessage = 'Borrow demand approved. Member has been notified for pickup.';
          this.loadInventory();
          setTimeout(() => this.successMessage = '', 3000);
        },
        error: (err) => {
          this.processingId = null;
          this.error = err.error?.message || 'Failed to approve borrow demand';
        }
      });
  }

  rejectDemand(record: BorrowRecord): void {
    this.processingId = record.id;
    this.borrowService.rejectBorrowDemand(record.id)
      .pipe(takeUntil(this.destroy$))
      .subscribe({
        next: () => {
          this.processingId = null;
          this.successMessage = 'Borrow demand rejected. Member has been notified.';
          this.loadInventory();
          setTimeout(() => this.successMessage = '', 3000);
        },
        error: (err) => {
          this.processingId = null;
          this.error = err.error?.message || 'Failed to reject borrow demand';
        }
      });
  }

  returnRecord(record: BorrowRecord): void {
    this.processingId = record.id;
    this.borrowService.returnBook({
      borrowRecordId: record.id,
      bookCondition: this.selectedCondition[record.id] || 'GOOD',
      damageNotes: this.damageNotes[record.id] || ''
    })
      .pipe(takeUntil(this.destroy$))
      .subscribe({
        next: () => {
          this.processingId = null;
          this.successMessage = 'Book returned successfully';
          this.loadInventory();
          setTimeout(() => this.successMessage = '', 3000);
        },
        error: (err) => {
          this.processingId = null;
          this.error = err.error?.message || 'Failed to return book';
        }
      });
  }

  get totalPages(): number {
    return Math.ceil(this.totalElements / this.pageSize);
  }

  getBorrowedCount(record: BorrowRecord): number {
    return this.bookBorrowedCounts[record.bookId] || 0;
  }

  getTotalCopies(record: BorrowRecord): number {
    return Number(record.totalCopies || record.book.totalCopies || 0);
  }

  getAvailableCopies(record: BorrowRecord): number {
    return Number(record.availableCopies || record.book.availableCopies || 0);
  }

  private normalizeRecord(record: BorrowRecord): BorrowRecord {
    if (record.book) {
      return {
        ...record,
        bookTitle: record.bookTitle || record.book.title,
        bookAuthor: record.bookAuthor || record.book.author,
        totalCopies: record.totalCopies ?? record.book.totalCopies,
        availableCopies: record.availableCopies ?? record.book.availableCopies
      };
    }

    return {
      ...record,
      book: {
        id: record.bookId,
        title: record.bookTitle || 'Unknown Title',
        author: record.bookAuthor || 'Unknown Author',
        isbn: '',
        publisher: '',
        publicationYear: 0,
        description: '',
        language: '',
        shelfLocation: '',
        totalCopies: record.totalCopies || 0,
        availableCopies: record.availableCopies || 0,
        coverImageUrl: '',
        status: ''
      }
    };
  }

  private calculateBorrowCountsByBook(records: BorrowRecord[]): Record<number, number> {
    return records.reduce((acc, record) => {
      const bookId = Number(record.bookId || record.book.id);
      acc[bookId] = (acc[bookId] || 0) + 1;
      return acc;
    }, {} as Record<number, number>);
  }
}
