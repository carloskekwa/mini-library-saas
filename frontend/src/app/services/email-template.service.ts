import { Injectable } from '@angular/core';
import { HttpClient, HttpParams } from '@angular/common/http';
import { Observable } from 'rxjs';
import { environment } from '../../environments/environment';
import { EmailTemplate } from '../models/index';

@Injectable({
  providedIn: 'root'
})
export class EmailTemplateService {
  private apiUrl = `${environment.apiUrl}/email-templates`;

  constructor(private http: HttpClient) {}

  getTemplate(type: string): Observable<EmailTemplate> {
    return this.http.get<EmailTemplate>(`${this.apiUrl}/${type}`);
  }

  saveTemplate(payload: { type: string; name: string; subject: string; body: string }): Observable<EmailTemplate> {
    const params = new HttpParams()
      .set('type', payload.type)
      .set('name', payload.name)
      .set('subject', payload.subject)
      .set('body', payload.body);
    return this.http.post<EmailTemplate>(this.apiUrl, {}, { params });
  }
}
