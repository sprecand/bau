import { Injectable, signal, computed, inject } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Router } from '@angular/router';
import { firstValueFrom } from 'rxjs';
import { environment } from '../../environments/environment';

export interface User {
  id: string;
  email: string;
  name: string;
  role: UserRole;
  betriebId?: string;
}

export enum UserRole {
  ADMIN = 'ADMIN',
  BETRIEB = 'BETRIEB'
}

export interface LoginRequest {
  email: string;
  password: string;
}

export interface LoginResponse {
  token: string;
  user: User;
}

@Injectable({
  providedIn: 'root'
})
export class AuthService {
  private readonly http = inject(HttpClient);
  private readonly router = inject(Router);

  // Signals for authentication state
  private readonly _user = signal<User | null>(null);
  private readonly _isAuthenticated = signal(false);
  private readonly _isLoading = signal(false);

  // Local development role override (only visible in development)
  private readonly _localRole = signal<UserRole | null>(null);

  // Public readonly signals
  readonly user = this._user.asReadonly();
  readonly isAuthenticated = this._isAuthenticated.asReadonly();
  readonly isLoading = this._isLoading.asReadonly();
  readonly localRole = this._localRole.asReadonly();

  // Computed properties
  readonly effectiveUser = computed(() => {
    const user = this._user();
    const localRole = this._localRole();
    
    if (!environment.production && localRole && user) {
      return { ...user, role: localRole };
    }
    return user;
  });

  // Effective authentication state considering local role override
  readonly effectivelyAuthenticated = computed(() => {
    if (!environment.production && this._localRole() === null && this._isAuthenticated()) {
      // In development, if local role is explicitly set to null (Abgemeldet), treat as logged out
      return false;
    }
    return this._isAuthenticated();
  });

  readonly isAdmin = computed(() => this.effectiveUser()?.role === UserRole.ADMIN);
  readonly isBetrieb = computed(() => this.effectiveUser()?.role === UserRole.BETRIEB);
  readonly showRoleSelector = computed(() => !environment.production && this._isAuthenticated());

  constructor() {
    this.checkAuthStatus();
  }

  async login(email: string, password: string): Promise<void> {
    this._isLoading.set(true);
    
    try {
      if (!environment.production) {
        // Mock login for local development
        await this.mockLogin(email, password);
      } else {
        // Real login for production
        const response = await firstValueFrom(
          this.http.post<LoginResponse>('/api/v1/auth/login', { email, password })
        );
        
        this.setAuthData(response.token, response.user);
      }
    } catch (error) {
      console.error('Login failed:', error);
      throw error;
    } finally {
      this._isLoading.set(false);
    }
  }

  async logout(): Promise<void> {
    this._isLoading.set(true);
    
    try {
      if (!environment.production) {
        // Mock logout for local development
        await this.mockLogout();
      } else {
        // Real logout for production
        await firstValueFrom(this.http.post('/api/v1/auth/logout', {}));
      }
    } catch (error) {
      console.error('Logout failed:', error);
    } finally {
      this.clearAuthData();
      this._isLoading.set(false);
      this.router.navigate(['/login']);
    }
  }

  setLocalRole(role: UserRole | null): void {
    if (!environment.production) {
      this._localRole.set(role);
      localStorage.setItem('localRole', role || '');
    }
  }

  async getIdToken(): Promise<string> {
    const token = localStorage.getItem('authToken');
    if (!token) {
      throw new Error('No authentication token available');
    }
    return token;
  }

  private async mockLogin(email: string, password: string): Promise<void> {
    // Simulate API delay
    await new Promise(resolve => setTimeout(resolve, 1000));
    
    // Mock user data based on email
    let role = UserRole.BETRIEB;
    if (email.includes('admin')) {
      role = UserRole.ADMIN;
    }

    const mockUser: User = {
      id: 'mock-user-id',
      email,
      name: email.split('@')[0],
      role,
      betriebId: role === UserRole.BETRIEB ? '123e4567-e89b-12d3-a456-426614174001' : undefined
    };

    const mockToken = 'mock-jwt-token-' + Date.now();
    
    this.setAuthData(mockToken, mockUser);
  }

  private async mockLogout(): Promise<void> {
    // Simulate API delay
    await new Promise(resolve => setTimeout(resolve, 500));
  }

  private setAuthData(token: string, user: User): void {
    localStorage.setItem('authToken', token);
    localStorage.setItem('user', JSON.stringify(user));
    this._user.set(user);
    this._isAuthenticated.set(true);
  }

  private clearAuthData(): void {
    localStorage.removeItem('authToken');
    localStorage.removeItem('user');
    localStorage.removeItem('localRole');
    this._user.set(null);
    this._isAuthenticated.set(false);
    this._localRole.set(null);
  }

  private checkAuthStatus(): void {
    const token = localStorage.getItem('authToken');
    const userStr = localStorage.getItem('user');
    const localRoleStr = localStorage.getItem('localRole');
    
    if (token && userStr) {
      try {
        const user = JSON.parse(userStr);
        this._user.set(user);
        this._isAuthenticated.set(true);
        
        if (!environment.production && localRoleStr) {
          this._localRole.set(localRoleStr as UserRole);
        }
      } catch (error) {
        console.error('Failed to parse stored user data:', error);
        this.clearAuthData();
      }
    } else {
      // Ensure we start as unauthenticated if no valid token/user
      this._isAuthenticated.set(false);
      this._user.set(null);
    }
  }
} 