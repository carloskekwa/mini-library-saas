import { Injectable } from '@angular/core';
import { HttpClient, HttpParams } from '@angular/common/http';
import { Observable } from 'rxjs';
import { environment } from '../../environments/environment';
import { PagedResponse, Reservation } from '../models/index';

@Injectable({
  providedIn: 'root'
})
export class ReservationService {
  private apiUrl = `${environment.apiUrl}/reservations`;

  constructor(private http: HttpClient) {}

  reserveBook(bookId: number): Observable<Reservation> {
    return this.http.post<Reservation>(this.apiUrl, { bookId });
  }

  getReservations(page: number = 0, pageSize: number = 20): Observable<PagedResponse<Reservation>> {
    const params = new HttpParams()
      .set('page', page.toString())
      .set('pageSize', pageSize.toString());
    return this.http.get<PagedResponse<Reservation>>(this.apiUrl, { params });
  }

  getActiveReservations(): Observable<Reservation[]> {
    return this.http.get<Reservation[]>(`${this.apiUrl}/active`);
  }

  getBookReservationQueue(bookId: number): Observable<Reservation[]> {
    return this.http.get<Reservation[]>(`${this.apiUrl}/book/${bookId}/queue`);
  }

  fulfillReservation(reservationId: number): Observable<Reservation> {
    return this.http.put<Reservation>(`${this.apiUrl}/${reservationId}/fulfill`, {});
  }

  cancelReservation(reservationId: number): Observable<void> {
    return this.http.delete<void>(`${this.apiUrl}/${reservationId}`);
  }

  getAllReservations(page: number = 0, pageSize: number = 20, status?: string): Observable<PagedResponse<Reservation>> {
    let params = new HttpParams()
      .set('page', page.toString())
      .set('pageSize', pageSize.toString());
    if (status) {
      params = params.set('status', status);
    }
    return this.http.get<PagedResponse<Reservation>>(`${this.apiUrl}/all`, { params });
  }
}
