import { Routes } from '@angular/router';
import { inject } from '@angular/core';
import { Router } from '@angular/router';
import { AuthService } from './services/auth.service';

// Auth guard function - uses effectivelyAuthenticated for development role testing
const authGuard = () => {
  const authService = inject(AuthService);
  const router = inject(Router);
  
  if (authService.effectivelyAuthenticated()) {
    return true;
  }
  
  // Redirect to login if not effectively authenticated
  router.navigate(['/login']);
  return false;
};

// Dashboard guard function - allows access for any authenticated state (including "Abgemeldet")
const dashboardGuard = () => {
  const authService = inject(AuthService);
  const router = inject(Router);
  
  if (authService.isAuthenticated()) {
    return true;
  }
  
  // Redirect to login if not authenticated at all
  router.navigate(['/login']);
  return false;
};

// Login guard function (redirect if already authenticated)
const loginGuard = () => {
  const authService = inject(AuthService);
  const router = inject(Router);
  
  if (authService.isAuthenticated()) {
    // Redirect to dashboard if already authenticated
    router.navigate(['/dashboard']);
    return false;
  }
  return true;
};

export const routes: Routes = [
  {
    path: '',
    redirectTo: '/login',
    pathMatch: 'full'
  },
  {
    path: 'login',
    loadComponent: () => import('./pages/login/login').then(m => m.LoginComponent),
    canActivate: [loginGuard]
  },
  {
    path: 'about',
    loadComponent: () => import('./pages/about/about').then(m => m.AboutComponent)
    // No guard - accessible to all users
  },
  {
    path: 'dashboard',
    loadComponent: () => import('./pages/dashboard/dashboard').then(m => m.DashboardComponent),
    canActivate: [dashboardGuard]
  },
  {
    path: 'bedarfe',
    loadComponent: () => import('./pages/bedarfe/bedarfe').then(m => m.BedarfeComponent),
    canActivate: [authGuard]
  },
  {
    path: 'betriebe',
    loadComponent: () => import('./pages/betriebe/betriebe').then(m => m.BetriebeComponent),
    canActivate: [authGuard]
  },
  {
    path: '**',
    redirectTo: '/login'
  }
];
