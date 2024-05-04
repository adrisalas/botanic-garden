import { Injectable } from '@angular/core';
import { User } from '../model/plant_interface';
import { HttpClient, HttpHeaders } from '@angular/common/http';
import { environment } from '../../environments/environment';

@Injectable({
  providedIn: 'root'
})
export class AuthService {

  constructor(private _httpClient: HttpClient) { }

  login(user: User) {
    delete user.fullName;
    var headers = new HttpHeaders({
      "Content-Type": "application/json",
      "Accept": "application/json"
    });

    return this._httpClient.post<any>(environment.urlLogin, JSON.stringify(user), {
      headers: headers
    });
  }

  decodeTokenToUser(token: string) {
    const data64 = token.split(".")[1];
    return JSON.parse(atob(data64));
  }
}
