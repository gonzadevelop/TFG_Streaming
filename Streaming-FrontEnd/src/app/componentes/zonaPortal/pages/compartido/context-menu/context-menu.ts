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
import { IPista } from '../../../../../model/pista/IPista';
import { IPlaylist } from '../../../../../model/home/IPlaylist';
import { StorageGlobal } from '../../../../../services/storageGlobal';
import { PlaylistService } from '../../../../../services/playlistService';
import { ContextMenuService } from '../../../../../services/contextMenuService';
import IPistaCola from '../../../../../model/pista/IPistaCola';

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

  readonly cerrar = output<void>();

  private readonly storage         = inject(StorageGlobal);
  private readonly playlistService = inject(PlaylistService);
  private readonly ctxMenuService  = inject(ContextMenuService);
  private readonly elRef           = inject(ElementRef<HTMLElement>);

  protected readonly misPlaylists   = signal<IPlaylist[]>([]);
  protected readonly subMenuAbierto = signal<boolean>(false);
  protected readonly mensaje        = signal<string | null>(null);
  private mensajeTimeout: ReturnType<typeof setTimeout> | null = null;

  protected readonly estiloMenu = computed(() => ({
    top:  `${this.position().y}px`,
    left: `${this.position().x}px`,
  }));

  constructor() {
    // Carga las playlists cuando el menú se abre
    effect(() => {
      if (this.visible()) {
        this.playlistService.getMisPlaylists().subscribe({
          next: (playlists) => this.misPlaylists.set(playlists),
          error: () => this.misPlaylists.set([]),
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
    this.playlistService.setAgregarCancionPlaylist({
      playlistId: playlist.id,
      pistaIds: [this.pista().idPista],
    }).subscribe({
      next: () => this.mostrarMensaje(`✔ Añadida a "${playlist.nombre}"`),
      error: () => this.mostrarMensaje('✖ Error al añadir a la playlist'),
    });
    this.subMenuAbierto.set(false);
    this.cerrar.emit();
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


