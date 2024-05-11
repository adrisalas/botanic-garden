import { AfterViewInit, Component } from '@angular/core';
import { LeafletModule } from '@asymmetrik/ngx-leaflet';
import * as L from 'leaflet';
import { MapService } from '../../services/map.service';
import { Router } from '@angular/router';
import { GeneralStoreService } from '../../services/general-store.service';
import { MatIconModule } from '@angular/material/icon';
import { MatButtonModule } from '@angular/material/button';
import { Map_Path, Map_Point, Map_Route, initializeMapPoint } from '../../model/map_interfaces';
import { FormPointComponent } from '../../components/form-point/form-point.component';
import { FormPathComponent } from '../../components/form-path/form-path.component';
import { BehaviorSubject } from 'rxjs';
import { MapPointService } from '../../services/map-point.service';
import { MapPathService } from '../../services/map-path.service';
import { MapRouteService } from '../../services/map-route.service';
import { FormRouteComponent } from '../../components/form-route/form-route.component';


@Component({
  selector: 'app-maps-page',
  standalone: true,
  imports: [LeafletModule, MatIconModule, MatButtonModule, FormPointComponent, FormPathComponent, FormRouteComponent],
  templateUrl: './maps-page.component.html',
  styleUrl: './maps-page.component.css'
})
export class MapsPageComponent implements AfterViewInit {
  map: any;
  allPoints: Map_Point[] = [];
  allPaths: Map_Path[] = [];

  allMarkers: L.CircleMarker[] = [];

  allRoutes: Map_Route[] = [];
  markersRoutes: L.Polyline[] = [];

  pointFormIsOpened: boolean = false;
  pathFormIsOpened: boolean = false;
  routeFormIsOpened: boolean = false;

  currentPoint: Map_Point;
  observableCurrentPoint: BehaviorSubject<Map_Point>;

  constructor(
    private mapService: MapService,
    private mapPointService: MapPointService,
    private mapPathService: MapPathService,
    private router: Router,
    private generalData: GeneralStoreService
  ) {
    if (this.generalData.tokenJWT() == "") {
      this.router.navigate(['']);
    }
    this.currentPoint = initializeMapPoint();
    this.observableCurrentPoint = new BehaviorSubject<Map_Point>(initializeMapPoint());
  }

  ngAfterViewInit(): void {
    this.map = L.map('map').setView(this.mapService.initialCoordinates, this.mapService.initialZoom);

    L.tileLayer('https://tile.openstreetmap.org/{z}/{x}/{y}.png', {
      maxZoom: 19,
      attribution: '&copy; <a href="http://www.openstreetmap.org/copyright">OpenStreetMap</a>'
    }).addTo(this.map);

    this.loadAllPoints();

    const handleClick = (e: L.LeafletMouseEvent) => {
      this.currentPoint!.lat = parseFloat(e.latlng.lat.toFixed(5));
      this.currentPoint!.lon = parseFloat(e.latlng.lng.toFixed(5));
      this.observableCurrentPoint.next(this.currentPoint);
    };

    this.map.on('click', handleClick);
  }

  /*................POINTS..................... */
  openFormPoint(point?: Map_Point) {
    this.pointFormIsOpened = true;
    this.pathFormIsOpened = false;
    this.routeFormIsOpened = false;
    this.cleanMarkersRoute();

    if (point != null) {
      this.currentPoint = point;
    } else {
      this.currentPoint = initializeMapPoint();
    }
    this.observableCurrentPoint.next(this.currentPoint);
  }

  setPointFormIsOpened(newValue: boolean) {
    this.pointFormIsOpened = newValue;
  }

  loadAllPoints() {
    let marker;
    this.map.eachLayer((layer: any) => {
      if (layer instanceof L.CircleMarker || layer instanceof L.Polyline) {
        layer.remove();
      }
    });

    this.mapPointService.findAllPoints().subscribe({
      next: result => {
        this.allPoints = result;
        this.allPoints.forEach(point => {
          marker = L.circleMarker([point.lat, point.lon], {
            radius: 10,
            color: '#018786',
            fillColor: '#01BAB8',
            //color: 'red',
            //fillColor: '#f03',
            fillOpacity: 0.5
          }).addTo(this.map);

          const handleClick = (e: L.LeafletMouseEvent) => {
            this.openFormPoint(point);
          };

          marker.on('click', handleClick);

          marker.bindTooltip("Id: " + point.id);
          this.allMarkers.push(marker);
        });

        this.loadAllPaths();
      },
      error: err => console.error('An error occurred', err)
    });
  }

  /*.................PATHS........................... */
  openFormPath() {
    this.pointFormIsOpened = false;
    this.pathFormIsOpened = true;
    this.routeFormIsOpened = false;
    this.cleanMarkersRoute();
  }

  setPathFormIsOpened(newValue: boolean) {
    this.pathFormIsOpened = newValue;
  }

  loadAllPaths() {
    let pathLine;
    let latlngs: L.LatLngExpression[] | L.LatLngExpression[][];

    this.mapPathService.findAllPaths().subscribe({
      next: result => {
        this.allPaths = result;
        this.allPaths.forEach(path => {
          latlngs = [
            [path.pointA.lat, path.pointA.lon],
            [path.pointB.lat, path.pointB.lon]
          ];
          pathLine = L.polyline(latlngs, { color: 'blue' }).addTo(this.map);

        });
      },
      error: err => alert("An error occurred reading all paths: " + err.reason)
    });
  }

  /*..............ROUTES................................. */
  openFormRoute() {
    this.pointFormIsOpened = false;
    this.pathFormIsOpened = false;
    this.routeFormIsOpened = true;
  }

  setRouteFormIsOpened(newValue: boolean) {
    this.routeFormIsOpened = newValue;
    if (!newValue) {
      this.cleanMarkersRoute();
    }
  }

  loadRoute(route: Map_Route) {
    let pathLine;
    let latLongPoint: [number, number];
    let latlngs: [number, number][] = [];
    this.cleanMarkersRoute();

    route.points.forEach(point => {
      if (point.lat && point.lon) {
        latLongPoint = [point.lat as number, point.lon as number];
        latlngs.push(latLongPoint);
      }
    });

    pathLine = L.polyline(latlngs, { color: 'rgba(255, 69, 0, 0.65)', weight: 10 });
    pathLine.addTo(this.map);
    this.markersRoutes.push(pathLine);
  }

  cleanMarkersRoute() {
    if (this.markersRoutes && this.markersRoutes.length > 0) {
      this.markersRoutes.forEach(marker => {
        this.map.removeLayer(marker);
      });
      this.markersRoutes = [];
    }
  }

  /*...............................................*/
  clearSelectedPoint() {
    this.currentPoint = initializeMapPoint();
    this.observableCurrentPoint.next(initializeMapPoint());
  }

  deleteAllPolyLines() {
    this.map.eachLayer((layer: any) => {
      if (layer instanceof L.Polyline) {
        layer.remove();
      }
    });
  }
}



