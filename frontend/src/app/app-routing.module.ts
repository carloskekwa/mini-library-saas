import { Routes } from '@angular/router';
import { AuthGuard } from './guards/auth.guard';

export const appRoutes: Routes = [
  {
    path: '',
    redirectTo: '/dashboard/member',
    pathMatch: 'full'
  },
  {
    path: 'login',
    loadComponent: () => import('./components/auth/login/login.component').then(m => m.LoginComponent)
  },
  {
    path: 'register',
    loadComponent: () => import('./components/auth/register/register.component').then(m => m.RegisterComponent)
  },
  {
    path: 'books',
    loadComponent: () => import('./components/books/book-list/book-list.component').then(m => m.BookListComponent),
    canActivate: [AuthGuard]
  },
  {
    path: 'books/:id',
    loadComponent: () => import('./components/books/book-detail/book-detail.component').then(m => m.BookDetailComponent),
    canActivate: [AuthGuard]
  },
  {
    path: 'manage/books',
    loadComponent: () => import('./components/books/book-management/book-management.component').then(m => m.BookManagementComponent),
    canActivate: [AuthGuard],
    data: { roles: ['ADMIN', 'LIBRARIAN'] }
  },
  {
    path: 'manage/categories',
    loadComponent: () => import('./components/categories/category-management.component').then(m => m.CategoryManagementComponent),
    canActivate: [AuthGuard],
    data: { roles: ['ADMIN', 'LIBRARIAN'] }
  },
  {
    path: 'librarian/borrowed-inventory',
    loadComponent: () => import('./components/librarian/borrowed-inventory/borrowed-inventory.component').then(m => m.BorrowedInventoryComponent),
    canActivate: [AuthGuard],
    data: { roles: ['LIBRARIAN', 'ADMIN'] }
  },
  {
    path: 'librarian/reservation-inventory',
    loadComponent: () => import('./components/librarian/reservation-inventory/reservation-inventory.component').then(m => m.ReservationInventoryComponent),
    canActivate: [AuthGuard],
    data: { roles: ['LIBRARIAN', 'ADMIN'] }
  },
  {
    path: 'reports',
    loadComponent: () => import('./components/reports/reports.component').then(m => m.ReportsComponent),
    canActivate: [AuthGuard],
    data: { roles: ['ADMIN', 'LIBRARIAN'] }
  },
  {
    path: 'admin/penalties',
    loadComponent: () => import('./components/admin/penalties/penalties.component').then(m => m.PenaltiesComponent),
    canActivate: [AuthGuard],
    data: { roles: ['ADMIN', 'LIBRARIAN'] }
  },
  {
    path: 'admin/users',
    loadComponent: () => import('./components/admin/user-console/user-console.component').then(m => m.UserConsoleComponent),
    canActivate: [AuthGuard],
    data: { roles: ['ADMIN', 'LIBRARIAN'] }
  },
  {
    path: 'admin/config',
    loadComponent: () => import('./components/admin/config/config.component').then(m => m.ConfigAdminComponent),
    canActivate: [AuthGuard],
    data: { roles: ['ADMIN'] }
  },
  {
    path: 'admin/email-templates',
    loadComponent: () => import('./components/admin/email-templates/email-templates.component').then(m => m.EmailTemplatesComponent),
    canActivate: [AuthGuard],
    data: { roles: ['ADMIN'] }
  },
  {
    path: 'admin/scheduled-tasks',
    loadComponent: () => import('./components/admin/scheduled-tasks/scheduled-tasks.component').then(m => m.ScheduledTasksComponent),
    canActivate: [AuthGuard],
    data: { roles: ['ADMIN'] }
  },
  {
    path: 'operations/batch-import',
    loadComponent: () => import('./components/admin/batch-import/batch-import.component').then(m => m.BatchImportComponent),
    canActivate: [AuthGuard],
    data: { roles: ['ADMIN', 'LIBRARIAN'] }
  },
  {
    path: 'borrows',
    loadComponent: () => import('./components/borrow/borrow-list/borrow-list.component').then(m => m.BorrowListComponent),
    canActivate: [AuthGuard]
  },
  {
    path: 'reservations',
    loadComponent: () => import('./components/reservations/reservation-list/reservation-list.component').then(m => m.ReservationListComponent),
    canActivate: [AuthGuard]
  },
  {
    path: 'notifications',
    loadComponent: () => import('./components/notifications/notification-list/notification-list.component').then(m => m.NotificationListComponent),
    canActivate: [AuthGuard]
  },
  {
    path: 'wishlist',
    loadComponent: () => import('./components/wishlist/wishlist.component').then(m => m.WishlistComponent),
    canActivate: [AuthGuard],
    data: { roles: ['MEMBER', 'LIBRARIAN', 'ADMIN'] }
  },
  {
    path: 'book-requests',
    loadComponent: () => import('./components/book-requests/book-requests.component').then(m => m.BookRequestsComponent),
    canActivate: [AuthGuard],
    data: { roles: ['MEMBER', 'LIBRARIAN', 'ADMIN'] }
  },
  {
    path: 'profile',
    loadComponent: () => import('./components/profile/profile.component').then(m => m.ProfileComponent),
    canActivate: [AuthGuard],
    data: { roles: ['MEMBER', 'LIBRARIAN', 'ADMIN'] }
  },
  {
    path: 'dashboard',
    redirectTo: '/dashboard/member',
    pathMatch: 'full'
  },
  {
    path: 'dashboard/member',
    loadComponent: () => import('./components/dashboard/dashboard.component').then(m => m.DashboardComponent),
    canActivate: [AuthGuard],
    data: { roles: ['MEMBER'] }
  },
  {
    path: 'dashboard/librarian',
    loadComponent: () => import('./components/dashboard/librarian-dashboard/librarian-dashboard.component').then(m => m.LibrarianDashboardComponent),
    canActivate: [AuthGuard],
    data: { roles: ['LIBRARIAN', 'ADMIN'] }
  },
  {
    path: 'dashboard/admin',
    loadComponent: () => import('./components/dashboard/admin-dashboard/admin-dashboard.component').then(m => m.AdminDashboardComponent),
    canActivate: [AuthGuard],
    data: { roles: ['ADMIN'] }
  },
  {
    path: '**',
    redirectTo: '/dashboard/member'
  }
];
