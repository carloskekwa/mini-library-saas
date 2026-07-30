import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { firstValueFrom } from 'rxjs';
import { ReportService } from '../../services/report.service';
import { AuthService } from '../../services/auth.service';
import { BookService } from '../../services/book.service';
import { BorrowService } from '../../services/borrow.service';
import { ReservationService } from '../../services/reservation.service';
import { UserAdminService } from '../../services/user-admin.service';
import { Book, BorrowRecord, PagedResponse, Report, Reservation, ReturnRecord, User } from '../../models/index';

interface MetricCard {
  label: string;
  value: string;
  detail: string;
  tone: 'blue' | 'green' | 'orange' | 'red';
}

interface BreakdownItem {
  label: string;
  value: number;
  percentage: number;
}

interface RankedItem {
  label: string;
  value: number;
  context?: string;
}

interface TrendItem {
  label: string;
  borrows: number;
  reservations: number;
  reports: number;
}

@Component({
  selector: 'app-reports',
  standalone: true,
  imports: [CommonModule],
  template: `
    <div class="reports-shell">
      <section class="hero">
        <div>
          <h1>Library Intelligence Console</h1>
          <p>Comprehensive statistics across inventory, circulation, reservations, user growth, and report activity.</p>
          <small *ngIf="lastRefreshedAt">Last refreshed: {{ lastRefreshedAt | date: 'medium' }}</small>
        </div>
        <button class="refresh-btn" (click)="refreshDashboard()" [disabled]="loading">
          {{ loading ? 'Refreshing...' : 'Refresh Statistics' }}
        </button>
      </section>

      <div *ngIf="error" class="banner banner-error">{{ error }}</div>
      <div *ngIf="warnings.length" class="banner banner-warning">
        <strong>Partial data:</strong> {{ warnings.join(' | ') }}
      </div>

      <section class="kpi-grid" *ngIf="!loading">
        <article class="kpi-card" [class]="'kpi-card ' + card.tone" *ngFor="let card of metricCards">
          <h3>{{ card.label }}</h3>
          <div class="kpi-value">{{ card.value }}</div>
          <small>{{ card.detail }}</small>
        </article>
      </section>

      <section class="panel-grid" *ngIf="!loading">
        <article class="panel">
          <h2>Inventory Health</h2>
          <div class="fact-row">
            <span>Physical copies</span>
            <strong>{{ totalCopies | number }}</strong>
          </div>
          <div class="fact-row">
            <span>Available now</span>
            <strong>{{ availableCopies | number }} ({{ toPercent(availableRate) }})</strong>
          </div>
          <div class="fact-row">
            <span>Borrowed out</span>
            <strong>{{ borrowedCopies | number }} ({{ toPercent(utilizationRate) }})</strong>
          </div>
          <div class="fact-row">
            <span>Low stock titles</span>
            <strong>{{ lowStockTitles | number }}</strong>
          </div>
          <div class="fact-row">
            <span>Out of stock titles</span>
            <strong>{{ outOfStockTitles | number }}</strong>
          </div>

          <h3>Category Mix</h3>
          <div class="bar-list" *ngIf="categoryBreakdown.length; else noCategoryData">
            <div class="bar-item" *ngFor="let item of categoryBreakdown">
              <div class="bar-head">
                <span>{{ item.label }}</span>
                <strong>{{ item.value }} ({{ toPercent(item.percentage) }})</strong>
              </div>
              <div class="bar-track"><div class="bar-fill" [style.width.%]="item.percentage * 100"></div></div>
            </div>
          </div>
          <ng-template #noCategoryData><p class="muted">No category data available.</p></ng-template>
        </article>

        <article class="panel">
          <h2>Circulation And Returns</h2>
          <div class="fact-row">
            <span>Borrow history entries</span>
            <strong>{{ borrowHistoryCount | number }}</strong>
          </div>
          <div class="fact-row">
            <span>Active borrows</span>
            <strong>{{ activeBorrowCount | number }}</strong>
          </div>
          <div class="fact-row">
            <span>Overdue borrows</span>
            <strong>{{ overdueCount | number }} ({{ toPercent(overdueRate) }})</strong>
          </div>
          <div class="fact-row">
            <span>Average loan length (returned)</span>
            <strong>{{ avgLoanDurationDays | number: '1.1-1' }} days</strong>
          </div>
          <div class="fact-row">
            <span>Total renewals</span>
            <strong>{{ totalRenewals | number }}</strong>
          </div>
          <div class="fact-row">
            <span>Late returns</span>
            <strong>{{ lateReturns | number }} / {{ returnCount | number }}</strong>
          </div>

          <h3>Top Borrowed Books</h3>
          <ol class="ranked-list" *ngIf="topBorrowedBooks.length; else noBorrowData">
            <li *ngFor="let row of topBorrowedBooks">
              <span>{{ row.label }}</span>
              <strong>{{ row.value }}</strong>
            </li>
          </ol>

          <h3>Most Borrowed Categories</h3>
          <ol class="ranked-list" *ngIf="topBorrowedCategories.length; else noCategoryBorrowData">
            <li *ngFor="let row of topBorrowedCategories">
              <span>{{ row.label }}</span>
              <strong>{{ row.value }} ({{ row.context }})</strong>
            </li>
          </ol>

          <ng-template #noCategoryBorrowData><p class="muted">No borrow-category mapping available.</p></ng-template>
          <ng-template #noBorrowData><p class="muted">No borrowing data available.</p></ng-template>
        </article>

        <article class="panel">
          <h2>Reservation Pressure</h2>
          <div class="fact-row">
            <span>Total reservations</span>
            <strong>{{ reservationCount | number }}</strong>
          </div>
          <div class="fact-row">
            <span>Active reservations</span>
            <strong>{{ activeReservationCount | number }}</strong>
          </div>
          <div class="fact-row">
            <span>Expiring within 48h</span>
            <strong>{{ expiringReservations | number }}</strong>
          </div>
          <div class="fact-row">
            <span>Average queue position</span>
            <strong>{{ averageQueuePosition | number: '1.1-1' }}</strong>
          </div>
          <div class="fact-row">
            <span>Longest queue observed</span>
            <strong>{{ maxQueuePosition | number }}</strong>
          </div>

          <h3>Reservation Status</h3>
          <div class="bar-list" *ngIf="reservationStatusBreakdown.length; else noReservationStatus">
            <div class="bar-item" *ngFor="let item of reservationStatusBreakdown">
              <div class="bar-head">
                <span>{{ item.label }}</span>
                <strong>{{ item.value }} ({{ toPercent(item.percentage) }})</strong>
              </div>
              <div class="bar-track"><div class="bar-fill alt" [style.width.%]="item.percentage * 100"></div></div>
            </div>
          </div>
          <ng-template #noReservationStatus><p class="muted">No reservation status data available.</p></ng-template>
        </article>

        <article class="panel">
          <h2>Fines And Compliance</h2>
          <div class="fact-row">
            <span>Total fine amount issued</span>
            <strong>{{ totalFineIssued | currency }}</strong>
          </div>
          <div class="fact-row">
            <span>Paid fine amount</span>
            <strong>{{ totalFinePaid | currency }}</strong>
          </div>
          <div class="fact-row">
            <span>Unpaid fine amount</span>
            <strong>{{ totalFineUnpaid | currency }}</strong>
          </div>
          <div class="fact-row">
            <span>Unpaid records</span>
            <strong>{{ unpaidFineRecords | number }}</strong>
          </div>
          <div class="fact-row">
            <span>Collection efficiency</span>
            <strong>{{ toPercent(fineCollectionRate) }}</strong>
          </div>
          <div class="fact-row" *ngIf="unpaidFineApiTotal > 0">
            <span>API unpaid total</span>
            <strong>{{ unpaidFineApiTotal | currency }}</strong>
          </div>

          <h3>Data Completeness</h3>
          <ol class="ranked-list">
            <li>
              <span>Books missing ISBN</span>
              <strong>{{ missingIsbnBooks | number }}</strong>
            </li>
            <li>
              <span>Books missing description</span>
              <strong>{{ missingDescriptionBooks | number }}</strong>
            </li>
            <li>
              <span>Books missing cover image</span>
              <strong>{{ missingCoverBooks | number }}</strong>
            </li>
          </ol>
        </article>

        <article class="panel">
          <h2>User And Role Distribution</h2>
          <p class="muted" *ngIf="!userStatsAvailable">User-level metrics are only visible for admin accounts.</p>
          <div *ngIf="userStatsAvailable">
            <div class="fact-row">
              <span>Total users</span>
              <strong>{{ userCount | number }}</strong>
            </div>
            <div class="fact-row">
              <span>New users in 30 days</span>
              <strong>{{ newUsers30d | number }}</strong>
            </div>
            <h3>Status</h3>
            <div class="bar-list" *ngIf="userStatusBreakdown.length">
              <div class="bar-item" *ngFor="let item of userStatusBreakdown">
                <div class="bar-head">
                  <span>{{ item.label }}</span>
                  <strong>{{ item.value }} ({{ toPercent(item.percentage) }})</strong>
                </div>
                <div class="bar-track"><div class="bar-fill" [style.width.%]="item.percentage * 100"></div></div>
              </div>
            </div>

            <h3>Roles</h3>
            <ol class="ranked-list" *ngIf="roleBreakdown.length">
              <li *ngFor="let item of roleBreakdown">
                <span>{{ item.label }}</span>
                <strong>{{ item.value }}</strong>
              </li>
            </ol>
          </div>
        </article>

        <article class="panel">
          <h2>Report Generation Analytics</h2>
          <div class="fact-row">
            <span>Total reports generated</span>
            <strong>{{ reportCount | number }}</strong>
          </div>
          <div class="fact-row">
            <span>Generated in last 30 days</span>
            <strong>{{ reportsLast30Days | number }}</strong>
          </div>
          <div class="fact-row">
            <span>Average report window</span>
            <strong>{{ averageReportWindowDays | number: '1.1-1' }} days</strong>
          </div>
          <div class="fact-row">
            <span>Largest window observed</span>
            <strong>{{ maxReportWindowDays | number: '1.1-1' }} days</strong>
          </div>

          <h3>Report Type Mix</h3>
          <div class="bar-list" *ngIf="reportTypeBreakdown.length; else noReportTypes">
            <div class="bar-item" *ngFor="let item of reportTypeBreakdown">
              <div class="bar-head">
                <span>{{ item.label }}</span>
                <strong>{{ item.value }} ({{ toPercent(item.percentage) }})</strong>
              </div>
              <div class="bar-track"><div class="bar-fill alt" [style.width.%]="item.percentage * 100"></div></div>
            </div>
          </div>
          <ng-template #noReportTypes><p class="muted">No report activity available.</p></ng-template>
        </article>
      </section>

      <section class="panel" *ngIf="!loading">
        <h2>6-Month Activity Trend</h2>
        <div class="trend-grid" *ngIf="trend.length; else noTrendData">
          <div class="trend-month" *ngFor="let row of trend">
            <h4>{{ row.label }}</h4>
            <div class="trend-bar-wrap">
              <div class="trend-bar borrows" [style.height.%]="trendPercent(row.borrows)"></div>
              <div class="trend-bar reservations" [style.height.%]="trendPercent(row.reservations)"></div>
              <div class="trend-bar reports" [style.height.%]="trendPercent(row.reports)"></div>
            </div>
            <small>{{ row.borrows }} B · {{ row.reservations }} R · {{ row.reports }} P</small>
          </div>
        </div>
        <ng-template #noTrendData><p class="muted">Not enough date-stamped activity to build trend data.</p></ng-template>
      </section>

      <section class="loading" *ngIf="loading">
        <div class="loader"></div>
        <p>Compiling detailed statistics...</p>
      </section>
    </div>
  `,
  styles: [
    `
      .reports-shell {
        font-family: 'Manrope', 'Segoe UI', sans-serif;
        background: linear-gradient(180deg, #f5fafb 0%, #f0f4fa 45%, #ffffff 100%);
        min-height: calc(100vh - 110px);
        padding: 1.5rem;
      }

      .hero {
        display: flex;
        justify-content: space-between;
        align-items: center;
        flex-wrap: wrap;
        gap: 1rem;
        padding: 1.25rem;
        border-radius: 16px;
        background: radial-gradient(circle at top right, #d4f4eb 0%, #e5eefc 45%, #ffffff 100%);
        border: 1px solid #d9e4f1;
        box-shadow: 0 12px 28px rgba(32, 68, 104, 0.08);
      }

      .hero h1 {
        margin: 0;
        color: #143352;
        font-weight: 800;
        letter-spacing: 0.01em;
      }

      .hero p {
        margin: 0.4rem 0;
        color: #36536f;
      }

      .hero small {
        color: #557089;
      }

      .refresh-btn {
        border: 0;
        border-radius: 999px;
        padding: 0.75rem 1.2rem;
        font-weight: 700;
        color: #fff;
        background: linear-gradient(120deg, #0f766e, #0ea5a4);
      }

      .refresh-btn:disabled {
        opacity: 0.7;
      }

      .banner {
        margin-top: 1rem;
        border-radius: 10px;
        padding: 0.75rem 1rem;
        font-size: 0.95rem;
      }

      .banner-error {
        background: #fee2e2;
        color: #7f1d1d;
      }

      .banner-warning {
        background: #fff7ed;
        color: #9a3412;
      }

      .kpi-grid {
        margin-top: 1rem;
        display: grid;
        grid-template-columns: repeat(auto-fit, minmax(190px, 1fr));
        gap: 0.8rem;
      }

      .kpi-card {
        border-radius: 14px;
        padding: 0.95rem;
        border: 1px solid;
        box-shadow: 0 10px 20px rgba(37, 65, 92, 0.07);
      }

      .kpi-card h3 {
        margin: 0;
        font-size: 0.82rem;
        text-transform: uppercase;
        letter-spacing: 0.08em;
      }

      .kpi-value {
        margin-top: 0.4rem;
        font-size: 1.5rem;
        font-weight: 800;
      }

      .kpi-card small {
        color: #38526b;
      }

      .kpi-card.blue {
        background: #eaf3ff;
        border-color: #bfd9ff;
        color: #1f4b7a;
      }

      .kpi-card.green {
        background: #e8fbf4;
        border-color: #baf0d7;
        color: #0f6b4f;
      }

      .kpi-card.orange {
        background: #fff4e8;
        border-color: #ffd7ac;
        color: #9a5311;
      }

      .kpi-card.red {
        background: #ffeef0;
        border-color: #f9c5cd;
        color: #9b1c2b;
      }

      .panel-grid {
        margin-top: 1rem;
        display: grid;
        gap: 0.9rem;
        grid-template-columns: repeat(auto-fit, minmax(300px, 1fr));
      }

      .panel {
        background: #ffffff;
        border: 1px solid #dbe7f1;
        border-radius: 14px;
        padding: 1rem;
        box-shadow: 0 8px 18px rgba(19, 48, 78, 0.07);
      }

      .panel h2 {
        margin: 0 0 0.75rem;
        font-size: 1.05rem;
        color: #183a5a;
      }

      .panel h3 {
        margin: 0.9rem 0 0.55rem;
        color: #28547a;
        font-size: 0.92rem;
      }

      .fact-row {
        display: flex;
        justify-content: space-between;
        gap: 0.75rem;
        border-bottom: 1px dashed #e7eef6;
        padding: 0.4rem 0;
      }

      .fact-row span {
        color: #3a556f;
      }

      .fact-row strong {
        color: #102d4a;
      }

      .muted {
        color: #60758a;
        margin: 0.5rem 0;
      }

      .bar-list {
        display: flex;
        flex-direction: column;
        gap: 0.45rem;
      }

      .bar-head {
        display: flex;
        justify-content: space-between;
        gap: 0.75rem;
        font-size: 0.86rem;
      }

      .bar-track {
        background: #edf3f9;
        border-radius: 999px;
        height: 7px;
        overflow: hidden;
      }

      .bar-fill {
        height: 100%;
        border-radius: 999px;
        background: linear-gradient(90deg, #0ea5a4, #15803d);
      }

      .bar-fill.alt {
        background: linear-gradient(90deg, #0ea5e9, #2563eb);
      }

      .ranked-list {
        margin: 0;
        padding-left: 1.1rem;
      }

      .ranked-list li {
        display: flex;
        justify-content: space-between;
        gap: 0.8rem;
        border-bottom: 1px solid #eff4f8;
        padding: 0.32rem 0;
      }

      .trend-grid {
        display: grid;
        grid-template-columns: repeat(auto-fit, minmax(100px, 1fr));
        gap: 0.65rem;
      }

      .trend-month {
        border: 1px solid #e1ebf5;
        border-radius: 10px;
        padding: 0.6rem;
        text-align: center;
      }

      .trend-month h4 {
        margin: 0 0 0.5rem;
        font-size: 0.86rem;
        color: #274968;
      }

      .trend-bar-wrap {
        height: 88px;
        display: flex;
        align-items: flex-end;
        justify-content: center;
        gap: 0.22rem;
      }

      .trend-bar {
        width: 12px;
        min-height: 2px;
        border-radius: 3px 3px 0 0;
      }

      .trend-bar.borrows { background: #0ea5a4; }
      .trend-bar.reservations { background: #f59e0b; }
      .trend-bar.reports { background: #3b82f6; }

      .trend-month small {
        color: #4f6b83;
      }

      .loading {
        margin-top: 1.2rem;
        padding: 1.2rem;
        display: flex;
        align-items: center;
        gap: 0.8rem;
        justify-content: center;
      }

      .loader {
        width: 22px;
        height: 22px;
        border: 3px solid #d1e8ed;
        border-top-color: #0f766e;
        border-radius: 50%;
        animation: spin 0.75s linear infinite;
      }

      @keyframes spin {
        to { transform: rotate(360deg); }
      }

      @media (max-width: 800px) {
        .reports-shell {
          padding: 1rem;
        }
      }
    `
  ]
})
export class ReportsComponent implements OnInit {
  loading = false;
  error = '';
  warnings: string[] = [];
  lastRefreshedAt = '';

