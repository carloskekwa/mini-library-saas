import { Component, OnInit, OnDestroy } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormBuilder, FormGroup, Validators, ReactiveFormsModule } from '@angular/forms';
import { ActivatedRoute, RouterModule } from '@angular/router';
import { BookService } from '../../../services/book.service';
import { ReviewService } from '../../../services/review.service';
import { BorrowService } from '../../../services/borrow.service';
import { ReservationService } from '../../../services/reservation.service';
import { WishlistService } from '../../../services/wishlist.service';
import { AuthService } from '../../../services/auth.service';
import { Book, Review } from '../../../models/index';
import { Subject } from 'rxjs';
import { takeUntil } from 'rxjs/operators';

@Component({
  selector: 'app-book-detail',
  standalone: true,
  imports: [CommonModule, ReactiveFormsModule, RouterModule],
  templateUrl: './book-detail.component.html',
  styleUrls: ['./book-detail.component.css']
})
export class BookDetailComponent implements OnInit, OnDestroy {
  book: Book | null = null;
  reviews: Review[] = [];
  averageRating: number = 0;
  reviewForm: FormGroup = new FormGroup({});
  loading = false;
  error = '';
  successMessage = '';
  submitted = false;
  Math = Math;
  private destroy$ = new Subject<void>();

  constructor(
    private route: ActivatedRoute,
    private bookService: BookService,
    private reviewService: ReviewService,
    private borrowService: BorrowService,
    private reservationService: ReservationService,
    private wishlistService: WishlistService,
    private authService: AuthService,
    private formBuilder: FormBuilder
  ) {}

  ngOnInit(): void {
    this.reviewForm = this.formBuilder.group({
      rating: [5, [Validators.required]],
      reviewText: ['', [Validators.maxLength(1000)]]
    });

    const id = this.route.snapshot.paramMap.get('id');
    if (id) {
      this.loadBook(Number(id));
      this.loadReviews(Number(id));
      this.loadAverageRating(Number(id));
    }
  }

  ngOnDestroy(): void {
    this.destroy$.next();
    this.destroy$.complete();
  }

  loadBook(id: number): void {
    this.loading = true;
    this.bookService.getBookById(id)
      .pipe(takeUntil(this.destroy$))
      .subscribe({
        next: (book) => {
          this.book = book;
          this.loading = false;
        },
        error: () => {
          this.error = 'Failed to load book';
          this.loading = false;
        }
      });
  }

  loadReviews(bookId: number): void {
    this.reviewService.getBookReviews(bookId)
      .pipe(takeUntil(this.destroy$))
      .subscribe({
        next: (response) => {
          this.reviews = response.content || response;
        },
        error: () => {
          this.error = 'Failed to load reviews';
        }
      });
  }

  loadAverageRating(bookId: number): void {
    this.reviewService.getAverageRating(bookId)
      .pipe(takeUntil(this.destroy$))
      .subscribe({
        next: (rating) => {
          this.averageRating = rating || 0;
        }
      });
  }

  borrowBook(): void {
    if (!this.book) return;
    this.borrowService.borrowBook(this.book.id)
      .pipe(takeUntil(this.destroy$))
      .subscribe({
        next: () => {
          this.successMessage = 'Book borrowed successfully!';
          setTimeout(() => this.successMessage = '', 3000);
        },
        error: (err) => {
          this.error = err.error?.message || 'Failed to borrow book';
        }
      });
  }

  reserveBook(): void {
    if (!this.book) return;
    this.reservationService.reserveBook(this.book.id)
      .pipe(takeUntil(this.destroy$))
      .subscribe({
        next: () => {
          this.successMessage = 'Book reserved successfully!';
          setTimeout(() => this.successMessage = '', 3000);
        },
        error: (err) => {
          this.error = err.error?.message || 'Failed to reserve book';
        }
      });
  }

  addToWishlist(): void {
    if (!this.book) return;
    this.wishlistService.addToWishlist(this.book.id)
      .pipe(takeUntil(this.destroy$))
      .subscribe({
        next: () => {
          this.successMessage = 'Added to wishlist successfully!';
          setTimeout(() => this.successMessage = '', 3000);
        },
        error: (err) => {
          this.error = err.error?.message || 'Failed to add to wishlist';
        }
      });
  }

  submitReview(): void {
    this.submitted = true;
    if (this.reviewForm.invalid || !this.book) {
      return;
    }

    const { rating, reviewText } = this.reviewForm.value;
    this.reviewService.createReview(this.book.id, rating, reviewText)
      .pipe(takeUntil(this.destroy$))
      .subscribe({
        next: () => {
          this.successMessage = 'Review submitted successfully!';
          this.reviewForm.reset({ rating: 5, reviewText: '' });
          this.submitted = false;
          this.loadReviews(this.book!.id);
          setTimeout(() => this.successMessage = '', 3000);
        },
        error: (err) => {
          this.error = err.error?.message || 'Failed to submit review';
        }
      });
  }

  deleteReview(review: Review): void {
    this.reviewService.deleteReview(review.id)
      .pipe(takeUntil(this.destroy$))
      .subscribe({
        next: () => {
          this.successMessage = 'Review deleted successfully!';
          this.loadReviews(this.book!.id);
          setTimeout(() => this.successMessage = '', 3000);
        },
        error: (err) => {
          this.error = err.error?.message || 'Failed to delete review';
        }
      });
  }

  canDeleteReview(review: Review): boolean {
    const currentUser = this.authService.getCurrentUser();
    if (!currentUser) {
      return false;
    }

    if (this.authService.hasRole('ADMIN')) {
      return true;
    }

    return currentUser.id === review.userId && this.authService.hasRole('MEMBER');
  }

  getBookCoverLetter(title: string | undefined): string {
    if (!title?.trim()) {
      return 'B';
    }
    return title.trim().charAt(0).toUpperCase();
  }

  get f() {
    return this.reviewForm.controls;
  }
}
