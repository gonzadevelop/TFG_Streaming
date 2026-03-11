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
import {ServApiSpring} from '../../../../services/ServApiSpring';
import {HttpResponse} from '@angular/common/http';
import ILoginResponse from '../../../../model/ILoginResponse';
import ILoginRequest from '../../../../model/ILoginRequest';
import {Subscription} from 'rxjs';

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
  private servApiSpring:ServApiSpring = inject( ServApiSpring );
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
    const request: ILoginRequest = {
      email: this.email(),
      password: this.loginForm.value.password
    };
    this.suscripcionLogin = this.servApiSpring.login(request).subscribe(
      (response: HttpResponse<ILoginResponse>):void => {{
          if (response.status === 200) {
            const token = response.body?.token || '';
            console.log('Inicio de sesión exitoso. Token:', token);
          }
        }
      }
    )
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
