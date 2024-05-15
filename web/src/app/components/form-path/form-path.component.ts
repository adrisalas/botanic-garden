import { Component, EventEmitter, Output } from '@angular/core';
import { Map_Path, Map_Point, initializeMapPath } from '../../model/map_interfaces';
import { MapPointService } from '../../services/map-point.service';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatIconModule } from '@angular/material/icon';
import { MatButtonModule } from '@angular/material/button';
import { FormsModule, ReactiveFormsModule } from '@angular/forms';
import { MatSelectModule } from '@angular/material/select';
import { MapPathService } from '../../services/map-path.service';

@Component({
  selector: 'app-form-path',
  standalone: true,
  imports: [MatFormFieldModule, MatIconModule, MatButtonModule, FormsModule, MatSelectModule, ReactiveFormsModule],
  templateUrl: './form-path.component.html',
  styleUrls: ['./form-path.component.css', '../../styles/mapFormStyles.css']
})
export class FormPathComponent {

  @Output() openFormPath = new EventEmitter<boolean>();
  @Output() refreshMap = new EventEmitter<void>();

  path: Map_Path;
  allPoints: Map_Point[] = [];
  allPaths: Map_Path[] = [];

  pointsA: Map_Point[] = [];
  pointsB: Map_Point[] = [];

  pathDelete: Map_Path | undefined;



  constructor(private mapPointService: MapPointService,
    private mapPathService: MapPathService) {
    this.path = initializeMapPath();
    this.loadAllPoints();
    this.loadAllPaths();
  }

  loadAllPoints() {
    this.mapPointService.findAllPoints().subscribe({
      next: result => {
        this.allPoints = result.sort((p1, p2) => {
          let id1 = (p1.id == null) ? 0 : p1.id
          let id2 = (p2.id == null) ? 0 : p2.id
          return id1 - id2
        });
        this.pointsA = result;
        this.pointsB = result
      },
      error: err => console.error('An error occurred loading all points', err.reason)
    });
  }

  loadAllPaths() {
    this.mapPathService.findAllPaths().subscribe({
      next: result => {
        this.allPaths = result.sort((path1, path2) => {
          let id1 = (path1.pointA.id == null) ? 0 : path1.pointA.id
          let id2 = (path2.pointA.id == null) ? 0 : path2.pointA.id
          return id1 - id2
        });
      },
      error: err => console.error('An error occurred loading all paths', err.reason)
    });
  }

  closeMenu() {
    this.openFormPath.emit(false);
  }

  /*.....................................................................*/
  onChangePointA() {
    this.pointsB = this.allPoints.filter(point => point.id != this.path.pointA.id);
  }

  onChangePointB() {
    this.pointsA = this.allPoints.filter(point => point.id != this.path.pointB.id);
  }

  /*.....................................................................*/
  createPath() {
    if (this.path.pointA.id == 0 || this.path.pointB.id == 0) {
      alert("First point and second point are required.");
    } else {
      this.mapPathService.createMapPath(this.path).subscribe({
        next: result => {
          this.allPaths.push(this.path);
          this.path = initializeMapPath();
          this.refreshMap.emit();
          this.pointsA = this.allPoints;
          this.pointsB = this.allPoints;
        },
        error: err => alert("An error occurred creating path: " + err.reason)
      });
    }
  }

  deletePath() {
    if (this.pathDelete != undefined) {
      if (confirm("Are you sure you want to delete the selected path?")) {
        this.mapPathService.deleteMapPath(this.pathDelete.pointA.id as number, this.pathDelete.pointB.id as number).subscribe({
          next: result => {
            this.refreshMap.emit();
            const indexToDelete = this.allPaths.findIndex(path => path.pointA.id == this.pathDelete?.pointA.id && path.pointB.id == this.pathDelete?.pointB.id);
            if (indexToDelete !== -1) {
              this.allPaths.splice(indexToDelete, 1);
            }
            this.pathDelete = undefined;
          },
          error: err => alert("An error occurred deleting path: " + err.reason)
        });
      }
    } else {
      alert("Select one path to delete.");
    }

  }

}
