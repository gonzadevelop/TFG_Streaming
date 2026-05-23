import { ChangeDetectionStrategy, Component, computed, inject, input, untracked, output, signal } from '@angular/core';
import { Router } from '@angular/router';
import { IPista } from '../../../../../../model/pista/IPista';
import { StorageGlobal } from '../../../../../../services/storageGlobal';
import { FavoritosService } from '../../../../../../services/favoritosService';
import { ContextMenuService } from '../../../../../../services/contextMenuService';
import { ContextMenu, ContextMenuPosition } from '../context-menu/context-menu';
import { ScrollRevealDirective } from '../../../../../../shared/directives/scroll-reveal.directive';

@Component({
  selector: 'app-mini-cancion',
  imports: [ContextMenu, ScrollRevealDirective],
  templateUrl: './mini-cancion.html',
  styleUrl: './mini-cancion.css',
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class MiniCancion {
  readonly pista    = input.required<IPista>();
  readonly posicion = input<number | null>(null);
  /** ID de la playlist actual para permitir la opción de eliminar */
  readonly playlistId       = input<number | null>(null);
  readonly esPlaylistPropia = input<boolean>(false);
  readonly mostrarPortada   = input<boolean>(true);

  readonly reproducirEvento  = output<IPista>();
  readonly cancionEliminada  = output<number>();

  private readonly storage           = inject(StorageGlobal);
  private readonly router            = inject(Router);
  private readonly favoritosService  = inject(FavoritosService);
  private readonly ctxMenuService    = inject(ContextMenuService);

  /** ID único para esta instancia del menú */
  private readonly menuId = crypto.randomUUID();

  protected readonly ctxPosition = signal<ContextMenuPosition>({ x: 0, y: 0 });

  /** Solo visible si este componente es el menú activo global */
  protected readonly ctxVisible = computed(() =>
    this.ctxMenuService.activeMenuId() === this.menuId
  );

  /** true si esta pista es la que está sonando actualmente */
  protected readonly isReproduciendoEsta = computed(() => {
    const actual = untracked(() => this.storage.GetReproduccion()());
    return !!actual.urlCancion
      && actual.urlCancion === this.pista().urlCancion
      && this.storage.reproduciendo();
  });

  /** true si esta pista está en la lista de favoritos */
  protected readonly esFavorito = computed(() =>
    this.favoritosService.favoritosIds().has(this.pista().idPista)
  );

  protected toggleFavorito(event: Event): void {
    event.stopPropagation();
    this.favoritosService.toggleFavorito(this.pista());
  }

  protected reproducir(): void {
    const p = this.pista();
    const actual = this.storage.GetReproduccion()();

    // Si ya es la pista activa (misma URL), solo alternamos play/pause
    if (actual.urlCancion && actual.urlCancion === p.urlCancion) {
      this.storage.TogglePlay();
      return;
    }

    this.reproducirEvento.emit(p);
  }

  protected abrirContextMenu(event: MouseEvent): void {
    event.preventDefault();
    event.stopPropagation();
    this.ctxPosition.set({ x: event.clientX, y: event.clientY });
    this.ctxMenuService.abrir(this.menuId);
  }

  protected cerrarContextMenu(): void {
    this.ctxMenuService.cerrar();
  }

  protected formatearDuracion(segundos: number): string {
    const m = Math.floor(segundos / 60);
    const s = segundos % 60;
    return `${m}:${s.toString().padStart(2, '0')}`;
  }

  protected navegarArtista(nombreArtista: string): void {
    this.router.navigate(['/artistas', nombreArtista]);
  }
}
