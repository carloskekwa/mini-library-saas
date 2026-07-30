import { Injectable } from '@angular/core';
import { HttpClient, HttpParams } from '@angular/common/http';
import { Observable } from 'rxjs';
import { environment } from '../../environments/environment';
import { BookRequest, PagedResponse } from '../models/index';

@Injectable({
  providedIn: 'root'
})
export class BookRequestService {
  private apiUrl = `${environment.apiUrl}/book-requests`;

  constructor(private http: HttpClient) {}

  createRequest(payload: { bookTitle: string; author: string; isbn?: string; justification?: string }): Observable<BookRequest> {
    let params = new HttpParams()
      .set('bookTitle', payload.bookTitle)
      .set('author', payload.author);

    if (payload.isbn?.trim()) {
      params = params.set('isbn', payload.isbn.trim());
    }
    if (payload.justification?.trim()) {
      params = params.set('justification', payload.justification.trim());
    }

    return this.http.post<BookRequest>(this.apiUrl, {}, { params });
  }

  getUserRequests(page: number = 0, pageSize: number = 20): Observable<PagedResponse<BookRequest>> {
    const params = new HttpParams()
      .set('page', page.toString())
      .set('pageSize', pageSize.toString());
    return this.http.get<PagedResponse<BookRequest>>(`${this.apiUrl}/user`, { params });
  }

  getPendingRequests(page: number = 0, pageSize: number = 20): Observable<PagedResponse<BookRequest>> {
    const params = new HttpParams()
      .set('page', page.toString())
      .set('pageSize', pageSize.toString());
    return this.http.get<PagedResponse<BookRequest>>(`${this.apiUrl}/pending`, { params });
  }

  approveRequest(requestId: number): Observable<void> {
    return this.http.put<void>(`${this.apiUrl}/${requestId}/approve`, {});
  }

  rejectRequest(requestId: number): Observable<void> {
    return this.http.put<void>(`${this.apiUrl}/${requestId}/reject`, {});
  }
}
