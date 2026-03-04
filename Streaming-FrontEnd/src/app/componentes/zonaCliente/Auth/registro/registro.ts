import {
  Component, inject,
  input,
  InputSignal,
  OnDestroy,
  OnInit,
  output,
  OutputEmitterRef,
  signal,
  WritableSignal
} from '@angular/core';
import {FormControl, FormGroup, ReactiveFormsModule, Validators} from '@angular/forms';
import {ServApiSpring} from '../../../../services/ServApiSpring';
import ILoginRequest from '../../../../model/ILoginRequest';

@Component({
  selector: 'app-registro',
  imports: [ReactiveFormsModule],
  templateUrl: './registro.html',
  styleUrl: './registro.css',
})
export class Registro implements OnInit, OnDestroy {
  private servApiSpring:ServApiSpring = inject( ServApiSpring );

  public email:InputSignal<string> = input<string>('');
  public atras:OutputEmitterRef<void> = output<void>();

  protected mostrarPassword:WritableSignal<boolean> = signal<boolean>(false);

  protected registroForm:FormGroup = new FormGroup({
    username: new FormControl('', [Validators.required, Validators.minLength(3)]),
    password: new FormControl('', [Validators.required, Validators.minLength(6), Validators.pattern('^(?=.*?[A-Z])(?=.*?[a-z])(?=.*?[0-9])(?=.*?[#?!@$%^&*-]).{6,}$')]),
    nombre: new FormControl('', [Validators.required]),
    apellidos: new FormControl('', [Validators.required]),
    genero: new FormControl('', [Validators.required]),
    dni: new FormControl('', [Validators.pattern(/^[0-9]{8}[A-Za-z]$/)]),
    telefono: new FormControl('', [Validators.pattern(/^[0-9]{9}$/)])
  });



  registrar(): void {
    if (this.registroForm.valid) {
      const registroData:ILoginRequest = {
        email: this.email(),
        password: this.registroForm.value
      };
      console.log('Registrando usuario con:', registroData);
    }
  }

  togglePasswordVisibility(): void {
    this.mostrarPassword.set(!this.mostrarPassword());
  }

  goBack(): void {
    this.atras.emit();
  }

  ngOnInit(): void {
    console.log('componente de registro cargado...');
    console.log('valor del email recibido como input:', this.email());
  }

  ngOnDestroy():void {
  }
}
