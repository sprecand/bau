import { Component, inject, signal, computed, LOCALE_ID, OnInit, Injectable } from '@angular/core';
import { CommonModule, registerLocaleData } from '@angular/common';
import { ReactiveFormsModule, FormBuilder, FormGroup, Validators } from '@angular/forms';
import { MatCardModule } from '@angular/material/card';
import { MatButtonModule } from '@angular/material/button';
import { MatIconModule } from '@angular/material/icon';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatInputModule } from '@angular/material/input';
import { MatSelectModule } from '@angular/material/select';
import { MatDatepickerModule } from '@angular/material/datepicker';
import { MatNativeDateModule, MAT_DATE_LOCALE, NativeDateAdapter, DateAdapter } from '@angular/material/core';
import { MatCheckboxModule } from '@angular/material/checkbox';
import { MatSnackBarModule, MatSnackBar } from '@angular/material/snack-bar';
import { BedarfService } from '../../services/bedarf';
import { AuthService } from '../../services/auth.service';
import { BedarfDto, BedarfCreateDto, BedarfUpdateDto, BedarfStatus, BedarfStatusUpdateDto } from '../../models/bedarf.model';
import localeDe from '@angular/common/locales/de';

registerLocaleData(localeDe);

// Simple custom date adapter for Swiss format
@Injectable()
export class SwissDateAdapter extends NativeDateAdapter {
  override format(date: Date): string {
    const day = date.getDate().toString().padStart(2, '0');
    const month = (date.getMonth() + 1).toString().padStart(2, '0');
    const year = date.getFullYear();
    return `${day}.${month}.${year}`;
  }
}

// Custom date formats for Swiss format (DD.MM.YYYY)
export const SWISS_DATE_FORMATS = {
  parse: {
    dateInput: 'DD.MM.YYYY',
  },
  display: {
    dateInput: 'DD.MM.YYYY',
    monthYearLabel: 'MMM YYYY',
    dateA11yLabel: 'LL',
    monthYearA11yLabel: 'MMMM YYYY',
  },
};

@Component({
  selector: 'app-bedarfe',
  standalone: true,
  imports: [
    CommonModule,
    ReactiveFormsModule,
    MatCardModule,
    MatButtonModule,
    MatIconModule,
    MatFormFieldModule,
    MatInputModule,
    MatSelectModule,
    MatDatepickerModule,
    MatNativeDateModule,
    MatCheckboxModule,
    MatSnackBarModule
  ],
  providers: [
    { provide: DateAdapter, useClass: SwissDateAdapter },
    { provide: MAT_DATE_LOCALE, useValue: 'de-CH' },
    { provide: LOCALE_ID, useValue: 'de-CH' }
  ],
  templateUrl: './bedarfe.html'
})
export class BedarfeComponent implements OnInit {
  private readonly bedarfService = inject(BedarfService);
  private readonly authService = inject(AuthService);
  private readonly formBuilder = inject(FormBuilder);
  private readonly snackBar = inject(MatSnackBar);

  // Signals for state management
  readonly bedarfe = signal<BedarfDto[]>([]);
  readonly loading = signal(false);
  readonly selectedBedarf = signal<BedarfDto | null>(null);
  readonly showForm = signal(false);
  readonly isEditing = signal(false);

  // Computed values
  readonly filteredBedarfe = computed(() => {
    return this.bedarfe().filter(bedarf => bedarf.status === BedarfStatus.AKTIV);
  });

  readonly displayedColumns = ['titel', 'betriebName', 'adresse', 'datumVon', 'datumBis', 'anzahlArbeiter', 'status', 'actions'];

  // Form
  bedarfForm: FormGroup;

  // Enums for template
  readonly BedarfStatus = BedarfStatus;

  constructor() {
    this.bedarfForm = this.formBuilder.group({
      titel: ['', [Validators.required, Validators.minLength(3)]],
      beschreibung: [''], // Optional - now includes qualifications
      adresse: ['', Validators.required],
      datumVon: ['', Validators.required],
      datumBis: ['', Validators.required],
      stundenProTag: [8, [Validators.required, Validators.min(1), Validators.max(12)]],
      stundenlohn: ['', [Validators.min(0)]],
      zimmermannAnzahl: [0, [Validators.min(0)]],
      holzbauAnzahl: [0, [Validators.min(0)]],
      mitWerkzeug: [false],
      mitFahrzeug: [false]
    });
  }

  ngOnInit(): void {
    this.loadBedarfe();
  }

  loadBedarfe(): void {
    this.loading.set(true);
    this.bedarfService.getBedarfe().subscribe({
      next: (response) => {
        this.bedarfe.set(response.content);
        this.loading.set(false);
      },
      error: (error) => {
        this.showError('Fehler beim Laden der Bedarfe');
        console.error('Error loading Bedarfe:', error);
        this.loading.set(false);
      }
    });
  }

  openCreateForm(): void {
    this.resetForm();
    this.isEditing.set(false);
    this.showForm.set(true);
  }

  openEditForm(bedarf: BedarfDto): void {
    this.selectedBedarf.set(bedarf);
    this.populateForm(bedarf);
    this.isEditing.set(true);
    this.showForm.set(true);
  }

