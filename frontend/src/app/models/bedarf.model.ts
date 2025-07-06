export interface BedarfDto {
  id?: string;
  betriebId: string;
  betriebName?: string;  // Company name from API spec
  titel?: string;
  beschreibung?: string;
  datumVon: string;
  datumBis: string;
  zimmermannAnzahl?: number;
  holzbauAnzahl?: number;
  anzahlArbeiter?: number;
  stundenProTag?: number;
  stundenlohn?: number;
  qualifikationen?: string[];
  mitWerkzeug: boolean;
  mitFahrzeug?: boolean;
  adresse: string;
  status: BedarfStatus;
  createdAt?: string;
  updatedAt?: string;
}

export interface BedarfCreateDto {
  betriebId?: string;
  titel?: string;
  beschreibung?: string;
  datumVon: string;
  datumBis: string;
  zimmermannAnzahl?: number;
  holzbauAnzahl?: number;
  anzahlArbeiter?: number;
  stundenProTag?: number;
  stundenlohn?: number;
  qualifikationen?: string[];
  mitWerkzeug: boolean;
  mitFahrzeug?: boolean;
  adresse: string;
}

export interface BedarfUpdateDto {
  titel?: string;
  beschreibung?: string;
  datumVon?: string;
  datumBis?: string;
  zimmermannAnzahl?: number;
  holzbauAnzahl?: number;
  anzahlArbeiter?: number;
  stundenProTag?: number;
  stundenlohn?: number;
  qualifikationen?: string[];
  mitWerkzeug?: boolean;
  mitFahrzeug?: boolean;
  adresse?: string;
}

export interface BedarfStatusUpdateDto {
  status: BedarfStatus;
}

export enum BedarfStatus {
  AKTIV = 'AKTIV',
  INAKTIV = 'INAKTIV',
  ABGESCHLOSSEN = 'ABGESCHLOSSEN'
}

export interface BedarfSearchParams {
  page?: number;
  size?: number;
  sort?: string;
  titel?: string;
  standort?: string;
  minStundenlohn?: number;
  maxStundenlohn?: number;
  qualifikationen?: string[];
  status?: BedarfStatus;
} 