  metricCards: MetricCard[] = [];
  categoryBreakdown: BreakdownItem[] = [];
  reservationStatusBreakdown: BreakdownItem[] = [];
  reportTypeBreakdown: BreakdownItem[] = [];
  userStatusBreakdown: BreakdownItem[] = [];
  roleBreakdown: RankedItem[] = [];
  topBorrowedBooks: RankedItem[] = [];
  topBorrowedCategories: RankedItem[] = [];
  trend: TrendItem[] = [];

  totalCopies = 0;
  availableCopies = 0;
  borrowedCopies = 0;
  availableRate = 0;
  utilizationRate = 0;
  lowStockTitles = 0;
  outOfStockTitles = 0;

  borrowHistoryCount = 0;
  activeBorrowCount = 0;
  overdueCount = 0;
  overdueRate = 0;
  avgLoanDurationDays = 0;
  totalRenewals = 0;
  lateReturns = 0;
  returnCount = 0;

  reservationCount = 0;
  activeReservationCount = 0;
  expiringReservations = 0;
  averageQueuePosition = 0;
  maxQueuePosition = 0;

  totalFineIssued = 0;
  totalFinePaid = 0;
  totalFineUnpaid = 0;
  unpaidFineRecords = 0;
  unpaidFineApiTotal = 0;
  fineCollectionRate = 0;

