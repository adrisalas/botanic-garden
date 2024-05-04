import { Routes } from '@angular/router';
import { PlantsPageComponent } from './pages/plants-page/plants-page.component';
import { NewsPageComponent } from './pages/news-page/news-page.component';
import { MapsPageComponent } from './pages/maps-page/maps-page.component';
import { LoginPageComponent } from './pages/login-page/login-page.component';
import { PoiPageComponent } from './pages/poi-page/poi-page.component';
import { BeaconsPageComponent } from './pages/beacons-page/beacons-page.component';

export const routes: Routes = [
    {
        path: '',
        pathMatch: 'full',
        redirectTo: 'login'
    },
    {
        path: 'plants',
        component: PlantsPageComponent
    },
    {
        path: 'news',
        component: NewsPageComponent
    },
    {
        path: 'map',
        component: MapsPageComponent
    },
    {
        path: 'login',
        component: LoginPageComponent
    },
    {
        path: 'poi',
        component: PoiPageComponent
    },
    {
        path: 'beacons',
        component: BeaconsPageComponent
    }
];
