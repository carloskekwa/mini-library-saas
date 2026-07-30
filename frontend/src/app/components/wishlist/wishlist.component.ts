import { Component, OnDestroy, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { RouterModule } from '@angular/router';
import { WishlistService } from '../../services/wishlist.service';
import { WishlistItem } from '../../models/index';
import { Subject } from 'rxjs';
import { takeUntil } from 'rxjs/operators';

@Component({
  selector: 'app-wishlist',
  standalone: true,
  imports: [CommonModule, RouterModule],
  templateUrl: './wishlist.component.html',
  styleUrls: ['./wishlist.component.css']
})
export class WishlistComponent implements OnInit, OnDestroy {
  wishlist: WishlistItem[] = [];
  loading = false;
  error = '';
  successMessage = '';
  deletingId: number | null = null;

  private readonly destroy$ = new Subject<void>();

  constructor(private readonly wishlistService: WishlistService) {}

  ngOnInit(): void {
    this.loadWishlist();
  }

  ngOnDestroy(): void {
    this.destroy$.next();
    this.destroy$.complete();
  }

  loadWishlist(): void {
    this.loading = true;
    this.wishlistService.getWishlist()
      .pipe(takeUntil(this.destroy$))
      .subscribe({
        next: (response) => {
          this.wishlist = response.content || [];
          this.loading = false;
        },
        error: (err) => {
          this.error = err.error?.message || 'Failed to load wishlist';
          this.loading = false;
        }
      });
  }

  remove(item: WishlistItem): void {
    this.deletingId = item.id;
    this.error = '';

    this.wishlistService.removeFromWishlist(item.id)
      .pipe(takeUntil(this.destroy$))
      .subscribe({
        next: () => {
          this.deletingId = null;
          this.successMessage = 'Removed from wishlist';
          this.loadWishlist();
          setTimeout(() => this.successMessage = '', 3000);
        },
        error: (err) => {
          this.deletingId = null;
          this.error = err.error?.message || 'Failed to remove wishlist item';
        }
      });
  }

  getBookCoverLetter(title: string | undefined): string {
    if (!title?.trim()) {
      return 'B';
    }
    return title.trim().charAt(0).toUpperCase();
  }
}
