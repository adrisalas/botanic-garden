import { Component, ViewChild } from '@angular/core';
import { MatTableDataSource, MatTableModule } from '@angular/material/table';
import { News, initializeNews } from '../../model/news_interface';
import { SelectionModel } from '@angular/cdk/collections';
import { MatSort, MatSortModule } from '@angular/material/sort';
import { NewsService } from '../../services/news.service';
import { GeneralStoreService } from '../../services/general-store.service';
import { Router } from '@angular/router';
import { CommonModule } from '@angular/common';
import { MatInputModule } from '@angular/material/input';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatIconModule } from '@angular/material/icon';
import { MatButtonModule } from '@angular/material/button';
import { MatSidenavModule } from '@angular/material/sidenav';
import { MatSelectModule } from '@angular/material/select';
import { MatCheckboxModule } from '@angular/material/checkbox';
import { FormsModule } from '@angular/forms';
import { animate, state, style, transition, trigger } from '@angular/animations';
import { MatDatepickerModule } from '@angular/material/datepicker';
import { provideNativeDateAdapter } from '@angular/material/core';

@Component({
  selector: 'app-news-page',
  standalone: true,
  animations: [
    trigger('detailExpand', [
      state('collapsed,void', style({ height: '0px', minHeight: '0' })),
      state('expanded', style({ height: '*' })),
      transition('expanded <=> collapsed', animate('225ms cubic-bezier(0.4, 0.0, 0.2, 1)')),
    ])
  ],
  providers: [provideNativeDateAdapter()],
  imports: [MatTableModule,
    MatSortModule, CommonModule, MatInputModule, MatFormFieldModule, MatIconModule,
    MatButtonModule, MatSidenavModule, MatSelectModule, MatCheckboxModule, FormsModule, MatDatepickerModule],
  templateUrl: './news-page.component.html',
  styleUrl: './news-page.component.css'
})
export class NewsPageComponent {

  displayedColumns: string[] = ['select', 'id', 'title', 'subtitle', 'date', 'image', 'description'];
  resultsLength: number = 0;
  dataSourceNews: MatTableDataSource<News> = new MatTableDataSource();
  selection = new SelectionModel<News>(true, []);
  isOpenCRUDNav: boolean = false;
  titleModal: string = 'Create News';
  modeModal: string = '';

  selectedNews: News = initializeNews();
  expandedElement: News | null = null;

  @ViewChild(MatSort) sort: MatSort;

  constructor(
    private newsService: NewsService,
    private generalData: GeneralStoreService,
    private router: Router) {
    if (this.generalData.tokenJWT() == "") {
      this.router.navigate(['']);
    } else {
      this.getDataNews();
    }

    this.sort = new MatSort();
  }

  ngAfterViewInit(): void {

    this.modeModal = '';
  }

  getDataNews() {
    this.newsService.getAllNews().subscribe({
      next: news => {
        if (this.dataSourceNews.data && this.dataSourceNews.data.length == 0) {
          this.dataSourceNews = new MatTableDataSource(news);
          this.dataSourceNews.sort = this.sort;
        } else {
          this.dataSourceNews.data = news;
        }
        this.resultsLength = news.length;
      },
      error: err => console.error('An error occurred', err)
    })
  }

  applyFilter(event: Event) {
    const filterValue = (event.target as HTMLInputElement).value;
    this.dataSourceNews.filter = filterValue.trim().toLowerCase();

    if (this.dataSourceNews.paginator) {
      this.dataSourceNews.paginator.firstPage();
    }
  }

  /*.... CREATE .... */
  showCreate() {
    this.modeModal = 'create';
    this.titleModal = 'Create News';
    this.selectedNews = initializeNews();
    this.isOpenCRUDNav = true;
  }

  /*....EDIT.... */
  showEdit() {
    this.modeModal = 'edit';
    this.titleModal = 'Edit News';
    if (this.selection.selected.length != 1) {
      alert("Select only one record to edit");
      this.isOpenCRUDNav = false;
    } else {
      this.selectedNews = this.selection.selected[0];
      this.isOpenCRUDNav = true;
    }
  }

  saveNews() {
    if (this.areEmptyFields()) {
      alert("Fields marked as (*) are required.")
      return;
    }

    if (this.modeModal == 'create') {
      this.newsService.createNews(this.selectedNews).subscribe({
        next: result => {
          alert("Successfully created news.");
          this.isOpenCRUDNav = false;
          this.getDataNews();
        },
        error: err => console.error('An error occurred', err)
      });

    } else if (this.modeModal = 'edit') {
      this.newsService.updateNews(this.selectedNews).subscribe({
        next: result => {
          alert("Successfully updated news.");
          this.isOpenCRUDNav = false;
        },
        error: err => {
          alert('An error ocurred. See console for details.')
          console.error('An error occurred', err)
        }
      });;

    }
  }

  deleteNews() {
    if (this.selection.selected.length != 1) {
      alert("Select only one record to delete");
    } else {
      const selectedNews: News = this.selection.selected[0];
      let confirmationText = `Are you sure you want to delete ${selectedNews.title}?`;
      if (confirm(confirmationText)) {
        this.newsService.deleteNews(this.selection.selected[0].id as number).subscribe({
          next: result => {
            alert("Successfully deleted news.");
            this.getDataNews();
            this.selection = new SelectionModel<News>(true, []);
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
    if (this.selectedNews.title == "" ||
      this.selectedNews.subtitle == "" ||
      this.selectedNews.description == "" ||
      this.selectedNews.date == ""
    ) {
      return true;
    }

    return false;
  }

  /*....TABLE SELECTION.... */
  isAllSelected() {
    const numSelected = this.selection.selected.length;
    const numRows = this.dataSourceNews.data.length;
    return numSelected === numRows;
  }

  toggleAllRows() {
    if (this.isAllSelected()) {
      this.selection.clear();
      return;
    }

    this.selection.select(...this.dataSourceNews.data);
  }

}
