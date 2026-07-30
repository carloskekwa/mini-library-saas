import { Injectable } from '@angular/core';
import { HttpClient, HttpParams } from '@angular/common/http';
import { Observable } from 'rxjs';
import { environment } from '../../environments/environment';
import { PagedResponse, Report } from '../models/index';

@Injectable({
  providedIn: 'root'
})
export class ReportService {
  private apiUrl = `${environment.apiUrl}/reports`;

  constructor(private http: HttpClient) {}

  generatePopularBooksReport(): Observable<Report> {
    return this.http.post<Report>(`${this.apiUrl}/popular-books`, {});
  }

  generateCirculationStats(start: string, end: string): Observable<Report> {
    const params = new HttpParams().set('start', start).set('end', end);
    return this.http.post<Report>(`${this.apiUrl}/circulation-stats`, {}, { params });
  }

  getReports(page: number = 0, pageSize: number = 20): Observable<PagedResponse<Report>> {
    const params = new HttpParams().set('page', page.toString()).set('pageSize', pageSize.toString());
    return this.http.get<PagedResponse<Report>>(this.apiUrl, { params });
  }

  deleteReport(reportId: number): Observable<void> {
    return this.http.delete<void>(`${this.apiUrl}/${reportId}`);
  }
}
