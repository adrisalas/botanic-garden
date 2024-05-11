import { Component } from '@angular/core';
import { MatInputModule } from '@angular/material/input';
import { User } from '../../model/plant_interface';
import { MatFormFieldModule } from '@angular/material/form-field';
import { FormsModule } from '@angular/forms';
import { MatIconModule } from '@angular/material/icon';
import { MatButtonModule } from '@angular/material/button';
import { AuthService } from '../../services/auth.service';
import { Router } from '@angular/router';
import { GeneralStoreService } from '../../services/general-store.service';

@Component({
  selector: 'app-login-page',
  standalone: true,
  imports: [MatInputModule, MatFormFieldModule, FormsModule, MatIconModule, MatButtonModule],
  templateUrl: './login-page.component.html',
  styleUrl: './login-page.component.css'
})
export class LoginPageComponent {
  user: User;
  hidePassword: boolean = true;

  constructor(
    private authService: AuthService,
    private router: Router,
    private generalData: GeneralStoreService
  ) {
    this.user = {
      fullName: "",
      username: "",
      password: ""
    }
  }


  login() {
    if (this.user.username != "" && this.user.password != "") {
      this.authService.login(this.user).subscribe({
        next: result => {
          let resultUser = this.authService.decodeTokenToUser(result.token);
          if (resultUser && resultUser.isAdmin) {
            this.router.navigate(['/plants']);
            this.generalData.changeActiveToken(result.token);
            this.generalData.setUserName(resultUser.username);
          } else {
            alert("Insufficient permissions or incorrect login.");
          }
        },
        error: err => console.error('An error occurred', err)
      })
    } else {
      alert("Both fields are required.");
    }
  }
}
