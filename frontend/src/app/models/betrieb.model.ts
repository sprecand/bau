export interface BetriebDto {
  id?: string;
  name: string;
  email: string;
  telefon?: string;
  adresse: string;
  status: BetriebStatus;
  createdAt?: string;
  updatedAt?: string;
}

export interface BetriebCreateDto {
  name: string;
  email: string;
  telefon?: string;
  adresse: string;
}

export interface BetriebUpdateDto {
  name?: string;
  email?: string;
  telefon?: string;
  adresse?: string;
}

export interface BetriebStatusUpdateDto {
  status: BetriebStatus;
}

export enum BetriebStatus {
  AKTIV = 'AKTIV',
  INAKTIV = 'INAKTIV'
}

export interface BetriebSearchParams {
  page?: number;
  size?: number;
  sort?: string;
  name?: string;
  status?: BetriebStatus;
} 