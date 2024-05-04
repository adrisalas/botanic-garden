import { HttpClient, HttpHeaders } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { GeneralStoreService } from './general-store.service';
import { Observable } from 'rxjs';
import { POI } from '../model/poi_interface';
import { environment } from '../../environments/environment.development';

@Injectable({
  providedIn: 'root'
})
export class PoiService {

  constructor(private _httpClient: HttpClient,
    private generalData: GeneralStoreService) { }

  getAllPOIs(): Observable<POI[]> {
    return this._httpClient.get<POI[]>(environment.urlPOIs);
  }

  createPOI(poi: POI): Observable<any> {
    delete poi.id;
    var headers = new HttpHeaders({
      "Content-Type": "application/json",
      "Accept": "application/json",
      "Authorization": this.generalData.getAuthorizationHeader()
    });

    return this._httpClient.post<any>(environment.urlPOIs, JSON.stringify(poi), {
      headers: headers
    });
  }

  updatePOI(poi: POI): Observable<any> {
    var headers = new HttpHeaders({
      "Content-Type": "application/json",
      "Accept": "application/json",
      "Authorization": this.generalData.getAuthorizationHeader()
    });

    return this._httpClient.put<any>(environment.urlPOIs + "/" + poi.id, JSON.stringify(poi), {
      headers: headers
    });
  }

  deletePOI(id: number): Observable<any> {
    var headers = new HttpHeaders({
      "Authorization": this.generalData.getAuthorizationHeader()
    });

    return this._httpClient.delete<any>(environment.urlPOIs + "/" + id, {
      headers
    });
  }

  findPOIById(id: number): Observable<POI> {
    return this._httpClient.get<POI>(environment.urlPOIs + "/" + id);
  }
}
