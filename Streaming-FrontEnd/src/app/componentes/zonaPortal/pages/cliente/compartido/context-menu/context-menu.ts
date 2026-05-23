import {
  ChangeDetectionStrategy,
  Component,
  computed,
  effect,
  ElementRef,
  inject,
  input,
  OnDestroy,
  output,
  signal,
} from '@angular/core';
import { IPista } from '../../../../../../model/pista/IPista';
import { IPlaylist } from '../../../../../../model/home/IPlaylist';
import { StorageGlobal } from '../../../../../../services/storageGlobal';
import { PlaylistService } from '../../../../../../services/playlistService';
import { ContextMenuService } from '../../../../../../services/contextMenuService';
import IPistaCola from '../../../../../../model/pista/IPistaCola';

export interface ContextMenuPosition {
  x: number;
  y: number;
}

@Component({
  selector: 'app-context-menu',
  imports: [],
  templateUrl: './context-menu.html',
  styleUrl: './context-menu.css',
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class ContextMenu implements OnDestroy {
  readonly pista    = input.required<IPista>();
  readonly position = input.required<ContextMenuPosition>();
  readonly visible  = input<boolean>(false);
  /** ID de la playlist actual (solo cuando el menú se abre desde dentro de una playlist propia) */
  readonly playlistId      = input<number | null>(null);
  readonly esPlaylistPropia = input<boolean>(false);

  readonly cerrar = output<void>();
  /** Emite el idPista eliminado para que el componente padre actualice la lista */
  readonly cancionEliminada = output<number>();

  private readonly storage         = inject(StorageGlobal);
  private readonly playlistService = inject(PlaylistService);
  private readonly ctxMenuService  = inject(ContextMenuService);
  private readonly elRef           = inject(ElementRef<HTMLElement>);

  protected readonly misPlaylists      = signal<IPlaylist[]>([]);
  protected readonly subMenuAbierto   = signal<boolean>(false);
  protected readonly mensaje           = signal<string | null>(null);
  protected readonly cargandoPlaylists = signal<boolean>(false);
  protected readonly guardando         = signal<boolean>(false);
  private mensajeTimeout: ReturnType<typeof setTimeout> | null = null;

  protected readonly estiloMenu = computed(() => {
    const pos = this.position();
    const viewportWidth = window.innerWidth;
    const viewportHeight = window.innerHeight;

    // Dimensiones aproximadas del menú
    const menuWidth = 220;
    const menuHeight = 200;

    let x = pos.x;
    let y = pos.y;

    // Ajustar horizontalmente si se sale por la derecha
    if (x + menuWidth > viewportWidth) {
      x = viewportWidth - menuWidth - 16;
    }

    // Ajustar verticalmente si se sale por abajo
    if (y + menuHeight > viewportHeight) {
      y = viewportHeight - menuHeight - 16;
    }

    // Asegurarse de que no sea negativo
    x = Math.max(10, x);
    y = Math.max(10, y);

    return {
      top: `${y}px`,
      left: `${x}px`,
    };
  });

  constructor() {
    // Carga las playlists cuando el menú se abre
    effect(() => {
      if (this.visible()) {
        this.cargandoPlaylists.set(true);
        this.playlistService.getMisPlaylists().subscribe({
          next: (playlists) => {
            this.misPlaylists.set(playlists);
            this.cargandoPlaylists.set(false);
          },
          error: () => {
            this.misPlaylists.set([]);
            this.cargandoPlaylists.set(false);
          },
        });
      }
    });

    // Cierra el menú al hacer clic fuera
    this._onDocumentClick = (e: MouseEvent) => {
      if (!this.elRef.nativeElement.contains(e.target as Node)) {
        this.ctxMenuService.cerrar();
        this.cerrar.emit();
      }
    };
    document.addEventListener('click', this._onDocumentClick, true);
  }

  private readonly _onDocumentClick: (e: MouseEvent) => void;

  ngOnDestroy(): void {
    document.removeEventListener('click', this._onDocumentClick, true);
    if (this.mensajeTimeout) clearTimeout(this.mensajeTimeout);
  }

  protected eliminarDePlaylist(): void {
    const p = this.pista();
    const idPista = p.idPista && p.idPista !== 0 ? p.idPista : (p.id ?? 0);
    const idPlaylist = this.playlistId();

    if (idPista === 0 || !idPlaylist) {
      this.mostrarMensaje('✖ No se pudo identificar la canción o la playlist');
      return;
    }

    this.guardando.set(true);
    this.playlistService.eliminarCancionDePlaylist(idPlaylist, idPista).subscribe({
      next: () => {
        this.guardando.set(false);
        this.cancionEliminada.emit(idPista);
        this.mostrarMensaje('✔ Eliminada de la playlist');
        setTimeout(() => {
          this.ctxMenuService.cerrar();
          this.cerrar.emit();
        }, 1200);
      },
      error: () => {
        this.guardando.set(false);
        this.mostrarMensaje('✖ Error al eliminar de la playlist');
      },
    });
  }

  protected agregarACola(): void {
    const p = this.pista();
    const pistaCola: IPistaCola = {
      idPista:          p.idPista,
      titulo:           p.titulo,
      artistas:         p.artistas,
      urlPortada:       p.urlPortada ?? '',
      urlCancion:       p.urlCancion,
      duracionSegundos: p.duracionSegundos,
      reproduciendo:    false,
      orden:            0,
    };
    this.storage.AgregarSiguienteEnCola(pistaCola);
    this.mostrarMensaje('✔ Añadida a la cola');
    this.cerrar.emit();
  }

  protected agregarAPlaylist(playlist: IPlaylist): void {
    const p = this.pista();
    // idPista puede venir como 0 en algunos contextos; usamos `id` como fallback
    const idPista = p.idPista && p.idPista !== 0 ? p.idPista : (p.id ?? 0);

    if (idPista === 0) {
      this.mostrarMensaje('✖ No se pudo identificar la canción');
      return;
    }

    this.guardando.set(true);
    this.subMenuAbierto.set(false);

    this.playlistService.setAgregarCancionPlaylist({
      playlistId: playlist.id,
      pistaIds: [idPista],
    }).subscribe({
      next: () => {
        this.guardando.set(false);
        this.mostrarMensaje(`✔ Añadida a "${playlist.nombre}"`);
        // Cerramos el menú tras breve feedback
        setTimeout(() => {
          this.ctxMenuService.cerrar();
          this.cerrar.emit();
        }, 1200);
      },
      error: () => {
        this.guardando.set(false);
        this.mostrarMensaje('✖ Error al añadir a la playlist');
      },
    });
  }

  protected abrirSubMenu(): void {
    this.subMenuAbierto.set(true);
  }

  protected cerrarSubMenu(): void {
    // Delay para permitir hover en el submenu
    setTimeout(() => {
      this.subMenuAbierto.set(false);
    }, 200);
  }

  protected toggleSubMenu(event: Event): void {
    event.stopPropagation();
    this.subMenuAbierto.update(v => !v);
  }

  private mostrarMensaje(msg: string): void {
    this.mensaje.set(msg);
    if (this.mensajeTimeout) clearTimeout(this.mensajeTimeout);
    this.mensajeTimeout = setTimeout(() => this.mensaje.set(null), 3000);
  }
}


