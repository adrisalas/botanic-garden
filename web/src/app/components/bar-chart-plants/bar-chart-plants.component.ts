import { Component } from '@angular/core';
import { NgxChartsModule, Color, ScaleType } from '@swimlane/ngx-charts';
import { StatsService } from '../../services/stats.service';
import { MostVisitedPlants } from '../../model/stats_interface';


@Component({
  selector: 'app-bar-chart-plants',
  standalone: true,
  imports: [NgxChartsModule],
  templateUrl: './bar-chart-plants.component.html',
  styleUrl: './bar-chart-plants.component.css'
})
export class BarChartPlantsComponent {

  data: any[] = [];

  view: [number, number] = [550, 400];

  // options
  showXAxis = true;
  showYAxis = true;
  gradient = false;
  showLegend = true;
  showXAxisLabel = true;
  xAxisLabel = 'Plants';
  showYAxisLabel = true;
  yAxisLabel = 'Visitors';

  colorScheme: Color = {
    name: 'myScheme',
    selectable: true,
    group: ScaleType.Ordinal,
    domain: ['#63d5f7', '#ffa8fb', '#a8ffaf', '#ffa8b8', '#a79afc', '#f7b674', '#f27d6b'],
  };

  constructor(private statsService: StatsService) {
    this.statsService.getMostVisitedPlants().subscribe({
      next: (result: MostVisitedPlants[]) => { this.data = this.resultPlantsToCharData(result) },
      error: err => console.error("An error occurred loading plants stats ", err)
    });
  }

  ngOnInit(): void {
    Object.assign(this, this.data);
  }

  onSelect(event: any) {
    console.log(event);
  }

  resultPlantsToCharData(result: MostVisitedPlants[]): any[] {
    let convertedData: any[] = [];
    result.forEach((plantVisited: MostVisitedPlants) => {
      convertedData.push({
        "name": plantVisited.plant.commonName,
        "value": plantVisited.count
      });
    });
    return convertedData;
  }
}
