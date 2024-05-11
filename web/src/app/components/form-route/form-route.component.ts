import { Component, ElementRef, EventEmitter, Output, ViewChild } from '@angular/core';
import { FormsModule, ReactiveFormsModule } from '@angular/forms';
import { MatButtonModule } from '@angular/material/button';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatIconModule } from '@angular/material/icon';
import { MatSelectModule } from '@angular/material/select';
import { Map_Point, Map_Route, Map_Route_Create, initializeMapRoute } from '../../model/map_interfaces';
import { MapPointService } from '../../services/map-point.service';
import { MapRouteService } from '../../services/map-route.service';
import { MatAutocompleteModule, MatAutocompleteSelectedEvent } from '@angular/material/autocomplete';
import { MatChipsModule } from '@angular/material/chips';
import { MatInputModule } from '@angular/material/input';
import { AsyncPipe } from '@angular/common';

@Component({
  selector: 'app-form-route',
  standalone: true,
  imports: [MatInputModule, MatFormFieldModule, MatIconModule, MatButtonModule, FormsModule, MatSelectModule,
    MatChipsModule, MatAutocompleteModule, ReactiveFormsModule, AsyncPipe],
  templateUrl: './form-route.component.html',
  styleUrls: ['./form-route.component.css', '../../styles/mapFormStyles.css']
})
export class FormRouteComponent {

  @Output() openFormRoute = new EventEmitter<boolean>();
  @Output() loadRouteInMap = new EventEmitter<Map_Route>();
  @Output() cleanMarkers = new EventEmitter<void>();

  isCreate: boolean = true;

  route: Map_Route;
  allRoutes: Map_Route[] = [];

  //Chips Points
  allPoints: Map_Point[] = [];
  filteredPoints: Map_Point[] | undefined;
  selectedPoints: Map_Point[] = [];
  @ViewChild('pointInput')
  pointInput!: ElementRef<HTMLInputElement>;

  constructor(
    private pointService: MapPointService,
    private routeService: MapRouteService) {
    this.route = initializeMapRoute();
    this.loadAllPoints();
    this.loadAllRoutes();
  }

  loadAllPoints() {
    this.pointService.findAllPoints().subscribe({
      next: result => { this.allPoints = result; this.filteredPoints = result },
      error: err => console.error('An error occurred loading all points', err.reason)
    });
  }

  showCreate() {
    this.isCreate = true;
    this.cleanMarkers.emit();
    this.route = initializeMapRoute();
  }

  showEdit() {
    this.isCreate = false;
    this.route = initializeMapRoute();
  }

  loadAllRoutes() {
    this.routeService.findAllRoutes().subscribe({
      next: result => { this.allRoutes = result; },
      error: err => alert("An error occurred reading all routes: " + err.reason)
    });
  }

  closeMenu() {
    this.openFormRoute.emit(false);
  }

  createRoute() {
    if (this.route.name == "") {
      alert("Name is required.");
      return;
    }
    if (this.selectedPoints.length < 2) {
      alert("At least 2 points are required.");
      return;
    }

    let routeCreate: Map_Route_Create = {
      name: this.route.name,
      points: []
    }
    this.selectedPoints.forEach(point => {
      routeCreate.points.push(point.id as number);
    });

    this.routeService.createMapRoute(routeCreate).subscribe({
      next: result => {
        alert("Route created successfully.");
        this.route = initializeMapRoute();
        this.selectedPoints = [];
        this.filteredPoints = this.allPoints;
        this.loadAllRoutes();
      },
      error: err => alert("An error occurred: " + err.reason)
    });

  }

  deleteRoute() {
    if (this.route.name != "" && this.route.id) {
      if (confirm(`Are you sure you want to delete ${this.route.name}?`)) {
        this.routeService.deleteMapRoute(this.route.id).subscribe({
          next: result => {
            const index = this.allRoutes.indexOf(this.route);
            if (index >= 0) {
              this.allRoutes.splice(index, 1);
            }
            this.cleanMarkers.emit();
            this.route = initializeMapRoute();
          },
          error: err => alert("An error occurred: " + err.reason)
        })
      }
    }

  }

  showRouteInMap() {
    if (this.route.name != "") {
      this.loadRouteInMap.emit(this.route);
    }
  }

  /*.....................CRUD ITEMS POINTS.........................*/
  removePoint(point: Map_Point): void {
    const index = this.selectedPoints.indexOf(point);

    if (index >= 0) {
      this.selectedPoints.splice(index, 1);
    }

    this.filteredPoints = this.allPoints;
  }

  selectedPoint(event: MatAutocompleteSelectedEvent): void {
    const idPointSelected = event.option.viewValue;
    const selectedPoint = this.allPoints.find(point => point.id == parseInt(idPointSelected));

    if (selectedPoint) {
      this.selectedPoints.push(selectedPoint);
    }

    this.pointInput.nativeElement.value = '';
    this.filteredPoints = this.allPoints;
  }

  filterPoints(event: any) {
    let value = event.target.value;
    if (value != "") {
      this.filteredPoints = this.filterAllPoints(value.toLowerCase());
    } else {
      this.filteredPoints = this.allPoints;
    }
  }

  filterAllPoints(filterValue: string): Map_Point[] {
    return this.allPoints.filter((point: any) => {
      if (point.id && point.id == filterValue) {
        return true;
      }
      return false;
    });
  }

}
