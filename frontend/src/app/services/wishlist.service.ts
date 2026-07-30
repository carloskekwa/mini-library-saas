import { Injectable } from '@angular/core';
import { HttpClient, HttpParams } from '@angular/common/http';
import { Observable } from 'rxjs';
import { environment } from '../../environments/environment';
import { PagedResponse, WishlistItem } from '../models/index';

@Injectable({
  providedIn: 'root'
})
export class WishlistService {
  private apiUrl = `${environment.apiUrl}/wishlist`;

  constructor(private http: HttpClient) {}

  addToWishlist(bookId: number): Observable<WishlistItem> {
    const params = new HttpParams().set('bookId', bookId.toString());
    return this.http.post<WishlistItem>(this.apiUrl, {}, { params });
  }

  getWishlist(page: number = 0, pageSize: number = 20): Observable<PagedResponse<WishlistItem>> {
    const params = new HttpParams()
      .set('page', page.toString())
      .set('pageSize', pageSize.toString());
    return this.http.get<PagedResponse<WishlistItem>>(this.apiUrl, { params });
  }

  removeFromWishlist(wishlistId: number): Observable<void> {
    return this.http.delete<void>(`${this.apiUrl}/${wishlistId}`);
  }
}
