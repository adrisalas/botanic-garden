import { Component } from '@angular/core';
import { Router } from '@angular/router';
import { GeneralStoreService } from '../../services/general-store.service';

@Component({
  selector: 'app-gamification-page',
  standalone: true,
  imports: [],
  templateUrl: './gamification-page.component.html',
  styleUrl: './gamification-page.component.css'
})
export class GamificationPageComponent {

  constructor(
    private router: Router,
    private generalData: GeneralStoreService) {
    if (this.generalData.tokenJWT() == "") {
      this.router.navigate(['']);
    }

  }

}
