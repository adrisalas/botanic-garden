import { Component } from '@angular/core';
import { NgxChartsModule } from '@swimlane/ngx-charts';
import { StatsService } from '../../services/stats.service';
import { StatVisitorsHour } from '../../model/stats_interface';

@Component({
  selector: 'app-linear-chart',
  standalone: true,
  imports: [NgxChartsModule],
  templateUrl: './linear-chart.component.html',
  styleUrl: './linear-chart.component.css'
})
export class LinearChartComponent {
  multi: any[] = [];
  view: [number, number] = [1100, 300];

  // options
  legend: boolean = false;
  showLabels: boolean = true;
  animations: boolean = true;
  xAxis: boolean = true;
  yAxis: boolean = true;
  showYAxisLabel: boolean = true;
  showXAxisLabel: boolean = true;
  xAxisLabel: string = 'Hour';
  yAxisLabel: string = 'Visitors';
  timeline: boolean = true;


  constructor(private statsService: StatsService) {
    this.statsService.getVisitorsPerHour().subscribe({
      next: result => { Object.assign(this, this.transformResultIntoChartData(result)) },
      error: err => console.log("An error occurred loading visitors per hour.", err)
    })
  }

  onSelect(data: any): void {
    console.log('Item clicked', JSON.parse(JSON.stringify(data)));
  }

  onActivate(data: any): void {
    console.log('Activate', JSON.parse(JSON.stringify(data)));
  }

  onDeactivate(data: any): void {
    console.log('Deactivate', JSON.parse(JSON.stringify(data)));
  }

  transformResultIntoChartData(resultApi: StatVisitorsHour[]): any[] {
    let series: any[] = []
    let chartData: any = {
      "name": "Number of Visitors"
    };
    let result: any[] = [];

    resultApi.forEach((visitorsPerHour: StatVisitorsHour) => {
      let objectSeries = { "name": visitorsPerHour.hour + ":00", "value": visitorsPerHour.count }
      series.push(objectSeries);
    });

    chartData.series = series;
    result.push(chartData);
    this.multi = result;
    return result;
  }

}
