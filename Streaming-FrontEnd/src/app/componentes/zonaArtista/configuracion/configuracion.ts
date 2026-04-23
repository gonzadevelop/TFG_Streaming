import { ChangeDetectionStrategy, Component, signal } from '@angular/core';

@Component({
  selector: 'app-configuracion',
  imports: [],
  templateUrl: './configuracion.html',
  styleUrl: './configuracion.css',
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class Configuracion {
  readonly nombreArtistico = signal('Tu Perfil Artistico');
  readonly emailContacto = signal('artista@keysound.com');
  readonly modoPrivado = signal(false);
  readonly dobleVerificacion = signal(true);
  readonly avisosLanzamiento = signal(true);
  readonly mensajesEstado = signal('');

  saveChanges(): void {
    window.localStorage.setItem('artistName', this.nombreArtistico());
    window.localStorage.setItem('artistEmail', this.emailContacto());
    window.localStorage.setItem('artistPrivateMode', String(this.modoPrivado()));
    window.localStorage.setItem('artistTwoFactor', String(this.dobleVerificacion()));
    window.localStorage.setItem('artistReleaseAlerts', String(this.avisosLanzamiento()));
    this.mensajesEstado.set('Cambios guardados correctamente.');
  }

  resetSettings(): void {
    this.nombreArtistico.set('Tu Perfil Artistico');
    this.emailContacto.set('artista@keysound.com');
    this.modoPrivado.set(false);
    this.dobleVerificacion.set(true);
    this.avisosLanzamiento.set(true);
    this.mensajesEstado.set('Ajustes restaurados a valores por defecto.');
  }

  toggleModoPrivado(): void {
    this.modoPrivado.update((value) => !value);
    this.mensajesEstado.set('Modo privado actualizado.');
  }

  toggleDobleVerificacion(): void {
    this.dobleVerificacion.update((value) => !value);
    this.mensajesEstado.set('Doble verificación actualizada.');
  }

  toggleAvisosLanzamiento(): void {
    this.avisosLanzamiento.update((value) => !value);
    this.mensajesEstado.set('Avisos de lanzamiento actualizados.');
  }
}

