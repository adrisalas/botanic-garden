import { HttpClient, HttpHeaders } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { GeneralStoreService } from './general-store.service';
import { environment } from '../../environments/environment.development';
import { Observable } from 'rxjs/internal/Observable';
import { MostVisitedPOIs, MostVisitedPlants, StatVisitorsDay, StatVisitorsHour } from '../model/stats_interface';

@Injectable({
  providedIn: 'root'
})
export class StatsService {

  private urlVisitorsPerDay: string = 'visitors-per-day';
  private urlVisitorsPerHour: string = 'visitors-per-hour';
  private urlMostVisitedPlants: string = 'most-visited-plants';
  private urlMostVisitedPOIs: string = 'most-visited-pois';

  constructor(private _httpClient: HttpClient,
    private generalData: GeneralStoreService) { }

  getVisitorsPerDay(): Observable<StatVisitorsDay[]> {
    var headers = new HttpHeaders({
      "Authorization": this.generalData.getAuthorizationHeader()
    });

    return this._httpClient.get<StatVisitorsDay[]>(environment.urlStats + this.urlVisitorsPerDay, {
      headers: headers
    });
  }

  getVisitorsPerHour(): Observable<StatVisitorsHour[]> {
    var headers = new HttpHeaders({
      "Authorization": this.generalData.getAuthorizationHeader()
    });

    return this._httpClient.get<StatVisitorsHour[]>(environment.urlStats + this.urlVisitorsPerHour, {
      headers: headers
    });
  }

  getMostVisitedPlants(): Observable<MostVisitedPlants[]> {
    var headers = new HttpHeaders({
      "Authorization": this.generalData.getAuthorizationHeader()
    });

    return this._httpClient.get<MostVisitedPlants[]>(environment.urlStats + this.urlMostVisitedPlants, {
      headers: headers
    });
  }

  getMostVisitedPOIs(): Observable<MostVisitedPOIs[]> {
    var headers = new HttpHeaders({
      "Authorization": this.generalData.getAuthorizationHeader()
    });

    return this._httpClient.get<MostVisitedPOIs[]>(environment.urlStats + this.urlMostVisitedPOIs, {
      headers: headers
    });
  }

}