  userStatsAvailable = false;
  userCount = 0;
  newUsers30d = 0;

  reportCount = 0;
  reportsLast30Days = 0;
  averageReportWindowDays = 0;
  maxReportWindowDays = 0;

  missingIsbnBooks = 0;
  missingDescriptionBooks = 0;
  missingCoverBooks = 0;

  constructor(
    private readonly reportService: ReportService,
    private readonly authService: AuthService,
    private readonly bookService: BookService,
    private readonly borrowService: BorrowService,
    private readonly reservationService: ReservationService,
    private readonly userAdminService: UserAdminService
  ) {}

  ngOnInit(): void {
    void this.refreshDashboard();
  }

  async refreshDashboard(): Promise<void> {
    this.loading = true;
    this.error = '';
    this.warnings = [];

    try {
      const [books, borrowHistory, activeBorrows, overdueRecords, borrowReturns, reservations, activeReservations, reports, users, unpaidFineTotal] = await Promise.all([
        this.safeLoad('Books', () => this.loadAllPages<Book>((page, size) => this.bookService.getBooks(page, size)), []),
        this.safeLoad('Borrow history', () => this.loadAllPages<BorrowRecord>((page, size) => this.borrowService.getAllBorrowHistory(page, size)), []),
        this.safeLoad('Active borrows', () => firstValueFrom(this.borrowService.getActiveBorrows()), []),
        this.safeLoad('Overdue records', () => firstValueFrom(this.borrowService.getOverdueRecords()), []),
        this.safeLoad('Return records', () => this.loadAllPages<ReturnRecord>((page, size) => this.borrowService.getAllReturnRecords(page, size)), []),
        this.safeLoad('Reservations', () => this.loadAllPages<Reservation>((page, size) => this.reservationService.getAllReservations(page, size)), []),
        this.safeLoad('Active reservations', () => firstValueFrom(this.reservationService.getActiveReservations()), []),
        this.safeLoad('Report logs', () => this.loadAllPages<Report>((page, size) => this.reportService.getReports(page, size)), []),
        this.safeLoad('User metrics', async () => {
          if (!this.authService.hasRole('ADMIN')) {
            return [] as User[];
          }
          return this.loadAllPages<User>((page, size) => this.userAdminService.getUsers({ page, pageSize: size }));
        }, []),
        this.safeLoad('Unpaid fine summary', async () => {
          const payload = await firstValueFrom(this.borrowService.getTotalUnpaidFines());
          return Number(payload?.totalUnpaidFines ?? payload?.total ?? payload?.amount ?? 0);
        }, 0)
      ]);

      this.applyAnalytics({
        books,
        borrowHistory,
        activeBorrows,
        overdueRecords,
        borrowReturns,
        reservations,
        activeReservations,
        reports,
        users,
        unpaidFineTotal
      });

      this.lastRefreshedAt = new Date().toISOString();
    } catch (err: any) {
      this.error = err?.error?.message || err?.message || 'Failed to build statistics dashboard';
    } finally {
      this.loading = false;
    }
  }

