import { Component, inject, signal, computed } from '@angular/core';
import { CommonModule } from '@angular/common';
import { MatCardModule } from '@angular/material/card';
import { MatButtonModule } from '@angular/material/button';
import { MatIconModule } from '@angular/material/icon';
import { BedarfService } from '../../services/bedarf';
import { BetriebService } from '../../services/betrieb';
import { BedarfDto, BedarfStatus } from '../../models/bedarf.model';
import { firstValueFrom } from 'rxjs';

interface DashboardStats {
  activeBedarfe: number;
  totalBedarfe: number;
  totalBetriebe: number;
  monthlyBedarfe: number;
}

interface Activity {
  id: string;
  title: string;
  description: string;
  timestamp: string;
  icon: string;
  actionRequired?: boolean;
}

@Component({
  selector: 'app-dashboard',
  standalone: true,
  imports: [
    CommonModule,
    MatCardModule,
    MatButtonModule,
    MatIconModule
  ],
  templateUrl: './dashboard.html'
})
export class DashboardComponent {
  private bedarfService = inject(BedarfService);
  private betriebService = inject(BetriebService);

  // Signals for dashboard data
  private readonly loading = signal(false);
  
  // Stats signals
  readonly stats = signal<DashboardStats>({
    activeBedarfe: 0,
    totalBedarfe: 0,
    totalBetriebe: 0,
    monthlyBedarfe: 0
  });

  // Activities signal - relevant for pilot
  readonly recentActivities = signal<Activity[]>([
    {
      id: '1',
      title: 'Neuer Bedarf erstellt',
      description: 'Zimmermann für Holzbau-Projekt in Grabs',
      timestamp: 'vor 2 Stunden',
      icon: 'add_circle',
      actionRequired: false
    },
    {
      id: '2',
      title: 'Bedarf aktualisiert',
      description: 'Stundenlohn für Zimmermann-Position angepasst',
      timestamp: 'vor 4 Stunden',
      icon: 'edit',
      actionRequired: false
    }
  ]);

  // Remove notifications for pilot
  readonly notifications = signal<never[]>([]);

  private readonly mockActivities = [
    {
      id: 1,
      icon: 'add_circle',
      title: 'Neuer Bedarf erstellt',
      description: 'Zimmermann für Holzbau-Projekt in Grabs',
      timestamp: 'vor 2 Stunden'
    },
    {
      id: 2,
      icon: 'edit',
      title: 'Bedarf aktualisiert',
      description: 'Stundenlohn für Zimmermann-Position angepasst',
      timestamp: 'vor 4 Stunden'
    }
  ];

  constructor() {
    this.loadDashboardData();
  }

  private async loadDashboardData(): Promise<void> {
    this.loading.set(true);
    
    try {
      // Load actual data from services
      const [bedarfeResponse, betriebResponse] = await Promise.all([
        firstValueFrom(this.bedarfService.getBedarfe()),
        firstValueFrom(this.betriebService.getBetriebe())
      ]);

      // Calculate stats
      const activeBedarfe = bedarfeResponse.content.filter((b: BedarfDto) => b.status === BedarfStatus.AKTIV).length;
      const totalBedarfe = bedarfeResponse.content.length;
      const totalBetriebe = betriebResponse.content.length;
      
      // Calculate monthly Bedarfe (created in last 30 days)
      const thirtyDaysAgo = new Date();
      thirtyDaysAgo.setDate(thirtyDaysAgo.getDate() - 30);
      const monthlyBedarfe = bedarfeResponse.content.filter((b: BedarfDto) => {
        if (!b.createdAt) return false;
        const createdDate = new Date(b.createdAt);
        return createdDate >= thirtyDaysAgo;
      }).length;
      
      this.stats.set({
        activeBedarfe,
        totalBedarfe,
        totalBetriebe,
        monthlyBedarfe
      });

    } catch (error) {
      console.error('Error loading dashboard data:', error);
      
      // Fallback to mock data
      this.stats.set({
        activeBedarfe: 12,
        totalBedarfe: 28,
        totalBetriebe: 8,
        monthlyBedarfe: 8
      });
    } finally {
      this.loading.set(false);
    }
  }

  // Computed properties
  readonly hasActivities = computed(() => this.recentActivities().length > 0);
  readonly hasNotifications = computed(() => this.notifications().length > 0);
}
