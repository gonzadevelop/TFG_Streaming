import {ChangeDetectionStrategy,
  Component,
  inject,
  input,
  InputSignal,
  OnDestroy,
  OnInit,
  output,
  OutputEmitterRef,
  signal, WritableSignal
} from '@angular/core';
import {FormControl, FormGroup, ReactiveFormsModule, Validators} from '@angular/forms';
import {RouterLink} from '@angular/router';
import {Subscription} from 'rxjs';
import {AuthService} from '../../../../services/authService';
import {IAuthResponse} from '../../../../model/IAuth';
import {IUserLogin} from '../../../../model/IUser';

@Component({
  selector: 'app-login',
  imports: [
    ReactiveFormsModule,
    RouterLink,
  ],
  templateUrl: './login.html',
  styleUrl: './login.css',
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class Login implements OnInit, OnDestroy {
  private auth: AuthService = inject( AuthService );
  private suscripcionLogin?: Subscription;


  public email:InputSignal<string> = input<string>('');
  public atras:OutputEmitterRef<void> = output<void>();

  protected mostrarPassword:WritableSignal<boolean> = signal<boolean>(false);

  protected loginForm: FormGroup = new FormGroup({
    user: new FormControl('', [Validators.email]),
    password: new FormControl('', [Validators.minLength(8)]),
  });

  login(): void {
    console.log('Iniciando sesión con:', this.email(), this.loginForm.value.password);
    const request: IUserLogin = {
      email: this.email(),
      password: this.loginForm.value.password
    };
    this.suscripcionLogin = this.auth.login(request).subscribe({
      next: (response: IAuthResponse): void => {
        const token = response.token || '';
        console.log('Inicio de sesión exitoso. Token:', token);
      }
    });
  }

  togglePasswordVisibility(): void {
    this.mostrarPassword.update(v => !v);
  }

  goBack(): void {
    this.atras.emit();
  }

  ngOnInit(): void {
    console.log('componente de login cargado...');
    console.log('valor del email recibido como input:', this.email());
  }

  ngOnDestroy():void {
    this.suscripcionLogin?.unsubscribe();
  }
}
