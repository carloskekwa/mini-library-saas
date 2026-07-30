import { Component } from '@angular/core';
import { CommonModule } from '@angular/common';
import { RouterModule } from '@angular/router';

@Component({
  selector: 'app-librarian-dashboard',
  standalone: true,
  imports: [CommonModule, RouterModule],
  templateUrl: './librarian-dashboard.component.html',
  styleUrls: ['./librarian-dashboard.component.css']
})
export class LibrarianDashboardComponent {
  cards = [
    {
      title: 'Borrow Demand Queue',
      description: 'Approve pending borrow demands and prepare member pickup.',
      route: '/librarian/borrowed-inventory',
      action: 'Open Queue'
    },
    {
      title: 'Category Management',
      description: 'Maintain book categories and metadata grouping.',
      route: '/manage/categories',
      action: 'Open Categories'
    },
    {
      title: 'Reservations',
      description: 'Track active reservations and queue movement.',
      route: '/reservations',
      action: 'Open Reservations'
    },
    {
      title: 'Reports',
      description: 'Generate popular books and circulation reports.',
      route: '/reports',
      action: 'Open Reports'
    },
    {
      title: 'Batch Import',
      description: 'Create and monitor catalog import jobs.',
      route: '/operations/batch-import',
      action: 'Open Batch Jobs'
    }
  ];
}
