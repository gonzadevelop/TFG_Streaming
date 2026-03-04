import {Component, inject, OnDestroy, output, OutputEmitterRef} from '@angular/core';
import {FormControl, FormGroup, ReactiveFormsModule, Validators} from '@angular/forms';
import {ServApiSpring} from '../../../../services/ServApiSpring';
import {Subscription} from 'rxjs';
import {HttpResponse} from '@angular/common/http';

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

  private servApiSpring:ServApiSpring = inject( ServApiSpring );
  private suscripcionComprobarEmail?: Subscription;

  registroForm: FormGroup = new FormGroup(
    {
      email: new FormControl('', [Validators.required, Validators.email ])
    }
  );

  comprobarEmail():void {
    console.log(this.registroForm.value.email);
    this.suscripcionComprobarEmail = this.servApiSpring
      .checkResitro( this.registroForm.value.email )
      .subscribe({
        next: ( response:HttpResponse<boolean> ):void => {
          console.log('Respuesta completa:', response);

          if (response.status === 200 && response.body !== null) {
            if (response.body) {
              this.onEmailChecked(true);
              this.onEmailSubmit(this.registroForm.value.email);
              console.log('El email está registrado.');
            } else {
              this.onEmailChecked(false);
              this.onEmailSubmit(this.registroForm.value.email);
              console.log('El email no está registrado.');
            }
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