  closeForm(): void {
    this.showForm.set(false);
    this.selectedBedarf.set(null);
    this.resetForm();
  }

  onSubmit(): void {
    if (this.loading()) return;

    // Always mark all fields as touched to show validation errors
    this.markFormGroupTouched();

    if (this.bedarfForm.invalid) {
      this.showError('Bitte füllen Sie alle erforderlichen Felder korrekt aus');
      return;
    }

    this.loading.set(true);
    const formValue = this.bedarfForm.value;
    const totalArbeiter = (formValue.zimmermannAnzahl || 0) + (formValue.holzbauAnzahl || 0);
    
    if (this.isEditing()) {
      const updateRequest: BedarfUpdateDto = {
        titel: formValue.titel,
        beschreibung: formValue.beschreibung,
        adresse: formValue.adresse,
        datumVon: this.formatDateForApi(formValue.datumVon ? new Date(formValue.datumVon) : null),
        datumBis: this.formatDateForApi(formValue.datumBis ? new Date(formValue.datumBis) : null),
        zimmermannAnzahl: formValue.zimmermannAnzahl,
        holzbauAnzahl: formValue.holzbauAnzahl,
        anzahlArbeiter: totalArbeiter,
        stundenProTag: formValue.stundenProTag,
        qualifikationen: formValue.beschreibung ? 
          formValue.beschreibung.split(',').map((q: string) => q.trim()).filter((q: string) => q.length > 0) : [],
        mitWerkzeug: formValue.mitWerkzeug,
        mitFahrzeug: formValue.mitFahrzeug,
        stundenlohn: formValue.stundenlohn
      };
      
      this.bedarfService.updateBedarf(this.selectedBedarf()!.id!, updateRequest).subscribe({
        next: () => {
          this.showSuccess('Bedarf erfolgreich aktualisiert');
          this.loadBedarfe();
          this.closeForm();
        },
        error: (error) => {
          this.showError('Fehler beim Aktualisieren des Bedarfs');
          console.error('Error updating Bedarf:', error);
          this.loading.set(false);
        }
      });
    } else {
      // Get the current user for betriebId
      const currentUser = this.authService.effectiveUser();
      if (!currentUser) {
        this.showError('Benutzer nicht authentifiziert');
        this.loading.set(false);
        return;
      }

      const createRequest: BedarfCreateDto = {
        betriebId: this.authService.effectiveUser()?.betriebId || 'mock-betrieb-id',
        titel: formValue.titel,
        beschreibung: formValue.beschreibung,
        adresse: formValue.adresse,
        datumVon: this.formatDateForApi(formValue.datumVon ? new Date(formValue.datumVon) : null),
        datumBis: this.formatDateForApi(formValue.datumBis ? new Date(formValue.datumBis) : null),
        zimmermannAnzahl: formValue.zimmermannAnzahl,
        holzbauAnzahl: formValue.holzbauAnzahl,
        anzahlArbeiter: totalArbeiter,
        stundenProTag: formValue.stundenProTag,
        qualifikationen: formValue.beschreibung ? 
          formValue.beschreibung.split(',').map((q: string) => q.trim()).filter((q: string) => q.length > 0) : [],
        mitWerkzeug: formValue.mitWerkzeug,
        mitFahrzeug: formValue.mitFahrzeug,
        stundenlohn: formValue.stundenlohn
      };
      
      console.log('Creating Bedarf with request:', createRequest);
      console.log('Current user:', this.authService.effectiveUser());
      
      this.bedarfService.createBedarf(createRequest).subscribe({
        next: () => {
          this.showSuccess('Bedarf erfolgreich erstellt');
          this.loadBedarfe();
          this.closeForm();
        },
        error: (error) => {
          this.showError('Fehler beim Erstellen des Bedarfs');
          console.error('Error creating Bedarf:', error);
          this.loading.set(false);
        }
      });
    }
  }

  deleteBedarf(bedarf: BedarfDto): void {
    if (!confirm(`Möchten Sie den Bedarf "${bedarf.titel}" wirklich löschen?`)) {
      return;
    }

    this.loading.set(true);
    this.bedarfService.deleteBedarf(bedarf.id!).subscribe({
      next: () => {
        this.showSuccess('Bedarf erfolgreich gelöscht');
        this.loadBedarfe();
      },
      error: (error) => {
        this.showError('Fehler beim Löschen des Bedarfs');
        console.error('Error deleting Bedarf:', error);
        this.loading.set(false);
      }
    });
  }

  toggleStatus(bedarf: BedarfDto): void {
    const newStatus = bedarf.status === BedarfStatus.AKTIV ? BedarfStatus.INAKTIV : BedarfStatus.AKTIV;
    const statusUpdate: BedarfStatusUpdateDto = { status: newStatus };
    
    this.loading.set(true);
    this.bedarfService.updateBedarfStatus(bedarf.id!, statusUpdate).subscribe({
      next: () => {
        this.showSuccess(`Bedarf ${newStatus === BedarfStatus.AKTIV ? 'aktiviert' : 'deaktiviert'}`);
        this.loadBedarfe();
      },
      error: (error) => {
        this.showError('Fehler beim Ändern des Status');
        console.error('Error updating status:', error);
        this.loading.set(false);
      }
    });
  }

