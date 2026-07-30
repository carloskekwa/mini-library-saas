import { Component, OnInit, OnDestroy } from '@angular/core';
import { CommonModule } from '@angular/common';
import { RouterModule, Router, NavigationEnd } from '@angular/router';
import { AuthService } from './services/auth.service';
import { NotificationService } from './services/notification.service';
import { Subject } from 'rxjs';
import { takeUntil, filter } from 'rxjs/operators';

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
  private navHistory: string[] = [];
  private readonly AUTH_ROUTES = ['/login', '/register'];

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

    this.router.events.pipe(
      filter(e => e instanceof NavigationEnd),
      takeUntil(this.destroy$)
    ).subscribe((e: any) => {
      const url: string = (e as NavigationEnd).urlAfterRedirects;
      if (!this.AUTH_ROUTES.some(r => url.startsWith(r))) {
        const last = this.navHistory[this.navHistory.length - 1];
        if (last !== url) {
          this.navHistory.push(url);
        }
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

  get canSeeStaffBookRequestsMenu(): boolean {
    return this.isAdmin || this.isLibrarian;
  }

  get canSeeManageCategoriesMenu(): boolean {
    return this.isAdmin || this.isLibrarian;
  }

  get canSeeManageBooksMenu(): boolean {
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

  get canSeeUserConsoleMenu(): boolean {
    return this.isLibrarian;
  }

  get canSeeAdminOnlyMenu(): boolean {
    return this.isAdmin;
  }

  get canSeeAdminExtraOperationsMenu(): boolean {
    return this.isAdmin;
  }

  get isOnDashboard(): boolean {
    const current = this.navHistory[this.navHistory.length - 1] ?? '';
    return current.startsWith('/dashboard');
  }

  get dashboardRoute(): string {
    return this.authService.getDefaultRouteForCurrentUser();
  }

  openProfile(): void {
    this.router.navigateByUrl('/profile');
  }

  goBack(): void {
    // Remove the current page from history
    this.navHistory.pop();
    const previous = this.navHistory[this.navHistory.length - 1];
    if (previous && !previous.startsWith('/dashboard')) {
      this.router.navigateByUrl(previous);
    } else {
      this.router.navigateByUrl(this.dashboardRoute);
    }
  }

  logout(): void {
    this.authService.logout();
    this.router.navigateByUrl('/login');
  }
}
