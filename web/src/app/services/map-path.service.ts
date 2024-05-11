import { HttpClient, HttpHeaders } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { GeneralStoreService } from './general-store.service';
import { environment } from '../../environments/environment.development';
import { Observable } from 'rxjs/internal/Observable';
import { Map_Path } from '../model/map_interfaces';

@Injectable({
  providedIn: 'root'
})
export class MapPathService {

  constructor(
    private _httpClient: HttpClient,
    private generalData: GeneralStoreService
  ) { }


  findAllPaths() {
    return this._httpClient.get<Map_Path[]>(environment.urlMapPaths);
  }

  createMapPath(path: Map_Path): Observable<any> {
    var headers = new HttpHeaders({
      "Content-Type": "application/json",
      "Accept": "application/json",
      "Authorization": this.generalData.getAuthorizationHeader()
    });

    return this._httpClient.post<any>(environment.urlMapPaths, JSON.stringify({ "pointAId": path.pointA.id, "pointBId": path.pointB.id }), {
      headers: headers
    });
  }

  deleteMapPath(idPointA: number, idPointB: number): Observable<any> {
    var headers = new HttpHeaders({
      "Authorization": this.generalData.getAuthorizationHeader()
    });

    return this._httpClient.delete<any>(environment.urlMapPaths + "/" + idPointA + "/" + idPointB, {
      headers
    });
  }
}
