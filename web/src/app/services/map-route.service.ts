import { HttpClient, HttpHeaders } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { GeneralStoreService } from './general-store.service';
import { Map_Route, Map_Route_Create } from '../model/map_interfaces';
import { environment } from '../../environments/environment.development';
import { Observable } from 'rxjs';

@Injectable({
  providedIn: 'root'
})
export class MapRouteService {

  constructor(
    private _httpClient: HttpClient,
    private generalData: GeneralStoreService) { }

  findAllRoutes() {
    return this._httpClient.get<Map_Route[]>(environment.urlMapRoutes);
  }

  createMapRoute(route: Map_Route_Create): Observable<any> {
    var headers = new HttpHeaders({
      "Content-Type": "application/json",
      "Accept": "application/json",
      "Authorization": this.generalData.getAuthorizationHeader()
    });

    return this._httpClient.post<any>(environment.urlMapRoutes, JSON.stringify(route), {
      headers: headers
    });
  }

  deleteMapRoute(idRoute: number): Observable<any> {
    var headers = new HttpHeaders({
      "Authorization": this.generalData.getAuthorizationHeader()
    });

    return this._httpClient.delete<any>(environment.urlMapRoutes + "/" + idRoute, {
      headers
    });
  }

}
