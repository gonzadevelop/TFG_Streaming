import {
  ChangeDetectionStrategy,
  Component,
  computed,
  inject,
  signal,
} from '@angular/core';
import {RouterLink} from '@angular/router';
import {NgOptimizedImage} from '@angular/common';
import {debounceTime, distinctUntilChanged, Subject, switchMap, of, catchError, forkJoin} from 'rxjs';
import {takeUntilDestroyed} from '@angular/core/rxjs-interop';
import {CancionService} from '../../../../services/cancionService';
import {AlbumService} from '../../../../services/albumService';
import {ArtistaService} from '../../../../services/artistaService';
import {PlaylistService} from '../../../../services/playlistService';
import {IPista} from '../../../../model/pista/IPista';
import {IAlbum} from '../../../../model/album/IAlbum';
import {IArtistaHome} from '../../../../model/home/IArtistaHome';
import {IPlaylist} from '../../../../model/home/IPlaylist';
import {StorageGlobal} from '../../../../services/storageGlobal';
import IPistaReproduccion from '../../../../model/pista/IPistaReproduccion';
export interface IResultadosBusqueda {
  canciones: IPista[];
  albums: IAlbum[];
  artistas: IArtistaHome[];
  playlists: IPlaylist[];
}
@Component({
  selector: 'app-header',
  templateUrl: './header.html',
  styleUrl: './header.css',
  changeDetection: ChangeDetectionStrategy.OnPush,
  imports: [RouterLink, NgOptimizedImage],
})
export class HeaderComponent {
  private readonly cancionService = inject(CancionService);
  private readonly albumService = inject(AlbumService);
  private readonly artistaService = inject(ArtistaService);
  private readonly playlistService = inject(PlaylistService);
  protected readonly storage = inject(StorageGlobal);
  readonly searchTerm = signal('');
  readonly resultados = signal<IResultadosBusqueda>({ canciones: [], albums: [], artistas: [], playlists: [] });
  readonly buscando = signal(false);
  readonly mostrarResultados = signal(false);
  readonly hayResultados = computed(() => {
    const r = this.resultados();
    return r.canciones.length > 0 || r.albums.length > 0 || r.artistas.length > 0 || r.playlists.length > 0;
  });
  private readonly isSidebarOpenSignal = signal(false);
  readonly isSidebarOpen = computed(() => this.isSidebarOpenSignal());
  private readonly busqueda$ = new Subject<string>();
  constructor() {
    this.busqueda$.pipe(
      debounceTime(300),
      distinctUntilChanged(),
      switchMap(term => {
        if (term.trim().length < 2) {
          this.resultados.set({ canciones: [], albums: [], artistas: [], playlists: [] });
          this.buscando.set(false);
          this.mostrarResultados.set(false);
          return of(null);
        }
        this.buscando.set(true);
        return forkJoin({
          canciones: this.cancionService.buscarCanciones(term).pipe(catchError(() => of([] as IPista[]))),
          albums: this.albumService.buscarAlbums(term).pipe(catchError(() => of([] as IAlbum[]))),
          artistas: this.artistaService.buscarArtistas(term).pipe(catchError(() => of([] as IArtistaHome[]))),
          playlists: this.playlistService.buscarPlaylists(term).pipe(catchError(() => of([] as IPlaylist[]))),
        });
      }),
      takeUntilDestroyed()
    ).subscribe(res => {
      if (res) {
        this.resultados.set(res);
        this.mostrarResultados.set(true);
      }
      this.buscando.set(false);
    });
  }
  onInput(value: string): void {
    this.searchTerm.set(value);
    this.busqueda$.next(value);
  }
  onSearch(): void {
    this.busqueda$.next(this.searchTerm());
  }
  clearSearch(): void {
    this.searchTerm.set('');
    this.resultados.set({ canciones: [], albums: [], artistas: [], playlists: [] });
    this.mostrarResultados.set(false);
  }
  reproducir(cancion: IPista): void {
    if (!cancion.album) return;
    this.albumService.buscarAlbums(cancion.album).pipe(
      switchMap(albums => {
        const album = albums.find(a => a.titulo === cancion.album);
        if (!album) return of(null);
        return this.albumService.getAlbum(album.id);
      }),
      catchError(() => of(null))
    ).subscribe(albumCompleto => {
      if (!albumCompleto) return;
      const pista = albumCompleto.canciones.find(p => p.titulo === cancion.titulo);
      if (!pista) return;
      const reproduccion: IPistaReproduccion = {
        idPista: pista.idPista,
        titulo: pista.titulo,
        artistas: pista.artistas ?? [cancion.artista],
        urlPortada: pista.urlPortada ?? cancion.caratula,
        urlCancion: pista.urlCancion,
        duracionSegundos: pista.duracionSegundos,
        reproduciendo: true,
      };
      this.storage.Reproducir(reproduccion);
      this.mostrarResultados.set(false);
    });
  }
  cerrarResultados(): void {
    this.mostrarResultados.set(false);
  }
  toggleSidebar(): void {
    this.isSidebarOpenSignal.update(v => !v);
  }
}
