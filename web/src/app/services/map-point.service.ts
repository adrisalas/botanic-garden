import { HttpClient, HttpHeaders } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { GeneralStoreService } from './general-store.service';
import { Map_Point } from '../model/map_interfaces';
import { environment } from '../../environments/environment.development';
import { Observable } from 'rxjs';

@Injectable({
  providedIn: 'root'
})
export class MapPointService {

  constructor(private _httpClient: HttpClient,
    private generalData: GeneralStoreService) {
  }

  findAllPoints() {
    return this._httpClient.get<Map_Point[]>(environment.urlMapPoints);
  }

  createMapPoint(point: Map_Point): Observable<any> {
    delete point.id;
    var headers = new HttpHeaders({
      "Content-Type": "application/json",
      "Accept": "application/json",
      "Authorization": this.generalData.getAuthorizationHeader()
    });

    return this._httpClient.post<any>(environment.urlMapPoints, JSON.stringify(point), {
      headers: headers
    });
  }

  updateMapPoint(point: Map_Point): Observable<any> {
    var headers = new HttpHeaders({
      "Content-Type": "application/json",
      "Accept": "application/json",
      "Authorization": this.generalData.getAuthorizationHeader()
    });

    return this._httpClient.put<any>(environment.urlMapPoints + "/" + point.id, JSON.stringify(point), {
      headers: headers
    });
  }

  deleteMapPoint(id: number): Observable<any> {
    var headers = new HttpHeaders({
      "Authorization": this.generalData.getAuthorizationHeader()
    });

    return this._httpClient.delete<any>(environment.urlMapPoints + "/" + id, {
      headers
    });
  }

}
