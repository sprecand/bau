import { Injectable } from '@angular/core';
import { HttpClient, HttpParams } from '@angular/common/http';
import { Observable } from 'rxjs';
import { BetriebDto, BetriebCreateDto, BetriebUpdateDto, BetriebStatusUpdateDto, BetriebSearchParams } from '../models/betrieb.model';
import { PageResponse } from '../models/common.model';
import { environment } from '../../environments/environment';

@Injectable({
  providedIn: 'root'
})
export class BetriebService {
  private readonly apiUrl = `${environment.apiUrl}/api/v1/betriebe`;

  constructor(private http: HttpClient) {}

  /**
   * Get all Betriebe with pagination and filtering
   */
  getBetriebe(params?: BetriebSearchParams): Observable<PageResponse<BetriebDto>> {
    let httpParams = new HttpParams();
    
    if (params) {
      Object.keys(params).forEach(key => {
        const value = (params as any)[key];
        if (value !== undefined && value !== null) {
          if (Array.isArray(value)) {
            value.forEach(v => httpParams = httpParams.append(key, v));
          } else {
            httpParams = httpParams.set(key, value.toString());
          }
        }
      });
    }

    return this.http.get<PageResponse<BetriebDto>>(this.apiUrl, { params: httpParams });
  }

  /**
   * Get Betrieb by ID
   */
  getBetrieb(id: string): Observable<BetriebDto> {
    return this.http.get<BetriebDto>(`${this.apiUrl}/${id}`);
  }

  /**
   * Create new Betrieb
   */
  createBetrieb(betrieb: BetriebCreateDto): Observable<BetriebDto> {
    return this.http.post<BetriebDto>(this.apiUrl, betrieb);
  }

  /**
   * Update existing Betrieb
   */
  updateBetrieb(id: string, betrieb: BetriebUpdateDto): Observable<BetriebDto> {
    return this.http.put<BetriebDto>(`${this.apiUrl}/${id}`, betrieb);
  }

  /**
   * Update Betrieb status
   */
  updateBetriebStatus(id: string, statusUpdate: BetriebStatusUpdateDto): Observable<BetriebDto> {
    return this.http.patch<BetriebDto>(`${this.apiUrl}/${id}/status`, statusUpdate);
  }

  /**
   * Delete Betrieb
   */
  deleteBetrieb(id: string): Observable<void> {
    return this.http.delete<void>(`${this.apiUrl}/${id}`);
  }
}
