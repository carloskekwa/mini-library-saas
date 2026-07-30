import { Component, OnInit, OnDestroy } from '@angular/core';
import { CommonModule } from '@angular/common';
import { NotificationService } from '../../../services/notification.service';
import { Notification } from '../../../models/index';
import { Subject } from 'rxjs';
import { takeUntil } from 'rxjs/operators';

@Component({
  selector: 'app-notification-list',
  standalone: true,
  imports: [CommonModule],
  templateUrl: './notification-list.component.html',
  styleUrls: ['./notification-list.component.css']
})
export class NotificationListComponent implements OnInit, OnDestroy {
  notifications: Notification[] = [];
  loading = false;
  error = '';
  successMessage = '';
  private destroy$ = new Subject<void>();

  constructor(private notificationService: NotificationService) {}

  ngOnInit(): void {
    this.loadNotifications();
  }

  ngOnDestroy(): void {
    this.destroy$.next();
    this.destroy$.complete();
  }

  loadNotifications(): void {
    this.loading = true;
    this.notificationService.getUnreadNotifications()
      .pipe(takeUntil(this.destroy$))
      .subscribe({
        next: (response: Notification[] | { content?: Notification[] }) => {
          this.notifications = Array.isArray(response) ? response : (response.content || []);
          this.loading = false;
        },
        error: () => {
          this.error = 'Failed to load notifications';
          this.loading = false;
        }
      });
  }

  markAsRead(notification: Notification): void {
    this.notificationService.markAsRead(notification.id)
      .pipe(takeUntil(this.destroy$))
      .subscribe({
        next: () => {
          this.loadNotifications();
        }
      });
  }

  deleteNotification(notification: Notification): void {
    this.notificationService.deleteNotification(notification.id)
      .pipe(takeUntil(this.destroy$))
      .subscribe({
        next: () => {
          this.successMessage = 'Notification deleted';
          this.loadNotifications();
          setTimeout(() => this.successMessage = '', 3000);
        }
      });
  }

  markAllAsRead(): void {
    this.notificationService.markAllAsRead()
      .pipe(takeUntil(this.destroy$))
      .subscribe({
        next: () => {
          this.loadNotifications();
        }
      });
  }
}