  toPercent(value: number): string {
    return `${(Math.max(0, value) * 100).toFixed(1)}%`;
  }

  trendPercent(value: number): number {
    const max = Math.max(...this.trend.map(item => Math.max(item.borrows, item.reservations, item.reports)), 1);
    return (value / max) * 100;
  }

  private async safeLoad<T>(label: string, loader: () => Promise<T>, fallback: T): Promise<T> {
    try {
      return await loader();
    } catch (err: any) {
      if (err?.status === 403) {
        this.warnings.push(`${label}: access denied`);
      } else {
        this.warnings.push(`${label}: unavailable`);
      }
      return fallback;
    }
  }

  private async loadAllPages<T>(fetchPage: (page: number, pageSize: number) => PromiseLike<PagedResponse<T>> | any): Promise<T[]> {
    const allItems: T[] = [];
    const pageSize = 200;
    let page = 0;
    let totalElements = Number.POSITIVE_INFINITY;
    let safety = 0;

    while (allItems.length < totalElements && safety < 100) {
      const response: PagedResponse<T> = await firstValueFrom(fetchPage(page, pageSize));
      const content = response?.content || [];
      totalElements = Number.isFinite(response?.totalElements) ? Number(response.totalElements) : totalElements;
      allItems.push(...content);

      if (content.length < pageSize) {
        break;
      }

      page += 1;
      safety += 1;
    }

    return Number.isFinite(totalElements) ? allItems.slice(0, totalElements) : allItems;
  }

