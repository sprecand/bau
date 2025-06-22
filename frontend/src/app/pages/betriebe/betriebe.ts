import { Component, signal, computed, inject, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { ReactiveFormsModule, FormBuilder, FormGroup, Validators } from '@angular/forms';
import { MatIconModule } from '@angular/material/icon';
import { MatSnackBarModule, MatSnackBar } from '@angular/material/snack-bar';

import { BetriebService } from '../../services/betrieb';
import { BetriebDto, BetriebStatus, BetriebCreateDto, BetriebUpdateDto, BetriebStatusUpdateDto } from '../../models/betrieb.model';
import { AuthService } from '../../services/auth.service';

@Component({
  selector: 'app-betriebe',
  standalone: true,
  imports: [
    CommonModule,
    ReactiveFormsModule,
    MatIconModule,
    MatSnackBarModule
  ],
  templateUrl: './betriebe.html',
  styleUrl: './betriebe.scss'
})
export class BetriebeComponent implements OnInit {
  private readonly betriebService = inject(BetriebService);
  private readonly authService = inject(AuthService);
  private readonly formBuilder = inject(FormBuilder);
  private readonly snackBar = inject(MatSnackBar);

  // Signals for state management
  readonly betriebe = signal<BetriebDto[]>([]);
  readonly loading = signal(false);
  readonly selectedBetrieb = signal<BetriebDto | null>(null);
  readonly showForm = signal(false);
  readonly isEditing = signal(false);

  // Authentication signals
  readonly isAdmin = this.authService.isAdmin;
  readonly user = this.authService.effectiveUser;

  // Computed values
  readonly filteredBetriebe = computed(() => {
    return this.betriebe().filter(betrieb => betrieb.status === BetriebStatus.AKTIV);
  });

  // Form
  betriebForm: FormGroup;

  // Enums for template
  readonly BetriebStatus = BetriebStatus;

  constructor() {
    this.betriebForm = this.formBuilder.group({
      name: ['', [Validators.required, Validators.minLength(2)]],
      adresse: ['', [Validators.required]],
      telefon: ['', [Validators.pattern(/^[0-9+\-\s()]+$/)]],
      email: ['', [Validators.required, Validators.email]]
    });
  }

  ngOnInit(): void {
    this.loadBetriebe();
  }

  loadBetriebe(): void {
    this.loading.set(true);
    this.betriebService.getBetriebe().subscribe({
      next: (response) => {
        this.betriebe.set(response.content);
        this.loading.set(false);
      },
      error: (error) => {
        this.showError('Fehler beim Laden der Betriebe');
        console.error('Error loading Betriebe:', error);
        this.loading.set(false);
      }
    });
  }

  openCreateForm(): void {
    if (!this.isAdmin()) {
      this.showError('Nur Administratoren können neue Betriebe erstellen');
      return;
    }
    this.resetForm();
    this.isEditing.set(false);
    this.showForm.set(true);
  }

  openEditForm(betrieb: BetriebDto): void {
    if (!this.isAdmin()) {
      this.showError('Nur Administratoren können Betriebe bearbeiten');
      return;
    }
    this.selectedBetrieb.set(betrieb);
    this.populateForm(betrieb);
    this.isEditing.set(true);
    this.showForm.set(true);
  }

  closeForm(): void {
    this.showForm.set(false);
    this.selectedBetrieb.set(null);
    this.resetForm();
  }

  onSubmit(): void {
    if (this.betriebForm.invalid) {
      this.markFormGroupTouched();
      return;
    }

    this.loading.set(true);
    const formValue = this.betriebForm.value;
    
    if (this.isEditing()) {
      const updateRequest: BetriebUpdateDto = {
        name: formValue.name,
        adresse: formValue.adresse,
        telefon: formValue.telefon,
        email: formValue.email
      };
      
      this.betriebService.updateBetrieb(this.selectedBetrieb()!.id!, updateRequest).subscribe({
        next: () => {
          this.showSuccess('Betrieb erfolgreich aktualisiert');
          this.loadBetriebe();
          this.closeForm();
        },
        error: (error) => {
          this.showError('Fehler beim Aktualisieren des Betriebs');
          console.error('Error updating Betrieb:', error);
          this.loading.set(false);
        }
      });
    } else {
      const createRequest: BetriebCreateDto = {
        name: formValue.name,
        adresse: formValue.adresse,
        telefon: formValue.telefon,
        email: formValue.email
      };
      
      this.betriebService.createBetrieb(createRequest).subscribe({
        next: () => {
          this.showSuccess('Betrieb erfolgreich erstellt');
          this.loadBetriebe();
          this.closeForm();
        },
        error: (error) => {
          this.showError('Fehler beim Erstellen des Betriebs');
          console.error('Error creating Betrieb:', error);
          this.loading.set(false);
        }
      });
    }
  }

  deleteBetrieb(betrieb: BetriebDto): void {
    if (!this.isAdmin()) {
      this.showError('Nur Administratoren können Betriebe löschen');
      return;
    }

    if (!confirm(`Möchten Sie den Betrieb "${betrieb.name}" wirklich löschen?`)) {
      return;
    }

    this.loading.set(true);
    this.betriebService.deleteBetrieb(betrieb.id!).subscribe({
      next: () => {
        this.showSuccess('Betrieb erfolgreich gelöscht');
        this.loadBetriebe();
      },
      error: (error) => {
        this.showError('Fehler beim Löschen des Betriebs');
        console.error('Error deleting Betrieb:', error);
        this.loading.set(false);
      }
    });
  }

  toggleStatus(betrieb: BetriebDto): void {
    if (!this.isAdmin()) {
      this.showError('Nur Administratoren können den Status ändern');
      return;
    }

    const newStatus = betrieb.status === BetriebStatus.AKTIV ? BetriebStatus.INAKTIV : BetriebStatus.AKTIV;
    const statusUpdate: BetriebStatusUpdateDto = { status: newStatus };
    
    this.loading.set(true);
    this.betriebService.updateBetriebStatus(betrieb.id!, statusUpdate).subscribe({
      next: () => {
        this.showSuccess(`Betrieb erfolgreich ${newStatus === BetriebStatus.AKTIV ? 'aktiviert' : 'deaktiviert'}`);
        this.loadBetriebe();
      },
      error: (error) => {
        this.showError('Fehler beim Ändern des Status');
        console.error('Error updating status:', error);
        this.loading.set(false);
      }
    });
  }

  private resetForm(): void {
    this.betriebForm.reset();
  }

  private populateForm(betrieb: BetriebDto): void {
    this.betriebForm.patchValue({
      name: betrieb.name,
      adresse: betrieb.adresse,
      telefon: betrieb.telefon,
      email: betrieb.email
    });
  }

  private markFormGroupTouched(): void {
    Object.keys(this.betriebForm.controls).forEach(key => {
      this.betriebForm.get(key)?.markAsTouched();
    });
  }

  private showSuccess(message: string): void {
    this.snackBar.open(message, 'Schließen', {
      duration: 5000,
      panelClass: ['success-snackbar']
    });
  }

  private showError(message: string): void {
    this.snackBar.open(message, 'Schließen', {
      duration: 5000,
      panelClass: ['error-snackbar']
    });
  }

  getStatusText(status: BetriebStatus): string {
    switch (status) {
      case BetriebStatus.AKTIV:
        return 'Aktiv';
      case BetriebStatus.INAKTIV:
        return 'Inaktiv';
      default:
        return 'Unbekannt';
    }
  }

  getStatusColor(status: BetriebStatus): string {
    switch (status) {
      case BetriebStatus.AKTIV:
        return 'primary';
      case BetriebStatus.INAKTIV:
        return 'warn';
      default:
        return 'accent';
    }
  }
}
