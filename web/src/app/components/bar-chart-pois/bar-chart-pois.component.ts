import { Component } from '@angular/core';
import { NgxChartsModule, Color, ScaleType } from '@swimlane/ngx-charts';
import { StatsService } from '../../services/stats.service';
import { MostVisitedPOIs } from '../../model/stats_interface';

@Component({
  selector: 'app-bar-chart-pois',
  standalone: true,
  imports: [NgxChartsModule],
  templateUrl: './bar-chart-pois.component.html',
  styleUrl: './bar-chart-pois.component.css'
})
export class BarChartPoisComponent {
  data: any[] = [];

  view: [number, number] = [550, 400];

  // options
  showXAxis = true;
  showYAxis = true;
  gradient = false;
  showLegend = true;
  showXAxisLabel = true;
  xAxisLabel = 'POIs';
  showYAxisLabel = true;
  yAxisLabel = 'Visitors';

  colorScheme: Color = {
    name: 'myScheme',
    selectable: true,
    group: ScaleType.Ordinal,
    domain: ['#63d5f7', '#ffa8fb', '#a8ffaf', '#ffa8b8', '#a79afc', '#f7b674', '#f27d6b'],
  };

  constructor(private statsService: StatsService) {
    this.statsService.getMostVisitedPOIs().subscribe({
      next: (result: MostVisitedPOIs[]) => { this.data = this.resultPOIsToCharData(result) },
      error: err => console.error("An error occurred loading plants stats ", err)
    });
  }

  ngOnInit(): void {
    Object.assign(this, this.data);
  }

  onSelect(event: any) {
    console.log(event);
  }

  resultPOIsToCharData(result: MostVisitedPOIs[]): any[] {
    let convertedData: any[] = [];
    result.forEach((poiVisited: MostVisitedPOIs) => {
      convertedData.push({
        "name": poiVisited.poi.name,
        "value": poiVisited.count
      });
    });
    return convertedData;
  }

}
