import { HttpClient, HttpHeaders } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { GeneralStoreService } from './general-store.service';
import { Observable } from 'rxjs';
import { Beacon, returnBeaconWithNullItem } from '../model/beacon_interface';
import { environment } from '../../environments/environment.development';

@Injectable({
  providedIn: 'root'
})
export class BeaconsService {

  constructor(private _httpClient: HttpClient,
    private generalData: GeneralStoreService) { }

  getAllBeacons(): Observable<Beacon[]> {
    return this._httpClient.get<Beacon[]>(environment.urlBeacons);
  }

  createBeacon(beacon: Beacon): Observable<any> {
    let jsonRequest;
    var headers = new HttpHeaders({
      "Content-Type": "application/json",
      "Accept": "application/json",
      "Authorization": this.generalData.getAuthorizationHeader()
    });

    if (beacon.item.type == "") {
      jsonRequest = returnBeaconWithNullItem(beacon);
    } else {
      jsonRequest = beacon;
    }


    return this._httpClient.post<any>(environment.urlBeacons, JSON.stringify(jsonRequest), {
      headers: headers
    });
  }

  updateBeacon(beacon: Beacon): Observable<any> {
    debugger;
    let jsonRequest;
    var headers = new HttpHeaders({
      "Content-Type": "application/json",
      "Accept": "application/json",
      "Authorization": this.generalData.getAuthorizationHeader()
    });

    if (beacon.item.type == "") {
      jsonRequest = returnBeaconWithNullItem(beacon);
    } else {
      jsonRequest = beacon;
    }

    return this._httpClient.put<any>(environment.urlBeacons, JSON.stringify(jsonRequest), {
      headers: headers
    });
  }

  deleteBeacon(id: string): Observable<any> {
    var headers = new HttpHeaders({
      "Authorization": this.generalData.getAuthorizationHeader()
    });

    return this._httpClient.delete<any>(environment.urlBeacons + "/" + id, {
      headers
    });
  }
}
