import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { GeneralStoreService } from './general-store.service';
import { LatLngExpression } from 'leaflet';

@Injectable({
  providedIn: 'root'
})
export class MapService {

  initialCoordinates: LatLngExpression = [43.52087, -5.61553];
  initialZoom = 18;

  constructor(private _httpClient: HttpClient,
    private generalData: GeneralStoreService
  ) { }

}
