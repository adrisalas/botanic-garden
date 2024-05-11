import { COMMA, ENTER } from '@angular/cdk/keycodes';
import { Component, ElementRef, EventEmitter, Input, OnInit, Output, ViewChild, inject } from '@angular/core';
import { Map_Point, initializeMapPoint } from '../../model/map_interfaces';
import { MatInputModule } from '@angular/material/input';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatIconModule } from '@angular/material/icon';
import { MatButtonModule } from '@angular/material/button';
import { FormControl, FormsModule, ReactiveFormsModule } from '@angular/forms';
import { AsyncPipe } from '@angular/common';
import { MatSelectModule } from '@angular/material/select';
import { Plant } from '../../model/plant_interface';
import { PlantsService } from '../../services/plants.service';
import { PoiService } from '../../services/poi.service';
import { POI } from '../../model/poi_interface';
import { MatChipInputEvent, MatChipsModule } from '@angular/material/chips';
import { MatAutocompleteSelectedEvent, MatAutocompleteModule } from '@angular/material/autocomplete';
import { BehaviorSubject, Observable, map, startWith } from 'rxjs';
import { LiveAnnouncer } from '@angular/cdk/a11y';
import { MapPointService } from '../../services/map-point.service';

@Component({
  selector: 'app-form-point',
  standalone: true,
  imports: [MatInputModule, MatFormFieldModule, MatIconModule, MatButtonModule, FormsModule, MatSelectModule,
    MatChipsModule, MatAutocompleteModule, ReactiveFormsModule, AsyncPipe],
  templateUrl: './form-point.component.html',
  styleUrls: ['./form-point.component.css', '../../styles/mapFormStyles.css']
})
export class FormPointComponent {
  point: Map_Point | undefined;
  @Input() observablePoint: BehaviorSubject<Map_Point> | undefined;
  @Output() openFormPoint = new EventEmitter<boolean>();
  @Output() clearParentPoint = new EventEmitter<boolean>();
  @Output() refreshPoints = new EventEmitter<void>();

  //Chips Plants..................................
  allPlants: Plant[] = [];
  filteredPlants: Plant[] | undefined;
  selectedPlants: Plant[] = [];
  @ViewChild('plantsInput')
  plantInput!: ElementRef<HTMLInputElement>;

  //Chips POIs....................................
  allPOIs: POI[] = [];
  filteredPOIs: POI[] | undefined;
  selectedPOIs: POI[] = [];
  @ViewChild('poisInput')
  poiInput!: ElementRef<HTMLInputElement>;

  announcer = inject(LiveAnnouncer);

  constructor(
    private plantService: PlantsService,
    private poiService: PoiService,
    private mapPointService: MapPointService
  ) {
    this.getAllData();
  }

  getAllData() {
    this.plantService.getAllPlants().subscribe({
      next: result => { this.allPlants = result; this.filteredPlants = result; this.subscribeParentPoint() },
      error: err => console.error(err)
    });

    this.poiService.getAllPOIs().subscribe({
      next: resultPOI => { this.allPOIs = resultPOI; this.filteredPOIs = resultPOI },
      error: err => console.error(err)
    });
  }

  subscribeParentPoint() {
    this.observablePoint?.subscribe({
      next: result => {
        this.point = result;
        this.selectedPOIs = [];
        this.selectedPlants = [];

        if (this.point && this.point.items) {
          this.point.items.forEach(item => {
            if (item.type == "PLANT") {
              let plant = this.allPlants.find(plant => plant.id == parseInt(item.id));
              if (plant) {
                this.selectedPlants.push(plant);
              }
            } else if (item.type == "POI") {
              let poi = this.allPOIs.find(poi => poi.id == parseInt(item.id));
              if (poi) {
                this.selectedPOIs.push(poi);
              }
            }
          });
        }
      }
    });
  }

  closeMenu() {
    this.openFormPoint.emit(false);
  }

  /*.....................CRUD ITEM PLANT.........................*/
  removePlant(plant: Plant): void {
    const index = this.selectedPlants.indexOf(plant);

    if (index >= 0) {
      this.selectedPlants.splice(index, 1);
      this.announcer.announce(`Plant Removed.`);
    }

    this.filteredPlants = this.allPlants;
    this.deleteRepeatedObjects();
  }

