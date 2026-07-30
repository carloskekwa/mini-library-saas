import { Component } from '@angular/core';
import { CommonModule } from '@angular/common';
import { RouterModule } from '@angular/router';

@Component({
  selector: 'app-admin-dashboard',
  standalone: true,
  imports: [CommonModule, RouterModule],
  templateUrl: './admin-dashboard.component.html',
  styleUrls: ['./admin-dashboard.component.css']
})
export class AdminDashboardComponent {
  cards = [
    {
      title: 'Book Management',
      description: 'Create, update, and remove catalog entries.',
      route: '/manage/books',
      action: 'Open Manager'
    },
    {
      title: 'Category Management',
      description: 'Maintain category taxonomy used by catalog items.',
      route: '/manage/categories',
      action: 'Open Categories'
    },
    {
      title: 'Reports and Analytics',
      description: 'Generate and review circulation reports.',
      route: '/reports',
      action: 'Open Reports'
    },
    {
      title: 'Penalties',
      description: 'Create and lift member penalties.',
      route: '/admin/penalties',
      action: 'Open Penalties'
    },
    {
      title: 'Audit Logs',
      description: 'Inspect system audit trails.',
      route: '/admin/audit-logs',
      action: 'Open Audit'
    },
    {
      title: 'User Console',
      description: 'ID-based user moderation and investigation workflows.',
      route: '/admin/users',
      action: 'Open Console'
    },
    {
      title: 'System Configuration',
      description: 'Manage config, templates, and scheduler.',
      route: '/admin/config',
      action: 'Open Config'
    },
    {
      title: 'Batch Import',
      description: 'Create and control data import jobs.',
      route: '/operations/batch-import',
      action: 'Open Jobs'
    }
  ];
}
