import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { environment } from '../../environments/environment';

export interface AIChatRequest {
  message: string;
  userId?: number;
}

export interface BookRecommendation {
  bookId: number;
  title: string;
  author: string;
  category: string;
  availability: string;
  availableCopies: number;
}

export interface AIChatResponse {
  answer: string;
  books: BookRecommendation[];
}

export interface AIInsightRequest {
  question: string;
}

export interface AIInsightResponse {
  analysis: string;
  generatedAt: string;
}

@Injectable({
  providedIn: 'root'
})
export class AiService {
  private apiUrl = `${environment.apiUrl}/ai`;

  constructor(private http: HttpClient) {}

  recommend(message: string, userId?: number): Observable<AIChatResponse> {
    const body: AIChatRequest = { message, userId };
    return this.http.post<AIChatResponse>(`${this.apiUrl}/recommend`, body);
  }

  getInsight(question: string): Observable<AIInsightResponse> {
    const body: AIInsightRequest = { question };
    return this.http.post<AIInsightResponse>(`${this.apiUrl}/insights`, body);
  }

  reindex(): Observable<void> {
    return this.http.post<void>(`${this.apiUrl}/reindex`, {});
  }
}
