import { ChangeDetectionStrategy, Component, computed, inject, input, output } from '@angular/core';
import { MiniCancion } from '../mini-cancion/mini-cancion';
import { IPista } from '../../../../../../model/pista/IPista';
import { StorageGlobal } from '../../../../../../services/storageGlobal';
import IPistaCola from '../../../../../../model/pista/IPistaCola';

@Component({
  selector: 'app-lista-canciones',
  imports: [MiniCancion],
  templateUrl: './lista-canciones.html',
  styleUrl: './lista-canciones.css',
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class ListaCanciones {
  readonly pistas = input.required<IPista[]>();
  readonly portadaFallback = input<string>('');
  readonly playlistId       = input<number | null>(null);
  readonly esPlaylistPropia = input<boolean>(false);
  readonly mostrarPortada   = input<boolean>(true);

  readonly cancionEliminada = output<number>();

  private readonly storage = inject(StorageGlobal);

  /** Pistas con idPista normalizado para que todos los contextos tengan el ID correcto */
  protected readonly pistasNormalizadas = computed(() =>
    this.pistas().map(p => {
      const raw = p as unknown as Record<string, unknown>;
      const idPista =
        (p.idPista && p.idPista !== 0) ? p.idPista :
        (p.id      && p.id      !== 0) ? p.id      :
        ((raw['idCancion']  as number) || 0) ||
        ((raw['cancionId']  as number) || 0) ||
        ((raw['pistaId']    as number) || 0);
      return { ...p, idPista };
    })
  );

  onReproducirPista(pista: IPista): void {
    const pistasList = this.pistasNormalizadas();
    const fallback = this.portadaFallback();
    const clickedIndex = pistasList.findIndex(
      p => p === pista || (p.urlCancion && p.urlCancion === pista.urlCancion)
    );
    const resolvedIndex = clickedIndex !== -1 ? clickedIndex : 0;

    const cola: IPistaCola[] = pistasList.map((p, index) => ({
      ...p,
      urlPortada: p.urlPortada || fallback,
      orden: index,
      reproduciendo: index === resolvedIndex,
    }));
    this.storage.SetCola(cola);
  }
}
