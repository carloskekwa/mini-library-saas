import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { BehaviorSubject, Observable } from 'rxjs';
import { tap } from 'rxjs/operators';
import { environment } from '../../environments/environment';
import { User, LoginRequest, RegisterRequest, AuthResponse } from '../models/index';

@Injectable({
  providedIn: 'root'
})
export class AuthService {
  private apiUrl = `${environment.apiUrl}/auth`;
  private currentUserSubject = new BehaviorSubject<User | null>(this.getUserFromSessionStorage());
  public currentUser$ = this.currentUserSubject.asObservable();

  constructor(private http: HttpClient) {}

  login(loginRequest: LoginRequest): Observable<AuthResponse> {
    return this.http.post<AuthResponse>(`${this.apiUrl}/login`, loginRequest).pipe(
      tap(response => {
        this.storeToken(response.accessToken);
        const user: User = {
          id: response.userId,
          username: response.username,
          email: response.email,
          firstName: response.firstName,
          lastName: response.lastName,
          status: 'active',
          roles: response.roles
        };
        this.currentUserSubject.next(user);
        sessionStorage.setItem('currentUser', JSON.stringify(user));
      })
    );
  }

  register(registerRequest: RegisterRequest): Observable<AuthResponse> {
    return this.http.post<AuthResponse>(`${this.apiUrl}/register`, registerRequest).pipe(
      tap(response => {
        this.storeToken(response.accessToken);
        const user: User = {
          id: response.userId,
          username: response.username,
          email: response.email,
          firstName: response.firstName,
          lastName: response.lastName,
          status: 'active',
          roles: response.roles
        };
        this.currentUserSubject.next(user);
        sessionStorage.setItem('currentUser', JSON.stringify(user));
      })
    );
  }

  logout(): void {
    sessionStorage.removeItem('authToken');
    sessionStorage.removeItem('currentUser');
    this.currentUserSubject.next(null);
  }

  refreshToken(): Observable<AuthResponse> {
    return this.http.post<AuthResponse>(`${this.apiUrl}/refresh-token`, {}).pipe(
      tap(response => {
        this.storeToken(response.accessToken);
        const user: User = {
          id: response.userId,
          username: response.username,
          email: response.email,
          firstName: response.firstName,
          lastName: response.lastName,
          status: 'active',
          roles: response.roles
        };
        this.currentUserSubject.next(user);
        sessionStorage.setItem('currentUser', JSON.stringify(user));
      })
    );
  }

  getToken(): string | null {
    return sessionStorage.getItem('authToken');
  }

  isLoggedIn(): boolean {
    return !!this.getToken();
  }

  getCurrentUser(): User | null {
    return this.currentUserSubject.value;
  }

  hasRole(role: string): boolean {
    const user = this.getCurrentUser();
    if (!user?.roles?.length) {
      return false;
    }
    return user.roles.includes(role);
  }

  hasAnyRole(roles: string[]): boolean {
    return roles.some((role) => this.hasRole(role));
  }

  getDefaultRouteForCurrentUser(): string {
    if (this.hasRole('ADMIN')) {
      return '/dashboard/admin';
    }
    if (this.hasRole('LIBRARIAN')) {
      return '/dashboard/librarian';
    }
    return '/dashboard/member';
  }

  updateCurrentUserProfile(profile: User): void {
    const currentUser = this.getCurrentUser();
    if (!currentUser) {
      return;
    }

    const updatedUser: User = {
      ...currentUser,
      ...profile,
      roles: profile.roles || currentUser.roles
    };

    this.currentUserSubject.next(updatedUser);
    sessionStorage.setItem('currentUser', JSON.stringify(updatedUser));
  }

  private storeToken(token: string): void {
    sessionStorage.setItem('authToken', token);
  }

  private getUserFromSessionStorage(): User | null {
    const userJson = sessionStorage.getItem('currentUser');
    return userJson ? JSON.parse(userJson) : null;
  }
}
