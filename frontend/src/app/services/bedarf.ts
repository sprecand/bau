import { Injectable } from '@angular/core';
import { HttpClient, HttpParams } from '@angular/common/http';
import { Observable } from 'rxjs';
import { BedarfDto, BedarfCreateDto, BedarfUpdateDto, BedarfStatusUpdateDto, BedarfSearchParams } from '../models/bedarf.model';
import { PageResponse } from '../models/common.model';
import { environment } from '../../environments/environment';

@Injectable({
  providedIn: 'root'
})
export class BedarfService {
  private readonly apiUrl = `${environment.apiUrl}/api/v1/bedarfe`;

  constructor(private http: HttpClient) {}

  /**
   * Get all Bedarfe with pagination and filtering
   */
  getBedarfe(params?: BedarfSearchParams): Observable<PageResponse<BedarfDto>> {
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

    return this.http.get<PageResponse<BedarfDto>>(this.apiUrl, { params: httpParams });
  }

  /**
   * Get Bedarf by ID
   */
  getBedarf(id: string): Observable<BedarfDto> {
    return this.http.get<BedarfDto>(`${this.apiUrl}/${id}`);
  }

  /**
   * Create new Bedarf
   */
  createBedarf(bedarf: BedarfCreateDto): Observable<BedarfDto> {
    return this.http.post<BedarfDto>(this.apiUrl, bedarf);
  }

  /**
   * Update existing Bedarf
   */
  updateBedarf(id: string, bedarf: BedarfUpdateDto): Observable<BedarfDto> {
    return this.http.put<BedarfDto>(`${this.apiUrl}/${id}`, bedarf);
  }

  /**
   * Update Bedarf status
   */
  updateBedarfStatus(id: string, statusUpdate: BedarfStatusUpdateDto): Observable<BedarfDto> {
    return this.http.patch<BedarfDto>(`${this.apiUrl}/${id}/status`, statusUpdate);
  }

  /**
   * Delete Bedarf
   */
  deleteBedarf(id: string): Observable<void> {
    return this.http.delete<void>(`${this.apiUrl}/${id}`);
  }

  /**
   * Get Bedarfe by Betrieb ID
   */
  getBedarfeByBetrieb(betriebId: string, params?: BedarfSearchParams): Observable<PageResponse<BedarfDto>> {
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

    return this.http.get<PageResponse<BedarfDto>>(`/api/v1/bedarfe/betrieb/${betriebId}`, { params: httpParams });
  }
}
