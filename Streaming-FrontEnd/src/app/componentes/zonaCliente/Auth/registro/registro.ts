import {
  Component, inject,
  input,
  InputSignal,
  OnDestroy,
  OnInit,
  signal,
  WritableSignal
} from '@angular/core';
import {FormControl, FormGroup, ReactiveFormsModule, Validators} from '@angular/forms';
import {Router, RouterLink} from '@angular/router';
import {AuthService} from '../../../../services/authService';
import {TokenService} from '../../../../services/tokenService';
import {Subscription} from 'rxjs';
import {IAuthResponse} from '../../../../model/auth/IAuth';
import {IUserRegister} from '../../../../model/auth/IUserRegister';

@Component({
  selector: 'app-registro',
  imports: [ReactiveFormsModule, RouterLink],
  templateUrl: './registro.html',
  styleUrl: './registro.css',
})
export class Registro implements OnInit, OnDestroy {
  private authService: AuthService = inject( AuthService );
  private tokenService: TokenService = inject(TokenService);
  private router: Router = inject(Router);
  private suscripcionRegistro?: Subscription;

  public email: InputSignal<string> = input<string>('');

  protected mostrarPassword: WritableSignal<boolean> = signal<boolean>(false);
  protected errorMessage: WritableSignal<string> = signal<string>('');
  protected isLoading: WritableSignal<boolean> = signal<boolean>(false);

  protected registroForm: FormGroup = new FormGroup({
    username: new FormControl('', [Validators.required, Validators.minLength(3)]),
    email: new FormControl('', [Validators.required, Validators.email]),
    password: new FormControl('', [Validators.required, Validators.minLength(8), Validators.pattern('^(?=.*?[A-Z])(?=.*?[a-z])(?=.*?[0-9])(?=.*?[#?!@$%^&*-]).{8,}$')]),
    rol: new FormControl('', [Validators.required])
  });

  registrar(): void {
    if (this.registroForm.invalid) return;

    this.isLoading.set(true);
    this.errorMessage.set('');

    const userData: IUserRegister = {
      ...this.registroForm.value
    };
      this.suscripcionRegistro = this.authService.register(userData).subscribe({
        next: (response: IAuthResponse): void => {
          const token = response.token ?? '';
          this.tokenService.setToken(token);
          console.log('Registro exitoso. Token guardado:', token);
          this.router.navigate(['/verificar-email'], {
              queryParams: { email: this.email() }
          });
        },
        error: (err): void => {
          console.error('Error en registro:', err);
          this.errorMessage.set('Error al crear la cuenta. Inténtalo de nuevo.');
          this.isLoading.set(false);
        }
      });
    }

  togglePasswordVisibility(): void {
    this.mostrarPassword.set(!this.mostrarPassword());
  }

  ngOnInit(): void {
    console.log('componente de registro cargado...');
    console.log('valor del email recibido como input:', this.email());
  }

  ngOnDestroy():void {
    this.suscripcionRegistro?.unsubscribe();
  }
}
