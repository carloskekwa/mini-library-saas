import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { environment } from '../../environments/environment';
import { User } from '../models/index';

@Injectable({
  providedIn: 'root'
})
export class ProfileService {
  private apiUrl = `${environment.apiUrl}/users/me`;

  constructor(private http: HttpClient) {}

  getMyProfile(): Observable<User> {
    return this.http.get<User>(this.apiUrl);
  }

  updateMyProfile(payload: { username?: string; email?: string; firstName?: string; lastName?: string }): Observable<User> {
    return this.http.put<User>(this.apiUrl, payload);
  }
}
