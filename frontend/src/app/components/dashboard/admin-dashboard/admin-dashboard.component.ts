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
      title: 'Reports and Analytics',
      description: 'Generate and review circulation reports.',
      route: '/reports',
      action: 'Open Reports'
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
    }
  ];
}
