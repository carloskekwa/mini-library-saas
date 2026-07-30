import { Injectable } from '@angular/core';
import { HttpClient, HttpParams } from '@angular/common/http';
import { Observable } from 'rxjs';
import { environment } from '../../environments/environment';
import { PagedResponse, User } from '../models/index';

@Injectable({
  providedIn: 'root'
})
export class UserAdminService {
  private apiUrl = `${environment.apiUrl}/users`;

  constructor(private http: HttpClient) {}

  getUsers(params: { page?: number; pageSize?: number; search?: string; status?: string }): Observable<PagedResponse<User>> {
    let httpParams = new HttpParams()
      .set('page', String(params.page ?? 0))
      .set('pageSize', String(params.pageSize ?? 20));

    if (params.search?.trim()) {
      httpParams = httpParams.set('search', params.search.trim());
    }

    if (params.status?.trim()) {
      httpParams = httpParams.set('status', params.status.trim());
    }

    return this.http.get<PagedResponse<User>>(this.apiUrl, { params: httpParams });
  }

  getUserById(userId: number): Observable<User> {
    return this.http.get<User>(`${this.apiUrl}/${userId}`);
  }

  updateUserStatus(userId: number, status: string): Observable<User> {
    const params = new HttpParams().set('status', status);
    return this.http.put<User>(`${this.apiUrl}/${userId}/status`, {}, { params });
  }
}
