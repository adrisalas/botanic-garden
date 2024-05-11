import { Component } from '@angular/core';
import { Router } from '@angular/router';
import { GeneralStoreService } from '../../services/general-store.service';

@Component({
  selector: 'app-stats-page',
  standalone: true,
  imports: [],
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
