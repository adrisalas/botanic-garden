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
import { Beacon, initializeBeacon, initializeBeaconFromAnother } from '../../model/beacon_interface';
import { SelectionModel } from '@angular/cdk/collections';
import { BeaconsService } from '../../services/beacons.service';
import { GeneralStoreService } from '../../services/general-store.service';
import { Router } from '@angular/router';
import { PlantsService } from '../../services/plants.service';
import { PoiService } from '../../services/poi.service';

@Component({
  selector: 'app-beacons-page',
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
  templateUrl: './beacons-page.component.html',
  styleUrl: './beacons-page.component.css'
})

export class BeaconsPageComponent {
  dataSourceBeacons: MatTableDataSource<Beacon> = new MatTableDataSource();
  selection = new SelectionModel<Beacon>(true, []);
  displayedColumns: string[] = ['select', 'id', 'item'];
  resultsLength: number = 0;
  isOpenCRUDNav: boolean = false;
  titleModal: string = 'Create Beacon';
  modeModal: string = '';

  selectedBeacon: Beacon = initializeBeacon();
  expandedElement: Beacon | null = null;
  associatedItemDetails: any;
  associatedItem: any;
  selectableItems: any;

  @ViewChild(MatSort) sort: MatSort;

  constructor(
    private beaconService: BeaconsService,
    private generalData: GeneralStoreService,
    private router: Router,
    private plantService: PlantsService,
    private poiService: PoiService) {
    if (this.generalData.tokenJWT() == "") {
      this.router.navigate(['']);
    } else {
      this.getDataBeacons();
    }

    this.sort = new MatSort();
  }

  ngAfterViewInit(): void {
    this.modeModal = '';
  }

  getDataBeacons() {
    this.beaconService.getAllBeacons().subscribe({
      next: beacons => {
        if (this.dataSourceBeacons.data && this.dataSourceBeacons.data.length == 0) {
          this.dataSourceBeacons = new MatTableDataSource(beacons);
          this.dataSourceBeacons.sort = this.sort;
        } else {
          this.dataSourceBeacons.data = beacons;
        }
        this.resultsLength = beacons.length;
      },
      error: err => console.error('An error occurred', err)
    })
  }

  applyFilter(event: Event) {
    const filterValue = (event.target as HTMLInputElement).value;
    this.dataSourceBeacons.filter = filterValue.trim().toLowerCase();

    if (this.dataSourceBeacons.paginator) {
      this.dataSourceBeacons.paginator.firstPage();
    }
  }

  /*.... CREATE .... */
  showCreate() {
    this.modeModal = 'create';
    this.titleModal = 'Create Beacon';
    this.selectedBeacon = initializeBeacon();
    this.isOpenCRUDNav = true;
  }

  /*....EDIT.... */
  showEdit() {
    this.modeModal = 'edit';
    this.titleModal = 'Edit Beacon';
    if (this.selection.selected.length != 1) {
      alert("Select only one record to edit");
      this.isOpenCRUDNav = false;
    } else {
      this.selectedBeacon = initializeBeaconFromAnother(this.selection.selected[0]);
      if (this.selectedBeacon.item.id != "") {
        this.loadItemIds();
      }
      this.isOpenCRUDNav = true;
    }
  }

  saveBeacon() {
    if (this.areEmptyFields()) {
      alert("Fields marked as (*) are required.")
      return;
    }

    if (this.modeModal == 'create') {
      this.beaconService.createBeacon(this.selectedBeacon).subscribe({
        next: result => {
          alert("Successfully created beacon.");
          this.isOpenCRUDNav = false;
          this.getDataBeacons();
        },
        error: err => console.error('An error occurred', err)
      });

    } else if (this.modeModal = 'edit') {
      this.beaconService.updateBeacon(this.selectedBeacon).subscribe({
        next: result => {
          alert("Successfully updated beacon.");
          this.isOpenCRUDNav = false;
          this.getDataBeacons();
        },
        error: err => {
          alert('An error ocurred. See console for details.')
          console.error('An error occurred', err)
        }
      });;

    }
  }

  deleteBeacon() {
    if (this.selection.selected.length != 1) {
      alert("Select only one record to delete");
    } else {
      let confirmationText = `Are you sure you want to delete the selected beacon?`;
      if (confirm(confirmationText)) {
        this.beaconService.deleteBeacon(this.selection.selected[0].id).subscribe({
          next: result => {
            alert("Successfully deleted beacon.");
            this.getDataBeacons();
            this.clearSelection();
          },
          error: err => {
            alert('An error ocurred. See console for details.')
            console.error('An error occurred', err)
          }
        });
      }
    }
  }

  areEmptyFields(): boolean {
    if (this.selectedBeacon.id == ""
    ) {
      return true;
    }
    return false;
  }

  expandRow(row: any) {
    this.associatedItemDetails = null;
    if (this.expandedElement == row) {
      this.expandedElement = null;
    } else {
      this.expandedElement = row;
      if (row.item && row.item.type == "PLANT") {
        this.plantService.findPlantById(row.item.id).subscribe({
          next: result => {
            this.associatedItemDetails = result;
          },
          error: err => {
            console.error('An error occurred', err)
          }
        });
      } else if (row.item && row.item.type == "POI") {
        this.poiService.findPOIById(row.item.id).subscribe({
          next: result => {
            this.associatedItemDetails = result;
          },
          error: err => {
            console.error('An error occurred', err)
          }
        });
      }
    }
  }

  loadItemIds() {
    let itemSelected = this.selectedBeacon.item;
    if (itemSelected && itemSelected.type == "PLANT") {
      this.plantService.getAllPlants().subscribe({
        next: result => {
          this.selectableItems = result;
        },
        error: err => {
          console.error('An error occurred', err)
        }
      });
    } else if (itemSelected && itemSelected.type == "POI") {
      this.poiService.getAllPOIs().subscribe({
        next: result => {
          this.selectableItems = result;
        },
        error: err => {
          console.error('An error occurred', err)
        }
      });
    }
  }

  clearSelection() {
    this.selection = new SelectionModel<Beacon>(true, []);
  }

  /*....TABLE SELECTION.... */
  isAllSelected() {
    const numSelected = this.selection.selected.length;
    const numRows = this.dataSourceBeacons.data.length;
    return numSelected === numRows;
  }

  toggleAllRows() {
    if (this.isAllSelected()) {
      this.selection.clear();
      return;
    }

    this.selection.select(...this.dataSourceBeacons.data);
  }

}
