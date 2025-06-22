import { Injectable, signal, effect } from '@angular/core';

export type Theme = 'light' | 'dark' | 'system';

@Injectable({
  providedIn: 'root'
})
export class ThemeService {
  private readonly _theme = signal<Theme>('system');
  private readonly _isDark = signal(false);

  readonly theme = this._theme.asReadonly();
  readonly isDark = this._isDark.asReadonly();

  constructor() {
    this.initializeTheme();
    this.setupThemeEffect();
  }

  private initializeTheme(): void {
    // Load saved theme preference or default to system
    const savedTheme = localStorage.getItem('theme') as Theme;
    if (savedTheme && ['light', 'dark', 'system'].includes(savedTheme)) {
      this._theme.set(savedTheme);
    }

    // Set initial dark mode state
    this.updateDarkMode();
  }

  private setupThemeEffect(): void {
    // Effect to update DOM when theme changes
    effect(() => {
      const isDark = this._isDark();
      const theme = this._theme();
      const htmlElement = document.documentElement;
      
      // Remove all theme classes first
      htmlElement.classList.remove('dark', 'light');
      
      // Add the appropriate class based on the effective theme
      if (theme === 'system') {
        // For system theme, let CSS media query handle it
        // Don't add any class, let @media (prefers-color-scheme: dark) work
      } else {
        // For explicit light/dark themes, add the class to override media query
        htmlElement.classList.add(theme);
      }
    });

    // Listen for system theme changes
    if (window.matchMedia) {
      const mediaQuery = window.matchMedia('(prefers-color-scheme: dark)');
      mediaQuery.addEventListener('change', () => {
        if (this._theme() === 'system') {
          this.updateDarkMode();
        }
      });
    }
  }

  private updateDarkMode(): void {
    const theme = this._theme();
    let isDark = false;

    switch (theme) {
      case 'dark':
        isDark = true;
        break;
      case 'light':
        isDark = false;
        break;
      case 'system':
        isDark = window.matchMedia && window.matchMedia('(prefers-color-scheme: dark)').matches;
        break;
    }

    this._isDark.set(isDark);
  }

  setTheme(theme: Theme): void {
    this._theme.set(theme);
    localStorage.setItem('theme', theme);
    this.updateDarkMode();
  }

  toggleTheme(): void {
    const currentTheme = this._theme();
    const isDark = this._isDark();
    
    // If currently system theme, switch to the opposite of what system provides
    // If currently explicit theme, toggle between light and dark
    let newTheme: Theme;
    
    if (currentTheme === 'system') {
      newTheme = isDark ? 'light' : 'dark';
    } else {
      newTheme = currentTheme === 'dark' ? 'light' : 'dark';
    }
    
    this.setTheme(newTheme);
  }
} 