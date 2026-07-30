import { Injectable } from '@angular/core';
import { HttpClient, HttpParams } from '@angular/common/http';
import { Observable } from 'rxjs';
import { environment } from '../../environments/environment';
import { Review } from '../models/index';

@Injectable({
  providedIn: 'root'
})
export class ReviewService {
  private apiUrl = `${environment.apiUrl}/reviews`;

  constructor(private http: HttpClient) {}

  createReview(bookId: number, rating: number, reviewText?: string): Observable<Review> {
    let params = new HttpParams()
      .set('bookId', bookId.toString())
      .set('rating', rating.toString());
    if (reviewText) {
      params = params.set('reviewText', reviewText);
    }
    return this.http.post<Review>(this.apiUrl, {}, { params });
  }

  getBookReviews(bookId: number, page: number = 0, pageSize: number = 10): Observable<any> {
    const params = new HttpParams()
      .set('page', page.toString())
      .set('pageSize', pageSize.toString());
    return this.http.get<any>(`${this.apiUrl}/book/${bookId}`, { params });
  }

  getAverageRating(bookId: number): Observable<number> {
    return this.http.get<number>(`${this.apiUrl}/book/${bookId}/rating`);
  }

  deleteReview(reviewId: number): Observable<void> {
    return this.http.delete<void>(`${this.apiUrl}/${reviewId}`);
  }
}
