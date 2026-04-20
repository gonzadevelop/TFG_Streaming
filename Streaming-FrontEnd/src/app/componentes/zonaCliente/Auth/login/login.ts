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
import {Router,RouterLink} from '@angular/router';
import {Subscription} from 'rxjs';
import {TokenService} from '../../../../services/tokenService';
import {AuthService} from '../../../../services/authService';
import {IAuthResponse} from '../../../../model/IAuth';
import {IUserLogin} from '../../../../model/IUserLogin';

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
  private router: Router = inject(Router);
  private tokenService: TokenService = inject(TokenService);

  public email:InputSignal<string> = input<string>('');
  public atras:OutputEmitterRef<void> = output<void>();

  protected mostrarPassword:WritableSignal<boolean> = signal<boolean>(false);
  protected errorMessage: WritableSignal<string> = signal<string>('');
  protected isLoading: WritableSignal<boolean> = signal<boolean>(false);

  protected loginForm: FormGroup = new FormGroup({
    user: new FormControl('', [Validators.email]),
    password: new FormControl('', [Validators.minLength(8)]),
  });

  login(): void {
    if (this.loginForm.invalid) return;

    this.isLoading.set(true);
    this.errorMessage.set('');

    const request: IUserLogin = {
      email: this.loginForm.value.user,
      password: this.loginForm.value.password
    };
    this.suscripcionLogin = this.auth.login(request).subscribe({
      next: (response: IAuthResponse): void => {
        const token = response.token || '';
        this.tokenService.setToken(token);
        this.isLoading.set(false);
        this.router.navigate(['/home']);
      },
      error: (): void => {
        this.isLoading.set(false);
        this.errorMessage.set('Usuario o contraseña incorrectos. Inténtalo de nuevo.');
        setTimeout(() => this.errorMessage.set(''), 5000);
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
