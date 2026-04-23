import { ChangeDetectionStrategy, Component, computed, inject, input, untracked } from '@angular/core';
import { IPistaPlaylist } from '../../../../../model/pista/IPistaPlaylist';
import { StorageGlobal } from '../../../../../services/storageGlobal';

@Component({
  selector: 'app-mini-cancion',
  imports: [],
  templateUrl: './mini-cancion.html',
  styleUrl: './mini-cancion.css',
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class MiniCancion {
  readonly pista    = input.required<IPistaPlaylist>();
  readonly posicion = input<number | null>(null);

  private readonly storage = inject(StorageGlobal);

  /** true si esta pista es la que está sonando actualmente */
  protected readonly isReproduciendoEsta = computed(() => {
    const actual = untracked(() => this.storage.GetReproduccion()());
    return !!actual.urlCancion
      && actual.urlCancion === this.pista().urlCancion
      && this.storage.reproduciendo();
  });

  protected readonly artistasTexto = computed(() =>
    (this.pista().artistas ?? []).map(a => a.username).join(', ')
  );

  protected reproducir(): void {
    const p = this.pista();
    const actual = this.storage.GetReproduccion()();

    // Si ya es la pista activa (misma URL), solo alternamos play/pause
    if (actual.urlCancion && actual.urlCancion === p.urlCancion) {
      this.storage.TogglePlay();
      return;
    }

    this.storage.Reproducir({
      idPista:          p.idPista ?? 0,
      titulo:           p.titulo,
      artistas:         p.artistas,
      urlPortada:       p.urlPortada,
      urlCancion:       p.urlCancion,
      duracionSegundos: p.duracionSegundos,
      reproduciendo:    true,
    });
  }

  protected formatearDuracion(segundos: number): string {
    const m = Math.floor(segundos / 60);
    const s = segundos % 60;
    return `${m}:${s.toString().padStart(2, '0')}`;
  }
}

