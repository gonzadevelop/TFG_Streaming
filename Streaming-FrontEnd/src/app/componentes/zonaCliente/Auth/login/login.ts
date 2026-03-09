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
import {ServApiSpring} from '../../../../services/ServApiSpring';
import {HttpErrorResponse, HttpResponse} from '@angular/common/http';
import ILoginResponse from '../../../../model/ILoginResponse';
import ILoginRequest from '../../../../model/ILoginRequest';
import {Subscription} from 'rxjs';

@Component({
  selector: 'app-login',
  imports: [
    ReactiveFormsModule
  ],
  templateUrl: './login.html',
  styleUrl: './login.css',
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class Login implements OnInit, OnDestroy {
  private servApiSpring:ServApiSpring = inject( ServApiSpring );
  private suscripcionLogin?: Subscription;
  private errorTimeout?: ReturnType<typeof setTimeout>;


  public email:InputSignal<string> = input<string>('');
  public atras:OutputEmitterRef<void> = output<void>();

  protected mostrarPassword:WritableSignal<boolean> = signal<boolean>(false);
  protected errorCredenciales:WritableSignal<string> = signal<string>('');
  protected cargando:WritableSignal<boolean> = signal<boolean>(false);

  protected loginForm:FormGroup = new FormGroup(
    {
      user: new FormControl('', [Validators.required, Validators.minLength(3)]),
      password: new FormControl('', [Validators.required, Validators.minLength(6)])
    }
  );


  login(): void {
    this.loginForm.markAllAsTouched();

    if (this.loginForm.invalid) {
      return;
    }

    this.errorCredenciales.set('');
    this.cargando.set(true);

    const request: ILoginRequest = {
      email: this.loginForm.value.user,
      password: this.loginForm.value.password
    };

    this.suscripcionLogin = this.servApiSpring.login(request).subscribe({
      next: (response: HttpResponse<ILoginResponse>): void => {
        this.cargando.set(false);
        if (response.status === 200) {
          const token = response.body?.token || '';
          console.log('Inicio de sesión exitoso. Token:', token);
        }
      },
      error: (err: HttpErrorResponse): void => {
        this.cargando.set(false);
        if (err.status === 401 || err.status === 403) {
          this.mostrarError('El usuario o la contraseña no son correctos.');
        } else if (err.status === 0) {
          this.mostrarError('No se pudo conectar con el servidor. Inténtalo más tarde.');
        } else {
          this.mostrarError('Ha ocurrido un error inesperado. Inténtalo de nuevo.');
        }
      }
    });
  }

  private mostrarError(mensaje: string): void {
    clearTimeout(this.errorTimeout);
    this.errorCredenciales.set(mensaje);
    this.errorTimeout = setTimeout(() => {
      this.errorCredenciales.set('');
    }, 5000);
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
    clearTimeout(this.errorTimeout);
  }
}
