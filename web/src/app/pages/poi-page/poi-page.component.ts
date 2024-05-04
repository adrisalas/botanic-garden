import { animate, state, style, transition, trigger } from '@angular/animations';
import { CommonModule } from '@angular/common';
import { Component, ViewChild } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { MatButtonModule } from '@angular/material/button';
import { MatCheckboxModule } from '@angular/material/checkbox';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatIconModule } from '@angular/material/icon';
import { MatInputModule } from '@angular/material/input';
import { MatSelectModule } from '@angular/material/select';
import { MatSidenavModule } from '@angular/material/sidenav';
import { MatSort, MatSortModule } from '@angular/material/sort';
import { MatTableDataSource, MatTableModule } from '@angular/material/table';
import { POI, initializePOI } from '../../model/poi_interface';
import { SelectionModel } from '@angular/cdk/collections';
import { GeneralStoreService } from '../../services/general-store.service';
import { Router } from '@angular/router';
import { PoiService } from '../../services/poi.service';

@Component({
  selector: 'app-poi-page',
  standalone: true,
  imports: [MatTableModule,
    MatSortModule, CommonModule, MatInputModule, MatFormFieldModule, MatIconModule,
    MatButtonModule, MatSidenavModule, MatSelectModule, MatCheckboxModule, FormsModule],
  animations: [
    trigger('detailExpand', [
      state('collapsed,void', style({ height: '0px', minHeight: '0' })),
      state('expanded', style({ height: '*' })),
      transition('expanded <=> collapsed', animate('225ms cubic-bezier(0.4, 0.0, 0.2, 1)')),
    ])
  ],
  templateUrl: './poi-page.component.html',
  styleUrl: './poi-page.component.css'
})
export class PoiPageComponent {
  displayedColumns: string[] = ['select', 'id', 'name', 'image', 'description'];
  resultsLength: number = 0;
  dataSourcePOIs: MatTableDataSource<POI> = new MatTableDataSource();
  selection = new SelectionModel<POI>(true, []);
  isOpenCRUDNav: boolean = false;
  titleModal: string = 'Create News';
  modeModal: string = '';

  selectedPOI: POI = initializePOI();
  expandedElement: POI | null = null;

  @ViewChild(MatSort) sort: MatSort;

  constructor(
    private poiService: PoiService,
    private generalData: GeneralStoreService,
    private router: Router) {
    if (this.generalData.tokenJWT() == "") {
      this.router.navigate(['']);
    } else {
      this.getDataPOIs();
    }

    this.sort = new MatSort();
  }

  ngAfterViewInit(): void {

    this.modeModal = '';
  }

  getDataPOIs() {
    this.poiService.getAllPOIs().subscribe({
      next: pois => {
        if (this.dataSourcePOIs.data && this.dataSourcePOIs.data.length == 0) {
          this.dataSourcePOIs = new MatTableDataSource(pois);
          this.dataSourcePOIs.sort = this.sort;
        } else {
          this.dataSourcePOIs.data = pois;
        }
        this.resultsLength = pois.length;
      },
      error: err => console.error('An error occurred', err)
    })
  }

  applyFilter(event: Event) {
    const filterValue = (event.target as HTMLInputElement).value;
    this.dataSourcePOIs.filter = filterValue.trim().toLowerCase();

    if (this.dataSourcePOIs.paginator) {
      this.dataSourcePOIs.paginator.firstPage();
    }
  }

  /*.... CREATE .... */
  showCreate() {
    this.modeModal = 'create';
    this.titleModal = 'Create News';
    this.selectedPOI = initializePOI();
    this.isOpenCRUDNav = true;
  }

  /*....EDIT.... */
  showEdit() {
    this.modeModal = 'edit';
    this.titleModal = 'Edit POI';
    if (this.selection.selected.length != 1) {
      alert("Select only one record to edit");
      this.isOpenCRUDNav = false;
    } else {
      this.selectedPOI = this.selection.selected[0];
      this.isOpenCRUDNav = true;
    }
  }

  savePOI() {
    if (this.areEmptyFields()) {
      alert("Fields marked as (*) are required.")
      return;
    }

    if (this.modeModal == 'create') {
      this.poiService.createPOI(this.selectedPOI).subscribe({
        next: result => {
          alert("Successfully created POI.");
          this.isOpenCRUDNav = false;
          this.getDataPOIs();
        },
        error: err => console.error('An error occurred', err)
      });

    } else if (this.modeModal = 'edit') {
      this.poiService.updatePOI(this.selectedPOI).subscribe({
        next: result => {
          alert("Successfully updated POI.");
          this.isOpenCRUDNav = false;
        },
        error: err => {
          alert('An error ocurred. See console for details.')
          console.error('An error occurred', err)
        }
      });;

    }
  }

  deletePOI() {
    if (this.selection.selected.length != 1) {
      alert("Select only one record to delete");
    } else {
      const selectedPOI: POI = this.selection.selected[0];
      let confirmationText = `Are you sure you want to delete ${selectedPOI.name}?`;
      if (confirm(confirmationText)) {
        this.poiService.deletePOI(this.selection.selected[0].id as number).subscribe({
          next: result => {
            alert("Successfully deleted POI.");
            this.getDataPOIs();
            this.selection = new SelectionModel<POI>(true, []);
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
    if (this.selectedPOI.name == "" ||
      this.selectedPOI.description == ""
    ) {
      return true;
    }

    return false;
  }

  /*....TABLE SELECTION.... */
  isAllSelected() {
    const numSelected = this.selection.selected.length;
    const numRows = this.dataSourcePOIs.data.length;
    return numSelected === numRows;
  }

  toggleAllRows() {
    if (this.isAllSelected()) {
      this.selection.clear();
      return;
    }

    this.selection.select(...this.dataSourcePOIs.data);
  }
}
