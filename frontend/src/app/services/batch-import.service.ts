import { Injectable } from '@angular/core';
import { HttpClient, HttpParams } from '@angular/common/http';
import { Observable } from 'rxjs';
import { environment } from '../../environments/environment';
import { BatchImportJob, PagedResponse } from '../models/index';

@Injectable({
  providedIn: 'root'
})
export class BatchImportService {
  private apiUrl = `${environment.apiUrl}/batch-import`;

  constructor(private http: HttpClient) {}

  createImportJob(filePath: string): Observable<BatchImportJob> {
    const params = new HttpParams().set('filePath', filePath);
    return this.http.post<BatchImportJob>(this.apiUrl, {}, { params });
  }

  getUserJobs(page: number = 0, pageSize: number = 20): Observable<PagedResponse<BatchImportJob>> {
    const params = new HttpParams().set('page', page.toString()).set('pageSize', pageSize.toString());
    return this.http.get<PagedResponse<BatchImportJob>>(this.apiUrl, { params });
  }

  startImport(jobId: number): Observable<void> {
    return this.http.post<void>(`${this.apiUrl}/${jobId}/start`, {});
  }

  completeImport(jobId: number): Observable<void> {
    return this.http.post<void>(`${this.apiUrl}/${jobId}/complete`, {});
  }
}
