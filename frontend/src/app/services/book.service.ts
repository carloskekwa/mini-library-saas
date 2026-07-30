import { Injectable } from '@angular/core';
import { HttpClient, HttpParams } from '@angular/common/http';
import { Observable } from 'rxjs';
import { environment } from '../../environments/environment';
import { Book, CreateBookRequest, UpdateBookRequest, PagedResponse } from '../models/index';

@Injectable({
  providedIn: 'root'
})
export class BookService {
  private apiUrl = `${environment.apiUrl}/books`;

  constructor(private http: HttpClient) {}

  getBooks(page: number = 0, pageSize: number = 20): Observable<PagedResponse<Book>> {
    const params = new HttpParams()
      .set('page', page.toString())
      .set('pageSize', pageSize.toString());
    return this.http.get<PagedResponse<Book>>(this.apiUrl, { params });
  }

  getBookById(id: number): Observable<Book> {
    return this.http.get<Book>(`${this.apiUrl}/${id}`);
  }

  searchBooks(searchRequest: any): Observable<PagedResponse<Book>> {
    return this.http.post<PagedResponse<Book>>(`${this.apiUrl}/search`, searchRequest);
  }

  searchByTitle(title: string, page: number = 0, pageSize: number = 20): Observable<PagedResponse<Book>> {
    return this.http.get<PagedResponse<Book>>(`${this.apiUrl}/search/title`, {
      params: new HttpParams()
        .set('title', title)
        .set('page', page.toString())
        .set('pageSize', pageSize.toString())
    });
  }

  searchByTitleOrAuthor(keyword: string, page: number = 0, pageSize: number = 20): Observable<PagedResponse<Book>> {
    return this.http.get<PagedResponse<Book>>(`${this.apiUrl}/search/title-author`, {
      params: new HttpParams()
        .set('keyword', keyword)
        .set('page', page.toString())
        .set('pageSize', pageSize.toString())
    });
  }

  searchByAuthor(author: string): Observable<PagedResponse<Book>> {
    return this.http.get<PagedResponse<Book>>(`${this.apiUrl}/search/author`, {
      params: new HttpParams().set('author', author)
    });
  }

  getRecentBooks(): Observable<PagedResponse<Book>> {
    return this.http.get<PagedResponse<Book>>(`${this.apiUrl}/recent`);
  }

  createBook(request: CreateBookRequest): Observable<Book> {
    return this.http.post<Book>(this.apiUrl, request);
  }

  updateBook(id: number, request: UpdateBookRequest): Observable<Book> {
    return this.http.put<Book>(`${this.apiUrl}/${id}`, request);
  }

  deleteBook(id: number): Observable<void> {
    return this.http.delete<void>(`${this.apiUrl}/${id}`);
  }

  getAvailableBooks(): Observable<PagedResponse<Book>> {
    return this.http.get<PagedResponse<Book>>(`${this.apiUrl}/available`);
  }

  getBooksByCategory(categoryId: number, page: number = 0, pageSize: number = 20, keyword: string = ''): Observable<PagedResponse<Book>> {
    let params = new HttpParams()
      .set('page', page.toString())
      .set('pageSize', pageSize.toString());
    if (keyword.trim()) {
      params = params.set('keyword', keyword.trim());
    }
    return this.http.get<PagedResponse<Book>>(`${this.apiUrl}/category/${categoryId}`, { params });
  }
}
