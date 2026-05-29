import {ChangeDetectionStrategy, Component, computed, inject, OnInit} from '@angular/core';
import { ActivatedRoute, Router } from '@angular/router';
import { ListaCanciones } from '../compartido/lista-canciones/lista-canciones';
import { AlbumCard } from '../compartido/album-card/album-card';
import { IArtista } from '../../../../../model/artista/IArtista';
import { ArtistaService } from '../../../../../services/artistaService';
import { UserService } from '../../../../../services/userService';
import { signal, WritableSignal } from '@angular/core';
import { KsLoaderComponent } from '../compartido/ks-loader/ks-loader';

@Component({
  selector: 'app-artista',
  standalone: true,
  imports: [ListaCanciones, AlbumCard, KsLoaderComponent],
  templateUrl: './artista.html',
  styleUrl: './artista.css',
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class Artista implements OnInit {
  private readonly artistaService: ArtistaService = inject(ArtistaService);
  private readonly userService: UserService = inject(UserService);
  private readonly router = inject(Router);
  private readonly activatedRoute = inject(ActivatedRoute);

  /**
   * Artista obtenido del backend
   */
  readonly artista: WritableSignal<IArtista | null> = signal<IArtista | null>(null);

  protected readonly cargando: WritableSignal<boolean> = signal(true);
  protected readonly error: WritableSignal<string | null> = signal<string | null>(null);
  protected readonly mostrarTodasLasCanciones: WritableSignal<boolean> = signal(false);
  protected readonly siguiendoLoading: WritableSignal<boolean> = signal(false);
  protected readonly siguiendoError: WritableSignal<string> = signal('');

  /** IDs de álbumes cuya fecha de lanzamiento ya ha llegado (o no tienen fecha). */
  private readonly albumsPublicadosIds = computed<Set<number>>(() => {
    const a = this.artista();
    if (!a) return new Set();
    const now = Date.now();
    return new Set(
      (a.albums ?? [])
        .filter(album => {
          if (!album.fechaLanzamiento) return true;
          return new Date(album.fechaLanzamiento).getTime() <= now;
        })
        .map(album => album.id)
    );
  });

  /** Canciones filtradas: solo las que pertenecen a un álbum ya publicado. */
  protected readonly cancionesPublicadas = computed(() => {
    const a = this.artista();
    if (!a?.canciones?.length) return [];
    const publicados = this.albumsPublicadosIds();
    return a.canciones.filter(p =>
      // Si no tiene albumId no podemos filtrar → se muestra
      p.albumId == null || publicados.has(p.albumId)
    );
  });

  /** Álbumes filtrados: solo los ya publicados. */
  protected readonly albumsPublicados = computed(() => {
    const a = this.artista();
    if (!a?.albums?.length) return [];
    const publicados = this.albumsPublicadosIds();
    return a.albums.filter(album => publicados.has(album.id));
  });

  ngOnInit(): void {
    // Usar paramMap para reaccionar a cambios en los parámetros de ruta
    this.activatedRoute.paramMap.subscribe(params => {
      const username = params.get('username');

      if (!username) {
        this.error.set('Usuario no especificado.');
        this.cargando.set(false);
        return;
      }

      this.cargando.set(true);
      this.error.set(null);

      this.artistaService.getArtista(username).subscribe({
        next: (data: IArtista) => {
          this.artista.set(data);
          this.cargando.set(false);
          console.log('Artista cargado:', this.artista());
        },
        error: (err: unknown) => {
          console.error('Error cargando artista:', err);
          this.error.set('No se pudo cargar el artista.');
          this.cargando.set(false);
        },
      });
    });
  }

  protected alternarCanciones(): void {
    this.mostrarTodasLasCanciones.update(valor => !valor);
  }

  protected toggleSeguir(): void {
    const a = this.artista();
    if (!a || this.siguiendoLoading()) return;

    this.siguiendoLoading.set(true);
    this.siguiendoError.set('');

    const accion$ = a.sigueAlArtista
      ? this.userService.dejarDeSeguirArtista(a.id)
      : this.userService.seguirArtista(a.id);

    accion$.subscribe({
      next: () => {
        this.artista.set({
          ...a,
          sigueAlArtista: !a.sigueAlArtista,
          seguidores: a.sigueAlArtista ? a.seguidores - 1 : a.seguidores + 1,
        });
        this.siguiendoLoading.set(false);
      },
      error: () => {
        this.siguiendoError.set('No se pudo realizar la acción. Inténtalo de nuevo.');
        this.siguiendoLoading.set(false);
      },
    });
  }
}
