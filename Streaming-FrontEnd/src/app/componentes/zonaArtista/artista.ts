import { ChangeDetectionStrategy, Component } from '@angular/core';

interface CancionTop {
  titulo: string;
  escuchas: string;
  duracion: string;
}

@Component({
  selector: 'app-artista',
  imports: [],
  templateUrl: './artista.html',
  styleUrl: './artista.css',
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class Artista {
  readonly nombreArtistico = 'Tu Perfil Artistico';

  readonly cancionesTop: CancionTop[] = [
    { titulo: 'Luz de Medianoche', escuchas: '128K', duracion: '3:42' },
    { titulo: 'Sincronias', escuchas: '84K', duracion: '2:58' },
    { titulo: 'Ciudad Neon', escuchas: '61K', duracion: '4:15' },
  ];
}


