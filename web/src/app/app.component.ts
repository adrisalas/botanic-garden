import { Component, computed, effect, signal } from '@angular/core';
import { RouterOutlet } from '@angular/router';
import { MatToolbarModule } from '@angular/material/toolbar'
import { MatButtonModule } from '@angular/material/button'
import { MatIconModule } from '@angular/material/icon'
import { MatSidenavModule } from '@angular/material/sidenav'
import { CustomSidenavComponent } from "./components/custom-sidenav/custom-sidenav.component";
import { FormsModule } from '@angular/forms';
import { GeneralStoreService } from './services/general-store.service';

@Component({
  selector: 'app-root',
  standalone: true,
  templateUrl: './app.component.html',
  styleUrl: './app.component.css',
  imports: [RouterOutlet, MatToolbarModule, MatButtonModule, MatIconModule, MatSidenavModule, CustomSidenavComponent, FormsModule]
})
export class AppComponent {

  title = 'bg-admin-app';
  collapsed = signal(false);
  sidenavWidth = computed(() => this.collapsed() ? '65px' : '250px');
  tokenJWT = "";

  constructor(
    private generalData: GeneralStoreService
  ) {

    effect(() => {
      this.tokenJWT = this.generalData.tokenJWT();
    });
  }

  logout() {
    this.generalData.tokenJWT.set('');
  }

}
