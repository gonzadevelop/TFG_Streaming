import {Component, inject, OnDestroy, output, OutputEmitterRef} from '@angular/core';
import {FormControl, FormGroup, ReactiveFormsModule, Validators} from '@angular/forms';
import {Subscription} from 'rxjs';
import {AuthService} from '../../../../services/authService';
import {HttpResponse} from '@angular/common/http';
import {ICheckEmailResponse} from '../../../../model/IAuth';

@Component({
  selector: 'app-comprobacion-email',
  imports: [
    ReactiveFormsModule
  ],
  templateUrl: './comprobacion-email.html',
  styleUrl: './comprobacion-email.css',
})
export class ComprobacionEmail implements OnDestroy {
  public emailChecked:OutputEmitterRef<boolean> = output<boolean>();
  public email:OutputEmitterRef<string> = output<string>();

  private auth: AuthService = inject( AuthService );
  private suscripcionComprobarEmail?: Subscription;

  registroForm: FormGroup = new FormGroup(
    {
      email: new FormControl('', [Validators.required, Validators.email ])
    }
  );

  comprobarEmail():void {
    console.log(this.registroForm.value.email);
    this.suscripcionComprobarEmail = this.auth
      .checkEmail( this.registroForm.value.email )
      .subscribe({
        next: ( response:ICheckEmailResponse ): void => {
          console.log('Respuesta completa:', response);

          if (response.registered) {
            this.onEmailChecked(true);
            this.onEmailSubmit(this.registroForm.value.email);
            console.log('El email está registrado.');
          } else {
              this.onEmailChecked(false);
              this.onEmailSubmit(this.registroForm.value.email);
              console.log('El email no está registrado.');
          }
        },
        error: (error:Error):void => {
          console.error('Error al comprobar el email:', error);
        }
      });
  }

  onEmailChecked( isRegistered: boolean ):void {
    this.emailChecked.emit( isRegistered );
  }

  onEmailSubmit( email: string ):void {
    this.email.emit( email );
  }

  ngOnDestroy(): void {
    if ( this.suscripcionComprobarEmail ) {
      this.suscripcionComprobarEmail.unsubscribe();
    }
  }
}
