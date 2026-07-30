import { Injectable } from '@angular/core';
import { HttpClient, HttpParams } from '@angular/common/http';
import { Observable } from 'rxjs';
import { environment } from '../../environments/environment';
import { BorrowRecord, ReturnRecord, PagedResponse } from '../models/index';

@Injectable({
  providedIn: 'root'
})
export class BorrowService {
  private apiUrl = `${environment.apiUrl}/borrow`;

  constructor(private http: HttpClient) {}

  borrowBook(bookId: number): Observable<BorrowRecord> {
    return this.http.post<BorrowRecord>(this.apiUrl, { bookId });
  }

  returnBook(payload: { borrowRecordId: number; bookCondition: string; damageNotes?: string }): Observable<ReturnRecord> {
    return this.http.post<ReturnRecord>(`${this.apiUrl}/return`, payload);
  }

  renewBorrow(borrowId: number): Observable<BorrowRecord> {
    return this.http.post<BorrowRecord>(`${this.apiUrl}/${borrowId}/renew`, {});
  }

  getBorrowHistory(page: number = 0, pageSize: number = 20): Observable<PagedResponse<BorrowRecord>> {
    const params = new HttpParams()
      .set('page', page.toString())
      .set('pageSize', pageSize.toString());
    return this.http.get<PagedResponse<BorrowRecord>>(`${this.apiUrl}/history`, { params });
  }

  getAllBorrowHistory(page: number = 0, pageSize: number = 20): Observable<PagedResponse<BorrowRecord>> {
    const params = new HttpParams()
      .set('page', page.toString())
      .set('pageSize', pageSize.toString());
    return this.http.get<PagedResponse<BorrowRecord>>(`${this.apiUrl}/history/all`, { params });
  }

  getActiveBorrows(): Observable<BorrowRecord[]> {
    return this.http.get<BorrowRecord[]>(`${this.apiUrl}/active`);
  }

  getOverdueRecords(): Observable<BorrowRecord[]> {
    return this.http.get<BorrowRecord[]>(`${this.apiUrl}/overdue`);
  }

  getBorrowInventory(page: number = 0, pageSize: number = 20): Observable<PagedResponse<BorrowRecord>> {
    const params = new HttpParams()
      .set('page', page.toString())
      .set('pageSize', pageSize.toString());
    return this.http.get<PagedResponse<BorrowRecord>>(`${this.apiUrl}/inventory`, { params });
  }

  getReturnRecords(page: number = 0, pageSize: number = 20): Observable<PagedResponse<ReturnRecord>> {
    const params = new HttpParams()
      .set('page', page.toString())
      .set('pageSize', pageSize.toString());
    return this.http.get<PagedResponse<ReturnRecord>>(`${this.apiUrl}/returns`, { params });
  }

  getAllReturnRecords(page: number = 0, pageSize: number = 20): Observable<PagedResponse<ReturnRecord>> {
    const params = new HttpParams()
      .set('page', page.toString())
      .set('pageSize', pageSize.toString());
    return this.http.get<PagedResponse<ReturnRecord>>(`${this.apiUrl}/returns/all`, { params });
  }

  payFine(returnRecordId: number): Observable<ReturnRecord> {
    return this.http.post<ReturnRecord>(`${this.apiUrl}/fines/${returnRecordId}/pay`, {});
  }

  getTotalUnpaidFines(): Observable<any> {
    return this.http.get<any>(`${this.apiUrl}/fines/total`);
  }
}
