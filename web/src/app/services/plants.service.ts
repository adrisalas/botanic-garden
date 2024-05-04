import { HttpClient, HttpHeaders } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { environment } from '../../environments/environment';
import { Observable } from 'rxjs';
import { Plant, checkOrSetNullValue } from '../model/plant_interface';
import { GeneralStoreService } from './general-store.service';

@Injectable({
  providedIn: 'root'
})
export class PlantsService {

  constructor(private _httpClient: HttpClient,
    private generalData: GeneralStoreService) { }

  getAllPlants(): Observable<Plant[]> {
    return this._httpClient.get<Plant[]>(environment.urlPlants);
  }

  createPlant(plant: Plant): Observable<any> {
    delete plant.id;
    var headers = new HttpHeaders({
      "Content-Type": "application/json",
      "Accept": "application/json",
      "Authorization": this.generalData.getAuthorizationHeader()
    });

    checkOrSetNullValue("season", plant);
    checkOrSetNullValue("first", plant);
    checkOrSetNullValue("second", plant);

    return this._httpClient.post<any>(environment.urlPlants, JSON.stringify(plant), {
      headers: headers
    });
  }

  updatePlant(plant: Plant): Observable<any> {
    var headers = new HttpHeaders({
      "Content-Type": "application/json",
      "Accept": "application/json",
      "Authorization": this.generalData.getAuthorizationHeader()
    });

    checkOrSetNullValue("season", plant);
    checkOrSetNullValue("first", plant);
    checkOrSetNullValue("second", plant);

    return this._httpClient.put<any>(environment.urlPlants + "/" + plant.id, JSON.stringify(plant), {
      headers: headers
    });
  }

  deletePlant(id: number): Observable<any> {
    var headers = new HttpHeaders({
      "Authorization": this.generalData.getAuthorizationHeader()
    });

    return this._httpClient.delete<any>(environment.urlPlants + "/" + id, {
      headers
    });
  }

  findPlantById(id: number): Observable<Plant> {
    return this._httpClient.get<Plant>(environment.urlPlants + "/" + id);
  }

}