  private resetForm(): void {
    this.bedarfForm.reset({
      zimmermannAnzahl: 0,
      holzbauAnzahl: 0,
      stundenProTag: 8,
      mitWerkzeug: false,
      mitFahrzeug: false
    });
  }

  private populateForm(bedarf: BedarfDto): void {
    this.bedarfForm.patchValue({
      titel: bedarf.titel,
      beschreibung: bedarf.beschreibung,
      adresse: bedarf.adresse,
      datumVon: bedarf.datumVon ? new Date(bedarf.datumVon) : null,
      datumBis: bedarf.datumBis ? new Date(bedarf.datumBis) : null,
      zimmermannAnzahl: bedarf.zimmermannAnzahl || 0,
      holzbauAnzahl: bedarf.holzbauAnzahl || 0,
      stundenProTag: bedarf.stundenProTag,
      mitWerkzeug: bedarf.mitWerkzeug,
      mitFahrzeug: bedarf.mitFahrzeug || false,
      stundenlohn: bedarf.stundenlohn
    });
  }

  private markFormGroupTouched(): void {
    Object.keys(this.bedarfForm.controls).forEach(key => {
      this.bedarfForm.get(key)?.markAsTouched();
    });
  }

  private showSuccess(message: string): void {
    this.snackBar.open(message, 'Schließen', {
      duration: 3000,
      panelClass: ['success-snackbar']
    });
  }

  private showError(message: string): void {
    this.snackBar.open(message, 'Schließen', {
      duration: 5000,
      panelClass: ['error-snackbar']
    });
  }

  // Helper methods for template
  getStatusText(status: BedarfStatus): string {
    switch (status) {
      case BedarfStatus.AKTIV:
        return 'Aktiv';
      case BedarfStatus.INAKTIV:
        return 'Inaktiv';
      case BedarfStatus.ABGESCHLOSSEN:
        return 'Abgeschlossen';
      default:
        return 'Unbekannt';
    }
  }

  getStatusColor(status: BedarfStatus): string {
    switch (status) {
      case BedarfStatus.AKTIV:
        return 'primary';
      case BedarfStatus.INAKTIV:
        return 'warn';
      case BedarfStatus.ABGESCHLOSSEN:
        return 'accent';
      default:
        return 'accent';
    }
  }

  formatDate(dateString: string): string {
    if (!dateString) return '';
    const date = new Date(dateString);
    const day = date.getDate().toString().padStart(2, '0');
    const month = (date.getMonth() + 1).toString().padStart(2, '0');
    const year = date.getFullYear();
    return `${day}.${month}.${year}`;
  }

  private formatDateForApi(date: Date | null): string {
    if (!date) return '';
    const year = date.getFullYear();
    const month = (date.getMonth() + 1).toString().padStart(2, '0');
    const day = date.getDate().toString().padStart(2, '0');
    return `${year}-${month}-${day}`;
  }

  /**
   * Contact company for job application
   */
  contactCompany(bedarf: BedarfDto): void {
    const message = `Interesse an: ${bedarf.titel}\n\nFirma: ${bedarf.betriebName || 'Unbekannt'}\nStandort: ${bedarf.adresse}\nZeitraum: ${this.formatDate(bedarf.datumVon)} - ${this.formatDate(bedarf.datumBis)}\n\nBitte kontaktieren Sie uns für weitere Informationen oder um Ihre Bewerbung einzureichen.`;
    
    // Show contact information dialog
    this.showContactDialog(bedarf, message);
  }

  /**
   * Check if current user can edit this bedarf
   */
  canEditBedarf(bedarf: BedarfDto): boolean {
    const user = this.authService.effectiveUser();
    if (!user) return false;
    
    // Admins can edit all bedarfs
    if (user.role === 'ADMIN') return true;
    
    // Betrieb users can only edit their own bedarfs
    if (user.role === 'BETRIEB' && user.betriebId === bedarf.betriebId) return true;
    
    return false;
  }

  /**
   * Show contact information dialog
   */
  private showContactDialog(bedarf: BedarfDto, message: string): void {
    const dialogMessage = `
      Kontaktaufnahme für: ${bedarf.titel}
      
      Firma: ${bedarf.betriebName || 'Unbekannt'}
      Standort: ${bedarf.adresse}
      
      Für die Kontaktaufnahme haben Sie folgende Möglichkeiten:
      
      1. Direkte Kontaktaufnahme über die Plattform-Administratoren
      2. Telefonische Anfrage über die Hauptnummer
      3. E-Mail-Anfrage mit Referenz auf diesen Bedarf
      
      Soll eine Kontaktanfrage versendet werden?
    `;
    
    if (confirm(dialogMessage)) {
      this.showSuccess('Kontaktanfrage wurde versendet. Sie werden in Kürze kontaktiert.');
      console.log('Contact request for:', bedarf.titel, 'Company:', bedarf.betriebName);
    }
  }
}
