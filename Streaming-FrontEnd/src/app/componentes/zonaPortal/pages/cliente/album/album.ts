import {
  ChangeDetectionStrategy,
  Component,
  OnInit,
  inject,
  signal,
  computed,
  WritableSignal,
} from '@angular/core';
import { ActivatedRoute, Router } from '@angular/router';
import { IAlbumCompleto } from '../../../../../model/album/IAlbumCompleto';
import { ListaCanciones } from '../compartido/lista-canciones/lista-canciones';
import { AlbumService } from '../../../../../services/albumService';
import { StorageGlobal } from '../../../../../services/storageGlobal';
import { FavoritosService } from '../../../../../services/favoritosService';
import IPistaCola from '../../../../../model/pista/IPistaCola';
import { KsLoaderComponent } from '../compartido/ks-loader/ks-loader';

@Component({
  selector: 'app-album',
  imports: [ListaCanciones, KsLoaderComponent],
  templateUrl: './album.html',
  styleUrl: './album.css',
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class Album implements OnInit {
  private readonly albumService: AlbumService = inject(AlbumService);
  private readonly router: Router = inject(Router);
  private readonly activatedRoute: ActivatedRoute = inject(ActivatedRoute);
  private readonly storage = inject(StorageGlobal);
  private readonly favoritosService = inject(FavoritosService);

  /**
   * Album obtenido del backend
   */
  readonly album: WritableSignal<IAlbumCompleto | null> = signal<IAlbumCompleto | null>(null);

  protected readonly duracionTotalSegundos = computed(() =>
    (this.album()?.canciones ?? []).reduce((acc, p) => acc + (p.duracionSegundos ?? 0), 0)
  );

  protected readonly cargando: WritableSignal<boolean> = signal(true);
  protected readonly error: WritableSignal<string | null> = signal<string | null>(null);

  ngOnInit(): void {
    // Cargamos favoritos (sin force) para que los corazones reflejen el estado actual
    this.favoritosService.cargarFavoritos();

    // Usar paramMap para reaccionar a cambios en los parámetros de ruta
    this.activatedRoute.paramMap.subscribe(params => {
      const albumId = params.get('id');

      if (!albumId) {
        this.error.set('Album no especificado.');
        this.cargando.set(false);
        return;
      }

      this.cargando.set(true);
      this.error.set(null);

      this.albumService.getAlbum(Number(albumId)).subscribe({
        next: (data: IAlbumCompleto) => {
          const albumNormalizado: IAlbumCompleto = {
            ...data,
            canciones: (data.canciones ?? []).map(p => {
              const raw = p as unknown as Record<string, unknown>;
              const idNormalizado = (p.idPista && p.idPista !== 0)
                ? p.idPista
                : (p.id && p.id !== 0)
                  ? p.id
                  : ((raw['idCancion'] as number) || (raw['cancionId'] as number) || 0);
              return {
                ...p,
                idPista: idNormalizado,
                urlPortada: p.urlPortada || p.caratula || data.portada || '',
                artistas: p.artistas?.length
                  ? p.artistas
                  : p.artista ? [p.artista] : [],
              };
            }),
          };
          this.album.set(albumNormalizado);
          this.cargando.set(false);
        },
        error: (err: unknown) => {
          console.error('Error cargando album:', err);
          this.error.set('No se pudo cargar el album.');
          this.cargando.set(false);
        },
      });
    });
  }

  protected navegarArtista(nombreArtista: string): void {
    this.router.navigate(['/artistas', nombreArtista]);
  }

  protected formatearDuracion(segundos: number): string {
    const horas = Math.floor(segundos / 3600);
    const minutos = Math.floor((segundos % 3600) / 60);
    const segs = segundos % 60;

    if (horas > 0) {
      return `${horas}h ${minutos}min`;
    }
    return `${minutos}min ${segs}seg`;
  }

  protected reproducirTodo(): void {
    const albumData = this.album();
    if (!albumData || !albumData.canciones || albumData.canciones.length === 0) return;

    const cola: IPistaCola[] = albumData.canciones.map((p, index) => ({
      ...p,
      urlPortada: p.urlPortada || albumData.portada,
      orden: index,
      reproduciendo: index === 0
    }));
    this.storage.SetCola(cola);
  }
}
