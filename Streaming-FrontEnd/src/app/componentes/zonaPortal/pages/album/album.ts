import {
  ChangeDetectionStrategy,
  Component,
  OnInit,
  inject,
  signal,
  WritableSignal,
} from '@angular/core';
import { ActivatedRoute, Router } from '@angular/router';
import { IAlbumCompleto } from '../../../../model/album/IAlbumCompleto';
import { ListaCanciones } from '../compartido/lista-canciones/lista-canciones';
import { AlbumService } from '../../../../services/albumService';

@Component({
  selector: 'app-album',
  imports: [ListaCanciones],
  templateUrl: './album.html',
  styleUrl: './album.css',
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class Album implements OnInit {
  private readonly albumService: AlbumService = inject(AlbumService);
  private readonly router: Router = inject(Router);
  private readonly activatedRoute: ActivatedRoute = inject(ActivatedRoute);

  /**
   * Album obtenido del backend
   */
  readonly album: WritableSignal<IAlbumCompleto | null> = signal<IAlbumCompleto | null>(null);

  protected readonly cargando: WritableSignal<boolean> = signal(true);
  protected readonly error: WritableSignal<string | null> = signal<string | null>(null);

  ngOnInit(): void {
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
          this.album.set(data);
          this.cargando.set(false);
          console.log('Album cargado:', this.album());
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
}


