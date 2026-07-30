import { Component, OnInit, OnDestroy } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { RouterModule, ActivatedRoute, Router } from '@angular/router';
import { BookService } from '../../../services/book.service';
import { BorrowService } from '../../../services/borrow.service';
import { ReservationService } from '../../../services/reservation.service';
import { WishlistService } from '../../../services/wishlist.service';
import { CategoryService } from '../../../services/category.service';
import { Book, Category } from '../../../models/index';
import { Subject } from 'rxjs';
import { takeUntil } from 'rxjs/operators';

@Component({
  selector: 'app-book-list',
  standalone: true,
  imports: [CommonModule, FormsModule, RouterModule],
  templateUrl: './book-list.component.html',
  styleUrls: ['./book-list.component.css']
})
export class BookListComponent implements OnInit, OnDestroy {
  books: Book[] = [];
  categories: Category[] = [];
  selectedCategoryId: number | null = null;
  loading = false;
  error = '';
  searchTerm = '';
  currentPage = 0;
  pageSize = 12;
  totalElements = 0;
  successMessage = '';
  private destroy$ = new Subject<void>();

  constructor(
    private bookService: BookService,
    private borrowService: BorrowService,
    private reservationService: ReservationService,
    private wishlistService: WishlistService,
    private categoryService: CategoryService,
    private route: ActivatedRoute,
    private router: Router
  ) {}

  ngOnInit(): void {
    const queryParams = this.route.snapshot.queryParams;
    this.searchTerm = queryParams['q'] || '';
    this.currentPage = Number(queryParams['page'] || 0);
    this.selectedCategoryId = queryParams['category'] ? Number(queryParams['category']) : null;

    this.categoryService.getCategories().pipe(takeUntil(this.destroy$)).subscribe({
      next: (cats) => this.categories = cats
    });

    this.loadFiltered(false);
  }

  ngOnDestroy(): void {
    this.destroy$.next();
    this.destroy$.complete();
  }

  loadFiltered(syncUrl: boolean = true): void {
    this.loading = true;
    this.error = '';

    let obs$;
    if (this.selectedCategoryId) {
      obs$ = this.bookService.getBooksByCategory(this.selectedCategoryId, this.currentPage, this.pageSize, this.searchTerm);
    } else if (this.searchTerm.trim()) {
      obs$ = this.bookService.searchByTitleOrAuthor(this.searchTerm, this.currentPage, this.pageSize);
    } else {
      obs$ = this.bookService.getBooks(this.currentPage, this.pageSize);
    }

    obs$.pipe(takeUntil(this.destroy$)).subscribe({
      next: (response) => {
        this.books = response.content || response;
        this.totalElements = response.totalElements || this.books.length;
        this.loading = false;
        if (syncUrl) {
          this.updateQueryParams();
        }
      },
      error: () => {
        this.error = 'Failed to load books';
        this.loading = false;
      }
    });
  }

  loadBooks(syncUrl: boolean = true): void {
    this.loadFiltered(syncUrl);
  }

  searchBooks(syncUrl: boolean = true): void {
    this.loadFiltered(syncUrl);
  }

  borrowBook(book: Book): void {
    this.borrowService.borrowBook(book.id)
      .pipe(takeUntil(this.destroy$))
      .subscribe({
        next: () => {
          this.successMessage = `Borrow demand submitted for "${book.title}". Waiting for librarian approval.`;
          this.loadBooks();
          setTimeout(() => this.successMessage = '', 3000);
        },
        error: (err) => {
          this.error = err.error?.message || 'Failed to submit borrow demand';
        }
      });
  }

  reserveBook(book: Book): void {
    this.reservationService.reserveBook(book.id)
      .pipe(takeUntil(this.destroy$))
      .subscribe({
        next: () => {
          this.successMessage = `Reservation created for "${book.title}"`;
          setTimeout(() => this.successMessage = '', 3000);
        },
        error: (err) => {
          this.error = err.error?.message || 'Failed to reserve book';
        }
      });
  }

  addToWishlist(book: Book): void {
    this.wishlistService.addToWishlist(book.id)
      .pipe(takeUntil(this.destroy$))
      .subscribe({
        next: () => {
          this.successMessage = `Added "${book.title}" to wishlist`;
          setTimeout(() => this.successMessage = '', 3000);
        },
        error: (err) => {
          this.error = err.error?.message || 'Failed to add book to wishlist';
        }
      });
  }

  goToPage(page: number): void {
    this.currentPage = page;
    this.loadFiltered();
  }

  onCategoryChange(): void {
    this.currentPage = 0;
    this.loadFiltered();
  }

  onSearchTriggered(): void {
    this.currentPage = 0;
    this.loadFiltered();
  }

  resetSearch(): void {
    this.searchTerm = '';
    this.selectedCategoryId = null;
    this.currentPage = 0;
    this.loadFiltered();
  }

  get totalPages(): number {
    return Math.ceil(this.totalElements / this.pageSize);
  }

  get pages(): number[] {
    const pages = [];
    for (let i = 0; i < this.totalPages; i++) {
      pages.push(i);
    }
    return pages;
  }

  getBookCoverLetter(title: string | undefined): string {
    if (!title?.trim()) {
      return 'B';
    }
    return title.trim().charAt(0).toUpperCase();
  }

  private updateQueryParams(): void {
    const queryParams: Record<string, any> = {
      page: this.currentPage || null,
      q: this.searchTerm.trim() || null,
      category: this.selectedCategoryId || null
    };

    this.router.navigate([], {
      relativeTo: this.route,
      queryParams,
      queryParamsHandling: 'merge'
    });
  }
}
