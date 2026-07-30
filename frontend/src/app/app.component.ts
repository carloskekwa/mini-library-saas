import { Component, OnInit, OnDestroy } from '@angular/core';
import { CommonModule } from '@angular/common';
import { RouterModule } from '@angular/router';
import { Router } from '@angular/router';
import { AuthService } from './services/auth.service';
import { NotificationService } from './services/notification.service';
import { Subject } from 'rxjs';
import { takeUntil } from 'rxjs/operators';

@Component({
  selector: 'app-root',
  standalone: true,
  imports: [CommonModule, RouterModule],
  templateUrl: './app.component.html',
  styleUrls: ['./app.component.css']
})
export class AppComponent implements OnInit, OnDestroy {
  title = 'Mini Library SaaS';
  isLoggedIn = false;
  currentUser: any = null;
  unreadNotificationCount = 0;
  private destroy$ = new Subject<void>();

  constructor(
    private authService: AuthService,
    private notificationService: NotificationService,
    private router: Router
  ) {}

  ngOnInit(): void {
    this.authService.currentUser$.pipe(takeUntil(this.destroy$)).subscribe(user => {
      this.currentUser = user;
      this.isLoggedIn = !!user;
      if (this.isLoggedIn) {
        this.updateNotificationCount();
      }
    });
  }

  ngOnDestroy(): void {
    this.destroy$.next();
    this.destroy$.complete();
  }

  updateNotificationCount(): void {
    this.notificationService.getUnreadCount().pipe(takeUntil(this.destroy$)).subscribe((response: any) => {
      this.unreadNotificationCount = Number(response?.unreadCount ?? response ?? 0);
    });
  }

  hasRole(role: string): boolean {
    return this.authService.hasRole(role);
  }

  hasAnyRole(roles: string[]): boolean {
    return this.authService.hasAnyRole(roles);
  }

  get isMember(): boolean {
    return this.hasRole('MEMBER');
  }

  get isLibrarian(): boolean {
    return this.hasRole('LIBRARIAN');
  }

  get isAdmin(): boolean {
    return this.hasRole('ADMIN');
  }

  get canSeeBooksMenu(): boolean {
    return this.isMember;
  }

  get canSeeNotificationsMenu(): boolean {
    return this.isMember;
  }

  get canSeeMyBorrowsMenu(): boolean {
    return this.isMember;
  }

  get canSeeReservationsMenu(): boolean {
    return this.isMember;
  }

  get canSeeWishlistMenu(): boolean {
    return this.isMember;
  }

  get canSeeBookRequestsMenu(): boolean {
    return this.isMember;
  }

  get canSeeManageCategoriesMenu(): boolean {
    return this.isAdmin || this.isLibrarian;
  }

  get canSeePenaltiesMenu(): boolean {
    return this.isAdmin;
  }

  get canSeeLibrarianInventoryMenu(): boolean {
    return this.isLibrarian;
  }

  get canSeeLibrarianReservationMenu(): boolean {
    return this.isLibrarian;
  }

  get canSeeAdminOnlyMenu(): boolean {
    return this.isAdmin;
  }

  get canSeeAdminExtraOperationsMenu(): boolean {
    return this.isAdmin;
  }

  get dashboardRoute(): string {
    return this.authService.getDefaultRouteForCurrentUser();
  }

  logout(): void {
    this.authService.logout();
    this.router.navigateByUrl('/login');
  }
}