  selectedPlant(event: MatAutocompleteSelectedEvent): void {
    const idPlantSelected = event.option.viewValue.split("--")[0];
    const selectedPlant = this.allPlants.find(plant => plant.id == parseInt(idPlantSelected));

    if (selectedPlant) {
      this.selectedPlants.push(selectedPlant);
    }

    this.plantInput.nativeElement.value = '';
    this.filteredPlants = this.allPlants;
    this.deleteRepeatedObjects();
  }

  filterPlants(event: any) {
    let value = event.target.value;
    if (value != "") {
      this.filteredPlants = this.filterAllPlants(value.toLowerCase());

    } else {
      this.filteredPlants = this.allPlants;
    }
    this.deleteRepeatedObjects();
  }

  filterAllPlants(filterValue: string): Plant[] {
    return this.allPlants.filter((plant: any) => {
      if (plant.commonName && plant.commonName.toLowerCase().includes(filterValue)) {
        return true;
      }
      if (plant.id && plant.id == filterValue) {
        return true;
      }
      return false;
    });
  }

  /*.....................CRUD ITEM POI...................... */
  removePOI(poi: POI): void {
    const index = this.selectedPOIs.indexOf(poi);

    if (index >= 0) {
      this.selectedPOIs.splice(index, 1);
      this.announcer.announce(`POI Removed.`);
    }

    this.filteredPOIs = this.allPOIs;
    this.deleteRepeatedObjects();
  }

  selectedPOI(event: MatAutocompleteSelectedEvent): void {
    const idPOISelected = event.option.viewValue.split("--")[0];
    const selectedPOI = this.allPOIs.find(poi => poi.id == parseInt(idPOISelected));

    if (selectedPOI) {
      this.selectedPOIs.push(selectedPOI);
    }

    this.poiInput.nativeElement.value = '';
    this.filteredPOIs = this.allPOIs;
    this.deleteRepeatedObjects();
  }

  filterPOIs(event: any) {
    let value = event.target.value;
    if (value != "") {
      this.filteredPOIs = this.filterAllPOIs(value.toLowerCase());

    } else {
      this.filteredPOIs = this.allPOIs;
    }

    this.deleteRepeatedObjects();
  }

  filterAllPOIs(filterValue: string): POI[] {
    return this.allPOIs.filter((poi: POI) => {
      if (poi.name && poi.name.toLowerCase().includes(filterValue)) {
        return true;
      }
      if (poi.id && String(poi.id) == filterValue) {
        return true;
      }
      return false;
    });
  }


  /*................................................................ */
  deleteRepeatedObjects(): void {
    if (this.filteredPlants != null) {
      this.filteredPlants = this.filteredPlants.filter(plant => {
        return !this.selectedPlants.find(selectedPlant => selectedPlant.id === plant.id);
      });
    }

    if (this.filteredPOIs != null) {
      this.filteredPOIs = this.filteredPOIs.filter(poi => {
        return !this.selectedPOIs.find(selectedPoi => selectedPoi.id === poi.id);
      });
    }
  }

  clearPoint() {
    this.point = initializeMapPoint();
    this.selectedPOIs = [];
    this.selectedPlants = [];
    this.clearParentPoint.emit();
  }


  /*..................................................... */

  saveOrUpdatePoint() {
    if (this.point && this.point.lat != 0 && this.point.lon != 0) {
      this.point.items = [];

      this.selectedPOIs.forEach(poi => {
        this.point?.items?.push({
          type: "POI",
          id: poi.id + ""
        })
      });

      this.selectedPlants.forEach(plant => {
        this.point?.items?.push({
          type: "PLANT",
          id: plant.id + ""
        })
      });

      if (this.point?.id == 0) {
        this.mapPointService.createMapPoint(this.point).subscribe({
          next: result => { this.refreshPoints.emit(); this.closeMenu() },
          error: err => alert("Error creating point: " + err.reason)
        });

      } else {
        this.mapPointService.updateMapPoint(this.point as Map_Point).subscribe({
          next: result => { this.refreshPoints.emit(); this.closeMenu() },
          error: err => alert("Error updating point: " + err.reason)
        });
      }
    } else {
      alert("Latitude and longitude are required.");
    }
  }

  /*.....................................................*/
  deletePoint() {
    if (this.point && this.point.id && this.point.id != 0) {
      if (confirm("Are you sure you want to delete point " + this.point.id + "?")) {
        this.mapPointService.deleteMapPoint(this.point.id).subscribe({
          next: result => { this.refreshPoints.emit(); this.closeMenu() },
          error: err => alert("Error deleting point: " + err.reason)
        });
      }
    }
  }

}

