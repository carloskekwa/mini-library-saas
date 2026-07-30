import { Injectable } from '@angular/core';
import { HttpClient, HttpParams } from '@angular/common/http';
import { Observable } from 'rxjs';
import { environment } from '../../environments/environment';
import { Penalty } from '../models/index';

@Injectable({
  providedIn: 'root'
})
export class PenaltyService {
  private apiUrl = `${environment.apiUrl}/penalties`;

  constructor(private http: HttpClient) {}

  createPenalty(payload: { userId: number; type: string; reason: string }): Observable<Penalty> {
    const params = new HttpParams()
      .set('userId', payload.userId.toString())
      .set('type', payload.type)
      .set('reason', payload.reason);
    return this.http.post<Penalty>(this.apiUrl, {}, { params });
  }

  getUserActivePenalties(userId: number): Observable<Penalty[]> {
    return this.http.get<Penalty[]>(`${this.apiUrl}/user/${userId}/active`);
  }

  liftPenalty(penaltyId: number): Observable<void> {
    return this.http.put<void>(`${this.apiUrl}/${penaltyId}/lift`, {});
  }
}
