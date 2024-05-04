import { Component, Input, computed, effect, signal } from '@angular/core';
import { MatListModule } from '@angular/material/list';
import { MatIconModule } from '@angular/material/icon';
import { MenuItem } from '../../model/types';
import { CommonModule } from '@angular/common';
import { RouterModule } from '@angular/router';
import { GeneralStoreService } from '../../services/general-store.service';

@Component({
  selector: 'app-custom-sidenav',
  standalone: true,
  imports: [MatListModule, MatIconModule, CommonModule, RouterModule],
  templateUrl: './custom-sidenav.component.html',
  styleUrl: './custom-sidenav.component.css'
})

export class CustomSidenavComponent {

  sideNavCollapsed = signal(false);
  @Input() set collapsed(val: boolean) {
    this.sideNavCollapsed.set(val);
  }

  profileImgWidth = computed(() => this.sideNavCollapsed() ? '42' : '100');
  userName = "";

  menuItems = signal<MenuItem[]>([
    {
      icon: "local_florist",
      label: "Plants",
      route: "plants"
    },
    {
      icon: "event",
      label: "News",
      route: "news"
    },
    {
      icon: "room",
      label: "Points of interest",
      route: "poi"
    },

    {
      icon: "wifi_tethering",
      label: "Beacons",
      route: "beacons"
    },
    {
      icon: "map",
      label: "Map",
      route: "map"
    }
  ]);

  constructor(private generalData: GeneralStoreService) {
    effect(() => {
      this.userName = this.generalData.username();
    });
  }
}
