import { ChangeDetectionStrategy, Component, input } from '@angular/core';
import { MiniCancion } from '../mini-cancion/mini-cancion';

@Component({
  selector: 'app-lista-canciones',
  imports: [MiniCancion],
  templateUrl: './lista-canciones.html',
  styleUrl: './lista-canciones.css',
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class ListaCanciones {
  readonly pistas = input.required<any[]>();
}
