import { Component, OnInit } from '@angular/core';
import { Router } from '@angular/router';
import { GeneralStoreService } from '../../services/general-store.service';
import { HeatmapCalendarComponent } from '../../components/heatmap-calendar/heatmap-calendar.component';
import { LinearChartComponent } from '../../components/linear-chart/linear-chart.component';
import { BarChartPlantsComponent } from '../../components/bar-chart-plants/bar-chart-plants.component';
import { BarChartPoisComponent } from '../../components/bar-chart-pois/bar-chart-pois.component';

@Component({
  selector: 'app-stats-page',
  standalone: true,
  imports: [HeatmapCalendarComponent, LinearChartComponent, BarChartPlantsComponent, BarChartPoisComponent],
  templateUrl: './stats-page.component.html',
  styleUrl: './stats-page.component.css'
})
export class StatsPageComponent {

  constructor(
    private router: Router,
    private generalData: GeneralStoreService) {
    if (this.generalData.tokenJWT() == "") {
      this.router.navigate(['']);
    }

  }

}
