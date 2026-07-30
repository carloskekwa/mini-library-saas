import { Injectable } from '@angular/core';
import { HttpClient, HttpParams } from '@angular/common/http';
import { Observable } from 'rxjs';
import { environment } from '../../environments/environment';
import { AuditLog, PagedResponse } from '../models/index';

@Injectable({
  providedIn: 'root'
})
export class AuditLogService {
  private apiUrl = `${environment.apiUrl}/audit-logs`;

  constructor(private http: HttpClient) {}

  getUserAuditLogs(userId: number, page: number = 0, pageSize: number = 20): Observable<PagedResponse<AuditLog>> {
    const params = new HttpParams().set('page', page.toString()).set('pageSize', pageSize.toString());
    return this.http.get<PagedResponse<AuditLog>>(`${this.apiUrl}/user/${userId}`, { params });
  }

  getAuditLogsByDateRange(start: string, end: string, page: number = 0, pageSize: number = 20): Observable<PagedResponse<AuditLog>> {
    const params = new HttpParams()
      .set('start', start)
      .set('end', end)
      .set('page', page.toString())
      .set('pageSize', pageSize.toString());
    return this.http.get<PagedResponse<AuditLog>>(`${this.apiUrl}/date-range`, { params });
  }
}
