import { ChangeDetectionStrategy, Component, signal, WritableSignal } from '@angular/core';
import { RouterLink } from '@angular/router';

interface CancionTop {
  titulo: string;
  escuchas: string;
  duracion: string;
}

@Component({
  selector: 'app-artista',
  imports: [RouterLink],
  templateUrl: './artista.html',
  styleUrl: './artista.css',
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class Artista {
  readonly nombreArtistico = 'Tu Perfil Artistico';
  readonly uploadMessage = signal<string>('');
  readonly mostrarFormulario = signal<boolean>(false);

  readonly cancionesTop: CancionTop[] = [
    { titulo: 'Luz de Medianoche', escuchas: '128K', duracion: '3:42' },
    { titulo: 'Sincronias', escuchas: '84K', duracion: '2:58' },
    { titulo: 'Ciudad Neon', escuchas: '61K', duracion: '4:15' },
  ];

  submitNewSong(): void {
    this.mostrarFormulario.set(true);
    //this.uploadMessage.set('Aquí se abriría el formulario de subida para artista.');
  }

  cerrarFormulario(): void {
    this.mostrarFormulario.set(false);
  }
}
