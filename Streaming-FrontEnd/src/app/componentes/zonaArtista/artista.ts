import { ChangeDetectionStrategy, Component, signal } from '@angular/core';
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

  readonly cancionesTop: CancionTop[] = [
    { titulo: 'Luz de Medianoche', escuchas: '128K', duracion: '3:42' },
    { titulo: 'Sincronias', escuchas: '84K', duracion: '2:58' },
    { titulo: 'Ciudad Neon', escuchas: '61K', duracion: '4:15' },
  ];

  submitNewSong(): void {
    this.uploadMessage.set('Funcionalidad de subir tema preparada para conectar con el formulario real.');
  }
}