  private applyAnalytics(data: {
    books: Book[];
    borrowHistory: BorrowRecord[];
    activeBorrows: BorrowRecord[];
    overdueRecords: BorrowRecord[];
    borrowReturns: ReturnRecord[];
    reservations: Reservation[];
    activeReservations: Reservation[];
    reports: Report[];
    users: User[];
    unpaidFineTotal: number;
  }): void {
    const now = new Date();
    const bookCategoryById = data.books.reduce((acc, item) => {
      acc[item.id] = item.category?.name?.trim() || 'Uncategorized';
      return acc;
    }, {} as Record<number, string>);

    this.totalCopies = data.books.reduce((sum, item) => sum + Number(item.totalCopies || 0), 0);
    this.availableCopies = data.books.reduce((sum, item) => sum + Number(item.availableCopies || 0), 0);
    this.borrowedCopies = Math.max(this.totalCopies - this.availableCopies, 0);
    this.availableRate = this.rate(this.availableCopies, this.totalCopies);
    this.utilizationRate = this.rate(this.borrowedCopies, this.totalCopies);
    this.lowStockTitles = data.books.filter(item => Number(item.availableCopies || 0) > 0 && Number(item.availableCopies || 0) <= 2).length;
    this.outOfStockTitles = data.books.filter(item => Number(item.availableCopies || 0) === 0).length;

    this.borrowHistoryCount = data.borrowHistory.length;
    this.activeBorrowCount = data.activeBorrows.length;
    this.overdueCount = data.overdueRecords.length;
    this.overdueRate = this.rate(this.overdueCount, this.activeBorrowCount);
    this.avgLoanDurationDays = this.average(
      data.borrowHistory
        .filter(item => !!item.returnDate)
        .map(item => this.diffDays(item.borrowDate, item.returnDate))
        .filter(days => days >= 0)
    );
    this.totalRenewals = data.borrowHistory.reduce((sum, item) => sum + Number(item.renewalCount || 0), 0);

    this.returnCount = data.borrowReturns.length;
    this.lateReturns = data.borrowReturns.filter(item => Number(item.daysLate || 0) > 0).length;
    this.totalFineIssued = data.borrowReturns.reduce((sum, item) => sum + Number(item.fineAmount || 0), 0);
    this.totalFineUnpaid = data.borrowReturns
      .filter(item => Number(item.fineAmount || 0) > 0 && !item.finePaid)
      .reduce((sum, item) => sum + Number(item.fineAmount || 0), 0);
    this.totalFinePaid = Math.max(this.totalFineIssued - this.totalFineUnpaid, 0);
    this.unpaidFineRecords = data.borrowReturns.filter(item => Number(item.fineAmount || 0) > 0 && !item.finePaid).length;
    this.unpaidFineApiTotal = Number(data.unpaidFineTotal || 0);
    this.fineCollectionRate = this.rate(this.totalFinePaid, this.totalFineIssued);

    this.reservationCount = data.reservations.length;
    this.activeReservationCount = data.activeReservations.length;
    this.expiringReservations = data.activeReservations.filter(item => {
      const expiry = this.safeDate(item.expiryDate);
      if (!expiry) {
        return false;
      }
      const diffHours = (expiry.getTime() - now.getTime()) / (1000 * 60 * 60);
      return diffHours >= 0 && diffHours <= 48;
    }).length;

    const queuePositions = data.reservations.map(item => Number(item.positionInQueue || 0)).filter(item => item > 0);
    this.averageQueuePosition = this.average(queuePositions);
    this.maxQueuePosition = queuePositions.length ? Math.max(...queuePositions) : 0;

    this.reportCount = data.reports.length;
    this.reportsLast30Days = data.reports.filter(item => this.withinDays(item.generatedDate, 30)).length;
    const reportWindows = data.reports
      .map(item => this.diffDays(item.startDate, item.endDate))
      .filter(days => days >= 0);
    this.averageReportWindowDays = this.average(reportWindows);
    this.maxReportWindowDays = reportWindows.length ? Math.max(...reportWindows) : 0;

    this.userStatsAvailable = this.authService.hasRole('ADMIN');
    this.userCount = data.users.length;
    this.newUsers30d = data.users.filter(item => this.withinDays(item.createdAt, 30)).length;

    this.missingIsbnBooks = data.books.filter(item => !item.isbn?.trim()).length;
    this.missingDescriptionBooks = data.books.filter(item => !item.description?.trim()).length;
    this.missingCoverBooks = data.books.filter(item => !item.coverImageUrl?.trim()).length;

    this.categoryBreakdown = this.toBreakdown(
      this.countBy(data.books, item => item.category?.name || 'Uncategorized'),
      data.books.length,
      7
    );

    this.reservationStatusBreakdown = this.toBreakdown(
      this.countBy(data.reservations, item => item.status || 'UNKNOWN'),
      data.reservations.length,
      7
    );

    this.reportTypeBreakdown = this.toBreakdown(
      this.countBy(data.reports, item => item.type || 'UNKNOWN'),
      data.reports.length,
      7
    );

    this.userStatusBreakdown = this.toBreakdown(
      this.countBy(data.users, item => item.status || 'UNKNOWN'),
      data.users.length,
      5
    );

    this.roleBreakdown = this.toRanked(
      this.countByMany(data.users, item => item.roles || []),
      5
    );

    this.topBorrowedBooks = this.toRanked(
      this.countBy(data.borrowHistory, item => item.bookTitle || `Book #${item.bookId}`),
      8
    );

    const categoryBorrowCounts = this.countBy(data.borrowHistory, item => {
      const nestedCategory = item.book?.category?.name?.trim();
      if (nestedCategory) {
        return nestedCategory;
      }
      return bookCategoryById[item.bookId] || 'Uncategorized';
    });

    this.topBorrowedCategories = this.toRankedWithContext(
      categoryBorrowCounts,
      8,
      data.borrowHistory.length
    );

    this.metricCards = [
      {
        label: 'Inventory Utilization',
        value: this.toPercent(this.utilizationRate),
        detail: `${this.borrowedCopies.toLocaleString()} of ${this.totalCopies.toLocaleString()} copies borrowed`,
        tone: this.utilizationRate > 0.8 ? 'orange' : 'green'
      },
      {
        label: 'Active Borrows',
        value: this.activeBorrowCount.toLocaleString(),
        detail: `${this.overdueCount.toLocaleString()} currently overdue`,
        tone: this.overdueCount > 0 ? 'red' : 'blue'
      },
      {
        label: 'Reservation Load',
        value: this.activeReservationCount.toLocaleString(),
        detail: `${this.expiringReservations.toLocaleString()} expire within 48h`,
        tone: this.activeReservationCount > 0 ? 'orange' : 'green'
      },
      {
        label: 'Fine Collection Rate',
        value: this.toPercent(this.fineCollectionRate),
        detail: `${this.totalFineUnpaid.toLocaleString(undefined, { maximumFractionDigits: 2 })} still unpaid`,
        tone: this.fineCollectionRate < 0.7 ? 'red' : 'blue'
      },
      {
        label: 'Reports This Month',
        value: this.reportsLast30Days.toLocaleString(),
        detail: `${this.reportCount.toLocaleString()} total report entries`,
        tone: 'blue'
      },
      {
        label: 'Catalog Completeness',
        value: this.toPercent(this.rate(this.totalCopies - this.missingDescriptionBooks, this.totalCopies || 1)),
        detail: `${this.missingDescriptionBooks.toLocaleString()} books missing descriptions`,
        tone: this.missingDescriptionBooks > 0 ? 'orange' : 'green'
      }
    ];

    this.trend = this.buildTrend(data.borrowHistory, data.reservations, data.reports);
  }

