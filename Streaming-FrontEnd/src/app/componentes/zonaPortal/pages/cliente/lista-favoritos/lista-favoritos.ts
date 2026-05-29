import {
  ChangeDetectionStrategy,
  Component,
  OnInit,
  inject,
  signal,
  Signal,
  WritableSignal,
  computed,
} from '@angular/core';
import { IPista } from '../../../../../model/pista/IPista';
import { ListaCanciones } from '../compartido/lista-canciones/lista-canciones';
import { FavoritosService } from '../../../../../services/favoritosService';
import { KsLoaderComponent } from '../compartido/ks-loader/ks-loader';
import { StorageGlobal } from '../../../../../services/storageGlobal';
import IPistaCola from '../../../../../model/pista/IPistaCola';

@Component({
  selector: 'app-lista-favoritos',
  imports: [ListaCanciones, KsLoaderComponent],
  templateUrl: './lista-favoritos.html',
  styleUrl: './lista-favoritos.css',
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class ListaFavoritos implements OnInit {
  private readonly favoritosService = inject(FavoritosService);
  private readonly storage          = inject(StorageGlobal);

  /** Lista de pistas en orden descendente (más reciente primero) */
  readonly pistas: Signal<IPista[]> = computed(() =>
    [...this.favoritosService.favoritosPistas()].reverse()
  );

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

  protected reproducirTodo(): void {
    const pistas = this.pistas();
    if (!pistas.length) return;

    const cola: IPistaCola[] = pistas.map((p, index) => ({
      ...p,
      orden: index,
      reproduciendo: index === 0,
    }));
    this.storage.SetCola(cola);
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
