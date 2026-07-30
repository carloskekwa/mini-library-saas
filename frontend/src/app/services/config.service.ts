import { Injectable } from '@angular/core';
import { HttpClient, HttpParams } from '@angular/common/http';
import { Observable } from 'rxjs';
import { environment } from '../../environments/environment';
import { ConfigProperty } from '../models/index';

@Injectable({
  providedIn: 'root'
})
export class ConfigService {
  private apiUrl = `${environment.apiUrl}/config`;

  constructor(private http: HttpClient) {}

  getAllConfigs(): Observable<ConfigProperty[]> {
    return this.http.get<ConfigProperty[]>(this.apiUrl);
  }

  getConfig(key: string): Observable<ConfigProperty> {
    return this.http.get<ConfigProperty>(`${this.apiUrl}/${key}`);
  }

  updateConfig(payload: { key: string; value: string; description?: string; type: string }): Observable<any> {
    let params = new HttpParams()
      .set('key', payload.key)
      .set('value', payload.value)
      .set('type', payload.type);

    if (payload.description?.trim()) {
      params = params.set('description', payload.description.trim());
    }

    return this.http.put<any>(this.apiUrl, {}, { params });
  }

  deleteConfig(key: string): Observable<void> {
    return this.http.delete<void>(`${this.apiUrl}/${key}`);
  }
}