  private buildTrend(borrows: BorrowRecord[], reservations: Reservation[], reports: Report[]): TrendItem[] {
    const monthKeys: string[] = [];
    const labels = new Map<string, string>();

    for (let i = 5; i >= 0; i -= 1) {
      const date = new Date();
      date.setDate(1);
      date.setHours(0, 0, 0, 0);
      date.setMonth(date.getMonth() - i);
      const key = `${date.getFullYear()}-${String(date.getMonth() + 1).padStart(2, '0')}`;
      monthKeys.push(key);
      labels.set(key, date.toLocaleString('en-US', { month: 'short' }));
    }

    const seed = monthKeys.reduce((acc, key) => {
      acc[key] = { borrows: 0, reservations: 0, reports: 0 };
      return acc;
    }, {} as Record<string, { borrows: number; reservations: number; reports: number }>);

    borrows.forEach(item => {
      const key = this.monthKey(item.borrowDate);
      if (key && seed[key]) {
        seed[key].borrows += 1;
      }
    });

    reservations.forEach(item => {
      const key = this.monthKey(item.reservationDate);
      if (key && seed[key]) {
        seed[key].reservations += 1;
      }
    });

    reports.forEach(item => {
      const key = this.monthKey(item.generatedDate);
      if (key && seed[key]) {
        seed[key].reports += 1;
      }
    });

    return monthKeys.map(key => ({
      label: labels.get(key) || key,
      borrows: seed[key].borrows,
      reservations: seed[key].reservations,
      reports: seed[key].reports
    }));
  }

