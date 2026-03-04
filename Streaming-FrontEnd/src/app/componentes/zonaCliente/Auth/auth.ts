import {Component, OnInit, signal, Signal, WritableSignal} from '@angular/core';
import {ComprobacionEmail} from './comprobacion-email/comprobacion-email';
import { Login } from './login/login';
import { Registro } from './registro/registro';

@Component({
  selector: 'app-auth',
  imports: [
    ComprobacionEmail,
    Login,
    Registro
  ],
  templateUrl: './auth.html',
  styleUrl: './auth.css',
})
export class Auth implements OnInit {
  protected vistaAtual! :WritableSignal<'check-email' | 'login' | 'registro'>;
  public emailSignal:WritableSignal<string> = signal<string>('');



  handlerOnEmailChecked( isRegistered: boolean ):void {
    if (isRegistered){
      this.vistaAtual.set('login');
    } else {
      this.vistaAtual.set('registro');
    }
  }

  handlerOnAtras():void {
    this.vistaAtual.set('check-email');
  }

  handlerOnEmail( email: string ):void {
    this.emailSignal.set(email);
  }

  ngOnInit(): void {
    this.vistaAtual = signal<'check-email' | 'login' | 'registro'>('check-email');  }
}
