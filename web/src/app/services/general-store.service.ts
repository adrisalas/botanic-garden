import { Injectable, WritableSignal, signal } from '@angular/core';
import { Subject } from 'rxjs';

@Injectable({
  providedIn: 'root'
})
export class GeneralStoreService {

  tokenJWT: WritableSignal<string> = signal('');
  username: WritableSignal<string> = signal('');

  constructor() { }

  changeActiveToken(token: string) {
    this.tokenJWT.set(token);
  }

  setUserName(newUsername: string) {
    this.username.set(newUsername);
  }

  getAuthorizationHeader(): string {
    return `Bearer ${this.tokenJWT()}`
  }
}
