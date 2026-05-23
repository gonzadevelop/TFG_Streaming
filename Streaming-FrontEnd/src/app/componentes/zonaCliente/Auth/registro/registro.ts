import {
  ChangeDetectionStrategy,
  Component,
  inject,
  OnDestroy,
  signal,
  WritableSignal
} from '@angular/core';
import {FormControl, FormGroup, ReactiveFormsModule, Validators} from '@angular/forms';
import {Router, RouterLink} from '@angular/router';
import {AuthService} from '../../../../services/authService';
import {Subscription} from 'rxjs';
import {IUserRegister} from '../../../../model/auth/IUserRegister';

@Component({
  selector: 'app-registro',
  imports: [ReactiveFormsModule, RouterLink],
  templateUrl: './registro.html',
  styleUrl: './registro.css',
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class Registro implements OnDestroy {
  private authService: AuthService = inject(AuthService);
  private router: Router = inject(Router);
  private suscripcionRegistro?: Subscription;

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

    const userData: IUserRegister = { ...this.registroForm.value };

    this.suscripcionRegistro = this.authService.register(userData).subscribe({
      next: (): void => {
        this.router.navigate(['/login']);
      },
      error: (): void => {
        this.errorMessage.set('Error al crear la cuenta. Inténtalo de nuevo.');
        this.isLoading.set(false);
      }
    });
  }

  togglePasswordVisibility(): void {
    this.mostrarPassword.update(v => !v);
  }

  ngOnDestroy(): void {
    this.suscripcionRegistro?.unsubscribe();
  }
}