  private monthKey(value?: string): string | null {
    const date = this.safeDate(value);
    if (!date) {
      return null;
    }
    return `${date.getFullYear()}-${String(date.getMonth() + 1).padStart(2, '0')}`;
  }

  private withinDays(value: string | undefined, days: number): boolean {
    const date = this.safeDate(value);
    if (!date) {
      return false;
    }
    const diff = Date.now() - date.getTime();
    return diff >= 0 && diff <= days * 24 * 60 * 60 * 1000;
  }

  private diffDays(startValue?: string, endValue?: string): number {
    const start = this.safeDate(startValue);
    const end = this.safeDate(endValue);
    if (!start || !end) {
      return -1;
    }
    return (end.getTime() - start.getTime()) / (1000 * 60 * 60 * 24);
  }

  private safeDate(value?: string): Date | null {
    if (!value) {
      return null;
    }
    const date = new Date(value);
    return Number.isNaN(date.getTime()) ? null : date;
  }

  private average(values: number[]): number {
    if (!values.length) {
      return 0;
    }
    return values.reduce((sum, value) => sum + value, 0) / values.length;
  }

  private rate(numerator: number, denominator: number): number {
    if (!denominator) {
      return 0;
    }
    return numerator / denominator;
  }

  private countBy<T>(rows: T[], keySelector: (value: T) => string): Record<string, number> {
    return rows.reduce((acc, row) => {
      const key = keySelector(row).trim();
      const label = key || 'Unknown';
      acc[label] = (acc[label] || 0) + 1;
      return acc;
    }, {} as Record<string, number>);
  }

