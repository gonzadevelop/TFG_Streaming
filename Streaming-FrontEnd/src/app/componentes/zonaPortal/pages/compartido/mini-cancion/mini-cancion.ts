import { ChangeDetectionStrategy, Component, computed, input } from '@angular/core';
import { IPistaPlaylist } from '../../../../../model/pista/IPistaPlaylist';

@Component({
  selector: 'app-mini-cancion',
  imports: [],
  templateUrl: './mini-cancion.html',
  styleUrl: './mini-cancion.css',
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class MiniCancion {
  readonly pista = input.required<IPistaPlaylist>();
  readonly posicion = input<number | null>(null);

  protected readonly artistasTexto = computed(() => {
    const artistas = this.pista().artistas ?? [];
    return artistas.map(a => a.username).join(', ');
  });

  protected formatearDuracion(segundos: number): string {
    const m = Math.floor(segundos / 60);
    const s = segundos % 60;
    return `${m}:${s.toString().padStart(2, '0')}`;
  }
}

