import { Component } from '@angular/core';
import { NgxChartsModule } from '@swimlane/ngx-charts';
import { colorSets } from './color-sets';
import { StatsService } from '../../services/stats.service';
import { StatVisitorsDay } from '../../model/stats_interface';

const monthName = new Intl.DateTimeFormat("en-us", { month: "short" });
const weekdayName = new Intl.DateTimeFormat("en-us", { weekday: "short" });

@Component({
  selector: 'app-heatmap-calendar',
  standalone: true,
  imports: [NgxChartsModule],
  templateUrl: './heatmap-calendar.component.html',
  styleUrl: './heatmap-calendar.component.css'
})
export class HeatmapCalendarComponent {
  //Options
  innerPadding = '10%';
  maxXAxisTickLength = 16;
  maxYAxisTickLength = 16;
  colorSets: any;
  colorScheme: any;
  selectedColorScheme: string = '';

  // heatmap
  heatmapMin: number = 0;
  heatmapMax: number = 12;
  calendarData: any[] = [];

  visitorsPerDay: StatVisitorsDay[] = []

  constructor(private statsService: StatsService) {
    Object.assign(this, {
      colorSets
    });
    this.setColorScheme('vivid');

    this.statsService.getVisitorsPerDay().subscribe({
      next: result => { this.visitorsPerDay = result; this.setHeatmapMax(); this.calendarData = this.getCalendarData(); },
      error: err => console.error("An error occurred loading visitors per day.")
    });
  }

  setColorScheme(name: string) {
    this.selectedColorScheme = name;
    this.colorScheme = this.colorSets.find((s: any) => s.name === name);
  }

  calendarAxisTickFormatting(mondayString: string) {
    const monday = new Date(mondayString);
    const month = monday.getMonth();
    const day = monday.getDate();
    const year = monday.getFullYear();
    const lastSunday = new Date(year, month, day - 1);
    const nextSunday = new Date(year, month, day + 6);
    return lastSunday.getMonth() !== nextSunday.getMonth() ? monthName.format(nextSunday) : '';
  }

  calendarTooltipText(c: any): string {
    return `
      <span class="tooltip-label">${c.label} • ${c.cell.date.toLocaleDateString()}</span>
      <span class="tooltip-val">${c.data.toLocaleString()}</span>
    `;
  }

  getCalendarData(): any[] {
    // today
    const now = new Date();
    const todaysDay = now.getDate();
    const thisDay = new Date(now.getFullYear(), now.getMonth(), todaysDay);

    // Monday
    const thisMonday = new Date(thisDay.getFullYear(), thisDay.getMonth(), todaysDay - thisDay.getDay() + 1);
    const thisMondayDay = thisMonday.getDate();
    const thisMondayYear = thisMonday.getFullYear();
    const thisMondayMonth = thisMonday.getMonth();

    // 52 weeks before monday
    const calendarData = [];
    const getDate = (d: any) => new Date(thisMondayYear, thisMondayMonth, d);
    for (let week = -52; week <= 0; week++) {
      const mondayDay = thisMondayDay + week * 7;
      const monday = getDate(mondayDay);

      // one week
      const series = [];
      for (let dayOfWeek = 7; dayOfWeek > 0; dayOfWeek--) {
        const date = getDate(mondayDay - 1 + dayOfWeek);

        // skip future dates
        if (date > now) {
          continue;
        }

        // value
        let value = 0;
        let correctDay = date.getDate() < 10 ? "0" + date.getDate() : date.getDate();
        let correctMonth = (date.getMonth() + 1) < 10 ? "0" + (date.getMonth() + 1) : (date.getMonth() + 1);
        correctMonth = correctMonth == "00" ? "12" : correctMonth;

        let fechaString = date.getFullYear() + "-" + correctMonth + "-" + correctDay;
        let countVisitors = this.findCountInDay(fechaString);
        if (countVisitors) {
          value = countVisitors.count;
        }

        series.push({
          date,
          name: weekdayName.format(date),
          value
        });
      }

      calendarData.push({
        name: monday.toString(),
        series
      });
    }

    return calendarData;
  }

  setHeatmapMax() {
    let maxCounts: number = 0;
    this.visitorsPerDay.forEach((visitorsDay: StatVisitorsDay) => {
      if (visitorsDay.count > maxCounts) {
        maxCounts = visitorsDay.count;
      }
    });

    this.heatmapMax = maxCounts;
  }

  getDayMonthYear(visitorsDay: StatVisitorsDay): {
    day: number,
    month: number,
    year: number
  } {
    let date = visitorsDay.day.split("-");
    return {
      "day": parseInt(date[2]),
      "month": parseInt(date[1]),
      "year": parseInt(date[0])
    }
  }

  findCountInDay(date: string) {
    return this.visitorsPerDay.find((visitor: StatVisitorsDay) => {
      if (visitor.day == date) {
        return true
      }
      return false;
    });
  }

  findByDate(date: string) {
    return function (visitor: StatVisitorsDay) {
      console.log(date + "------" + visitor.day);
      return date == visitor.day;
    };
  }
}