  private countByMany<T>(rows: T[], keySelector: (value: T) => string[]): Record<string, number> {
    return rows.reduce((acc, row) => {
      const keys = keySelector(row);
      if (!keys.length) {
        acc['Unknown'] = (acc['Unknown'] || 0) + 1;
        return acc;
      }
      keys.forEach(key => {
        const label = (key || 'Unknown').trim() || 'Unknown';
        acc[label] = (acc[label] || 0) + 1;
      });
      return acc;
    }, {} as Record<string, number>);
  }

  private toBreakdown(map: Record<string, number>, total: number, limit: number): BreakdownItem[] {
    if (!total) {
      return [];
    }
    return Object.entries(map)
      .sort((a, b) => b[1] - a[1])
      .slice(0, limit)
      .map(([label, value]) => ({
        label,
        value,
        percentage: this.rate(value, total)
      }));
  }

  private toRanked(map: Record<string, number>, limit: number): RankedItem[] {
    return Object.entries(map)
      .sort((a, b) => b[1] - a[1])
      .slice(0, limit)
      .map(([label, value]) => ({ label, value }));
  }

  private toRankedWithContext(map: Record<string, number>, limit: number, total: number): RankedItem[] {
    return Object.entries(map)
      .sort((a, b) => b[1] - a[1])
      .slice(0, limit)
      .map(([label, value]) => ({
        label,
        value,
        context: this.toPercent(this.rate(value, total))
      }));
  }
}
