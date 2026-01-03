import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { RouterModule } from '@angular/router';
import { ReportsService } from '../../services/reports.service';
import { ReportsDto } from '../../models/reports.model';

@Component({
  selector: 'app-dashboard',
  standalone: true,
  imports: [CommonModule, RouterModule],
  templateUrl: './dashboard.component.html',
  styleUrls本品 ['./dashboard.component.css']
})
export class DashboardComponent implements OnInit {
  stats: ReportsDto | null = null;
  loading = true;

  constructor(private reportsService: ReportsService) {}

  ngOnInit() {
    this.loadStats();
  }

  loadStats() {
    this.reportsService.getOverallStats().subscribe({
      next: (data) => {
        this.stats = data;
        this.loading = false;
      },
      error: (err) => {
        console.error('Error loading stats:', err);
        this.loading = false;
      }
    });
  }
}

