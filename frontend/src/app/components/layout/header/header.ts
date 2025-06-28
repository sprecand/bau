import { Component, inject, signal } from '@angular/core';
import { CommonModule } from '@angular/common';
import { RouterModule } from '@angular/router';
import { MatIconModule } from '@angular/material/icon';
import { MatButtonModule } from '@angular/material/button';
import { MatMenuModule } from '@angular/material/menu';
import { MatSelectModule } from '@angular/material/select';
import { AuthService, UserRole } from '../../../services/auth.service';
import { ThemeService } from '../../../services/theme.service';

@Component({
  selector: 'app-header',
  standalone: true,
  imports: [
    CommonModule,
    RouterModule,
    MatIconModule,
    MatButtonModule,
    MatMenuModule,
    MatSelectModule
  ],
  templateUrl: './header.html'
})
export class HeaderComponent {
  private readonly authService = inject(AuthService);
  private readonly themeService = inject(ThemeService);

  // Expose auth service signals
  readonly user = this.authService.effectiveUser;
  readonly isAuthenticated = this.authService.isAuthenticated;
  readonly effectivelyAuthenticated = this.authService.effectivelyAuthenticated;
  readonly showRoleSelector = this.authService.showRoleSelector;
  readonly localRole = this.authService.localRole;
  readonly isAdmin = this.authService.isAdmin;
  readonly isBetrieb = this.authService.isBetrieb;

  // Expose theme service signals
  readonly isDark = this.themeService.isDark;
  readonly theme = this.themeService.theme;

  // Expose UserRole enum for template
  readonly UserRole = UserRole;

  // Mobile menu state
  readonly mobileMenuOpen = signal(false);

  toggleMobileMenu(open?: boolean): void {
    if (open !== undefined) {
      this.mobileMenuOpen.set(open);
    } else {
      this.mobileMenuOpen.set(!this.mobileMenuOpen());
    }
  }

  closeMobileMenu(): void {
    this.mobileMenuOpen.set(false);
  }

  async logout(): Promise<void> {
    await this.authService.logout();
  }

  onRoleChange(role: UserRole | null): void {
    this.authService.setLocalRole(role);
  }

  onRoleSelectChange(event: Event): void {
    const target = event.target as HTMLSelectElement;
    const value = target.value;
    this.onRoleChange(value ? (value as UserRole) : null);
  }

  getRoleDisplayName(role: UserRole): string {
    switch (role) {
      case UserRole.ADMIN:
        return 'Administrator';
      case UserRole.BETRIEB:
        return 'Betrieb';
      default:
        return 'Unbekannt';
    }
  }

  toggleTheme(): void {
    this.themeService.toggleTheme();
  }

  getThemeIcon(): string {
    return this.isDark() ? 'light_mode' : 'dark_mode';
  }

  getThemeTooltip(): string {
    return this.isDark() ? 'Heller Modus' : 'Dunkler Modus';
  }
}
