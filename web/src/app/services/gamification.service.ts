import { HttpClient, HttpHeaders } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { GeneralStoreService } from './general-store.service';
import { Observable } from 'rxjs';
import { Plant } from '../model/plant_interface';
import { environment } from '../../environments/environment.development';
import { User_Points } from '../model/gamification_interface';

@Injectable({
  providedIn: 'root'
})
export class GamificationService {

  constructor(
    private _httpClient: HttpClient,
    private generalData: GeneralStoreService
  ) { }

  findSelectedPlant(): Observable<Plant> {
    return this._httpClient.get<any>(environment.urlGamificationPlant);
  }

  updateSelectedPlant(plant: Plant): Observable<any> {
    var headers = new HttpHeaders({
      "Content-Type": "application/json",
      "Accept": "application/json",
      "Authorization": this.generalData.getAuthorizationHeader()
    });

    return this._httpClient.post<any>(environment.urlGamificationPlant, JSON.stringify({ "plantId": plant.id }), {
      headers: headers
    });
  }

  deactivateSelectedPlant(): Observable<any> {
    var headers = new HttpHeaders({
      "Authorization": this.generalData.getAuthorizationHeader()
    });

    return this._httpClient.delete<any>(environment.urlGamificationPlant, {
      headers
    });
  }

  findAllUsersPoints(): Observable<any> {
    var headers = new HttpHeaders({
      "Authorization": this.generalData.getAuthorizationHeader()
    });

    return this._httpClient.get<any>(environment.urlGamificationPoints, { headers: headers });
  }

}
