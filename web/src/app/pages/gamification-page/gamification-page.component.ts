import { AfterViewInit, Component, ViewChild } from '@angular/core';
import { Router } from '@angular/router';
import { GeneralStoreService } from '../../services/general-store.service';
import { MatInputModule } from '@angular/material/input';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatIconModule } from '@angular/material/icon';
import { MatButtonModule } from '@angular/material/button';
import { FormsModule, ReactiveFormsModule } from '@angular/forms';
import { MatSelectModule } from '@angular/material/select';
import { Plant, initializePlant } from '../../model/plant_interface';
import { PlantsService } from '../../services/plants.service';
import { GamificationService } from '../../services/gamification.service';
import { User_Points } from '../../model/gamification_interface';
import { MatTableDataSource, MatTableModule } from '@angular/material/table';
import { MatSort, MatSortModule } from '@angular/material/sort';
import { CommonModule } from '@angular/common';
import { MatCheckboxModule } from '@angular/material/checkbox';
import { SelectionModel } from '@angular/cdk/collections';

@Component({
  selector: 'app-gamification-page',
  standalone: true,
  imports: [MatInputModule, MatFormFieldModule, MatIconModule, MatButtonModule, FormsModule,
    MatSelectModule, ReactiveFormsModule, MatTableModule, MatSortModule, CommonModule, MatCheckboxModule],
  templateUrl: './gamification-page.component.html',
  styleUrl: './gamification-page.component.css'
})
export class GamificationPageComponent implements AfterViewInit {
  displayedColumns: string[] = ['username', 'points'];
  activePlant: Plant;
  allPlants: Plant[] = [];
  idSelectedPlant: number = -1;

  allUserPoints: User_Points[] = [];
  selection = new SelectionModel<User_Points>(true, []);
  dataSourceUsersPoints: MatTableDataSource<User_Points> = new MatTableDataSource();
  @ViewChild(MatSort) sort: MatSort;

  constructor(
    private router: Router,
    private generalData: GeneralStoreService,
    private plantService: PlantsService,
    private gamificationService: GamificationService) {
    if (this.generalData.tokenJWT() == "") {
      this.router.navigate(['']);
    }

    this.sort = new MatSort();
    this.activePlant = initializePlant();
    this.setPlantAsNotSelected();
    this.loadAllPlants();
  }

  ngAfterViewInit(): void {
    this.getDataUsersPoints();
  }

  loadAllPlants() {
    this.plantService.getAllPlants().subscribe({
      next: result => { this.allPlants = result; this.loadActivePlant(); },
      error: err => console.error("An error occurred loading all plants: " + err.reason)
    });
  }

  loadActivePlant() {
    this.gamificationService.findSelectedPlant().subscribe({
      next: result => {
        if (result && result.id) {
          this.activePlant = result;
        }
      },
      error: err => console.error("An error occurred loading active plant: " + err.reason)
    });
  }


  changeActivePlant(event: any) {
    if (this.idSelectedPlant == this.activePlant.id) {
      return;
    }

    if (this.idSelectedPlant != -1) {
      let changedPlant = this.allPlants.find(plant => plant.id === this.idSelectedPlant) as Plant;

      if (confirm("Are you sure you want to change active plant? ")) {
        this.gamificationService.updateSelectedPlant(changedPlant).subscribe({
          next: result => { this.activePlant = changedPlant; },
          error: err => console.log("An error occurred updating active plant: " + err.reason)
        });
      }
    } else {
      if (confirm("Are you sure you want to deactivate current plant? ")) {
        this.gamificationService.deactivateSelectedPlant().subscribe({
          next: result => { this.setPlantAsNotSelected(); },
          error: err => console.log("An error occurred updating active plant: " + err.reason)
        });
      }

    }
  }

  setPlantAsNotSelected() {
    this.activePlant = initializePlant();
    this.activePlant.commonName = "There is no active plant";
    this.activePlant.id = -1;
  }

  /* ................USERS POINTS................ */
  getDataUsersPoints() {
    this.gamificationService.findAllUsersPoints().subscribe({
      next: users => {
        if (this.dataSourceUsersPoints.data && this.dataSourceUsersPoints.data.length == 0) {
          this.dataSourceUsersPoints = new MatTableDataSource(users);
          this.dataSourceUsersPoints.sort = this.sort;
        } else {
          this.dataSourceUsersPoints.data = users;
        }
      },
      error: err => console.error('An error occurred', err)
    })
  }

  /* ................TABLE CONTROLS................*/
  isAllSelected() {
    const numSelected = this.selection.selected.length;
    const numRows = this.dataSourceUsersPoints.data.length;
    return numSelected === numRows;
  }

  toggleAllRows() {
    if (this.isAllSelected()) {
      this.selection.clear();
      return;
    }

    this.selection.select(...this.dataSourceUsersPoints.data);
  }

  applyFilter(event: Event) {
    const filterValue = (event.target as HTMLInputElement).value;
    this.dataSourceUsersPoints.filter = filterValue.trim().toLowerCase();

    if (this.dataSourceUsersPoints.paginator) {
      this.dataSourceUsersPoints.paginator.firstPage();
    }
  }
}
