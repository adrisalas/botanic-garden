import { AfterViewInit, Component, ViewChild } from '@angular/core';
import { PlantsService } from '../../services/plants.service';
import { Plant, allMonths, initializePlant } from '../../model/plant_interface';
import { MatPaginatorModule } from '@angular/material/paginator';
import { MatSort, MatSortModule } from '@angular/material/sort';
import { MatTableDataSource, MatTableModule } from '@angular/material/table';
import { CommonModule } from '@angular/common';
import { MatInputModule } from '@angular/material/input';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatIconModule } from '@angular/material/icon';
import { MatButtonModule } from '@angular/material/button';
import { MatSidenavModule } from '@angular/material/sidenav';
import { MatSelectModule } from '@angular/material/select';
import { MatCheckboxModule } from '@angular/material/checkbox';
import { SelectionModel } from '@angular/cdk/collections';
import { FormsModule } from '@angular/forms';
import { GeneralStoreService } from '../../services/general-store.service';
import { Router } from '@angular/router';
import { animate, state, style, transition, trigger } from '@angular/animations';


@Component({
  selector: 'app-plants-page',
  standalone: true,
  animations: [
    trigger('detailExpand', [
      state('collapsed,void', style({ height: '0px', minHeight: '0' })),
      state('expanded', style({ height: '*' })),
      transition('expanded <=> collapsed', animate('225ms cubic-bezier(0.4, 0.0, 0.2, 1)')),
    ])
  ],
  imports: [MatTableModule,
    MatSortModule, MatPaginatorModule,
    CommonModule, MatInputModule, MatFormFieldModule, MatIconModule,
    MatButtonModule, MatSidenavModule, MatSelectModule, MatCheckboxModule, FormsModule],
  templateUrl: './plants-page.component.html',
  styleUrl: './plants-page.component.css'
})

export class PlantsPageComponent implements AfterViewInit {

  displayedColumns: string[] = ['select', 'id', 'commonName', 'scientificName', 'type', 'image', 'description', 'details'];
  resultsLength: number = 0;
  dataSource: MatTableDataSource<Plant> = new MatTableDataSource();
  selection = new SelectionModel<Plant>(true, []);
  isOpenCRUDNav: boolean = false;
  titleModal: string = 'Create new plant';
  modeModal: string = '';

  selectedPlant: Plant = initializePlant();
  expandedElement: Plant | null = null;

  allMonths = allMonths;

  @ViewChild(MatSort) sort: MatSort;

  constructor(
    private plantService: PlantsService,
    private generalData: GeneralStoreService,
    private router: Router) {
    if (this.generalData.tokenJWT() == "") {
      this.router.navigate(['']);
    } else {
      this.getDataPlants();
    }

    this.sort = new MatSort();
  }

  ngAfterViewInit(): void {

    this.modeModal = '';
  }

  getDataPlants() {
    this.plantService.getAllPlants().subscribe({
      next: plants => {
        if (this.dataSource.data && this.dataSource.data.length == 0) {
          this.dataSource = new MatTableDataSource(plants);
          this.dataSource.sort = this.sort;
        } else {
          this.dataSource.data = plants;
        }
        this.resultsLength = plants.length;
      },
      error: err => console.error('An error occurred', err)
    })
  }

  applyFilter(event: Event) {
    const filterValue = (event.target as HTMLInputElement).value;
    this.dataSource.filter = filterValue.trim().toLowerCase();

    if (this.dataSource.paginator) {
      this.dataSource.paginator.firstPage();
    }
  }

  /*.... CREATE .... */
  showCreate() {
    this.modeModal = 'create';
    this.titleModal = 'Create new plant';
    this.selectedPlant = initializePlant();
    this.isOpenCRUDNav = true;
  }

  /*....EDIT.... */
  showEdit() {
    this.modeModal = 'edit';
    this.titleModal = 'Edit plant';
    if (this.selection.selected.length != 1) {
      alert("Select only one record to edit");
      this.isOpenCRUDNav = false;
    } else {
      this.selectedPlant = this.selection.selected[0];
      if (!this.selectedPlant.details.flowering) {
        this.selectedPlant.details.flowering = {
          first: "",
          second: ""
        };
      }

      this.isOpenCRUDNav = true;
    }
  }

  savePlant() {
    if (this.areEmptyFields()) {
      alert("Fields marked as (*) are required.")
      return;
    }

    if (this.modeModal == 'create') {
      this.plantService.createPlant(this.selectedPlant).subscribe({
        next: result => {
          alert("Successfully created plant.");
          this.isOpenCRUDNav = false;
          this.getDataPlants();
        },
        error: err => console.error('An error occurred', err)
      });

    } else if (this.modeModal = 'edit') {
      this.plantService.updatePlant(this.selectedPlant).subscribe({
        next: result => {
          alert("Successfully updated plant.");
          this.isOpenCRUDNav = false;
        },
        error: err => {
          alert('An error ocurred. See console for details.')
          console.error('An error occurred', err)
        }
      });;

    }
  }

  deletePlant() {
    if (this.selection.selected.length != 1) {
      alert("Select only one record to delete");
    } else {
      const selectedPlant: Plant = this.selection.selected[0];
      let confirmationText = `Are you sure you want to delete ${selectedPlant.commonName}?`;
      if (confirm(confirmationText)) {
        this.plantService.deletePlant(this.selection.selected[0].id as number).subscribe({
          next: result => {
            alert("Successfully deleted plant.");
            this.getDataPlants();
            this.selection = new SelectionModel<Plant>(true, []);
          },
          error: err => {
            alert('An error ocurred. See console for details.')
            console.error('An error occurred', err)
          }
        });;
      }

    }

  }

  areEmptyFields(): boolean {
    if (this.selectedPlant.commonName == "" ||
      this.selectedPlant.scientificName == "" ||
      this.selectedPlant.description == "" ||
      this.selectedPlant.type == ""
    ) {
      return true;
    }

    return false;
  }

  /*....TABLE SELECTION.... */
  isAllSelected() {
    const numSelected = this.selection.selected.length;
    const numRows = this.dataSource.data.length;
    return numSelected === numRows;
  }

  toggleAllRows() {
    if (this.isAllSelected()) {
      this.selection.clear();
      return;
    }

    this.selection.select(...this.dataSource.data);
  }

  isImageUrl(image: string): boolean {
    return (image.includes("https") || image.includes(".com") || image.includes("www"))
  }

}

