import {
  ChangeDetectionStrategy,
  Component,
  OnInit,
  inject,
  signal,
  WritableSignal,
  computed,
  Signal,
} from '@angular/core';
import { IPista } from '../../../../../model/pista/IPista';
import { ListaCanciones } from '../compartido/lista-canciones/lista-canciones';
import { FavoritosService } from '../../../../../services/favoritosService';
import { KsLoaderComponent } from '../compartido/ks-loader/ks-loader';

@Component({
  selector: 'app-lista-favoritos',
  imports: [ListaCanciones, KsLoaderComponent],
  templateUrl: './lista-favoritos.html',
  styleUrl: './lista-favoritos.css',
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class ListaFavoritos implements OnInit {
  private readonly favoritosService = inject(FavoritosService);

  /** Lista de pistas reactiva — se actualiza automáticamente al pulsar el corazón */
  readonly pistas: Signal<IPista[]> = this.favoritosService.favoritosPistas;

  readonly totalCanciones = computed(() => this.pistas().length);

  readonly duracionTotalSegundos = computed(() =>
    this.pistas().reduce((acc, p) => acc + (p.duracionSegundos ?? 0), 0)
  );

  protected formatearDuracion(segundos: number): string {
    const horas = Math.floor(segundos / 3600);
    const minutos = Math.floor((segundos % 3600) / 60);
    if (horas > 0) return `${horas} h ${minutos} min`;
    return `${minutos} min`;
  }

  protected readonly cargando: WritableSignal<boolean> = signal(true);
  protected readonly error: WritableSignal<string | null> = signal<string | null>(null);

  ngOnInit(): void {
    this.cargando.set(true);
    this.error.set(null);

    /** Refrescamos siempre (cubre navegación directa y recarga de página). */

    this.favoritosService.cargarFavoritos({
      force: true,
      onComplete: () => this.cargando.set(false),
      onError: (msg) => {
        this.error.set(msg);
        this.cargando.set(false);
      },
    });
  }
}
