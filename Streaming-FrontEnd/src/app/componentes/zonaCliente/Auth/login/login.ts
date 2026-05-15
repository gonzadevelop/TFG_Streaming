import {
  ChangeDetectionStrategy,
  Component,
  inject,
  OnDestroy,
  OnInit,
  signal,
  WritableSignal
} from '@angular/core';
import {FormControl, FormGroup, ReactiveFormsModule, Validators} from '@angular/forms';
import {Router, RouterLink} from '@angular/router';
import {Subscription} from 'rxjs';
import {TokenService} from '../../../../services/tokenService';
import {AuthService} from '../../../../services/authService';
import {IAuthResponse} from '../../../../model/auth/IAuth';
import {IUserLogin} from '../../../../model/auth/IUserLogin';

@Component({
  selector: 'app-login',
  imports: [ReactiveFormsModule, RouterLink],
  templateUrl: './login.html',
  styleUrl: './login.css',
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class Login implements OnInit, OnDestroy {
  private readonly auth = inject(AuthService);
  private readonly router = inject(Router);
  private readonly tokenService = inject(TokenService);
  private suscripcionLogin?: Subscription;

  protected mostrarPassword: WritableSignal<boolean> = signal<boolean>(false);
  protected errorMessage: WritableSignal<string> = signal<string>('');
  protected isLoading: WritableSignal<boolean> = signal<boolean>(false);

  protected loginForm: FormGroup = new FormGroup({
    user: new FormControl('', [Validators.required, Validators.email]),
    password: new FormControl('', [Validators.required, Validators.minLength(2)]),
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
        this.tokenService.setToken(response.token ?? '');
        this.tokenService.setUsername(response.user?.username ?? '');
        const role = response.user?.idRole?.nombre ?? this.tokenService.getPrimaryRole(response.token);
        this.tokenService.setRole(role ?? null);
        this.isLoading.set(false);
        this.router.navigate([role === 'ROLE_ARTISTA' ? '/artista/home' : '/']);
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
    this.router.navigate(['/']);
  }

  ngOnInit(): void {}

  ngOnDestroy(): void {
    this.suscripcionLogin?.unsubscribe();
  }
}
