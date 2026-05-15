import {
  ChangeDetectionStrategy,
  Component,
  OnInit,
  inject,
  signal,
  computed,
} from '@angular/core';
import { RouterLink } from '@angular/router';
import { forkJoin, of } from 'rxjs';
import { catchError } from 'rxjs/operators';
import { EstadisticaService } from '../../../../services/estadisticaService';
import { StorageGlobal } from '../../../../services/storageGlobal';
import { ITopCancion, ITopArtista, ITopAlbum } from '../../../../model/estadistica/IEstadistica';
import IPistaCola from '../../../../model/pista/IPistaCola';

@Component({
  selector: 'app-estadistica',
  imports: [RouterLink],
  templateUrl: './estadistica.html',
  styleUrl: './estadistica.css',
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class Estadistica implements OnInit {
  private readonly estadisticaService = inject(EstadisticaService);
  private readonly storage = inject(StorageGlobal);

  protected readonly cargando = signal(true);
  protected readonly error = signal<string | null>(null);

  protected readonly minutosEscuchados = signal<number>(0);
  protected readonly topCanciones = signal<ITopCancion[]>([]);
  protected readonly topArtistas = signal<ITopArtista[]>([]);
  protected readonly topAlbumes = signal<ITopAlbum[]>([]);

  protected readonly horasEscuchadas = computed(() =>
    Math.floor(this.minutosEscuchados() / 60)
  );

  protected readonly minutosRestantes = computed(() =>
    this.minutosEscuchados() % 60
  );

  ngOnInit(): void {
    forkJoin({
      minutos:   this.estadisticaService.getMinutosMes().pipe(catchError(() => of(0))),
      canciones: this.estadisticaService.getTopCanciones().pipe(catchError(() => of([]))),
      artistas:  this.estadisticaService.getTopArtistas().pipe(catchError(() => of([]))),
      albumes:   this.estadisticaService.getTopAlbumes().pipe(catchError(() => of([]))),
    }).subscribe({
      next: ({ minutos, canciones, artistas, albumes }) => {
        this.minutosEscuchados.set(minutos);

        const rawCanciones = canciones as Record<string, unknown>[];
        if (rawCanciones.length > 0) console.log('📦 [estadistica] keys canción:', Object.keys(rawCanciones[0]));
        this.topCanciones.set(
          rawCanciones.map(c => ({
            idPista:          (c['idPista'] ?? c['id'] ?? 0) as number,
            titulo:           (c['titulo'] ?? '') as string,
            artistas:         ((c['artistas'] ?? []) as string[]),
            urlPortada:       (c['urlPortada'] ?? c['caratula'] ?? '') as string,
            urlCancion:       (c['urlCancion'] ?? c['url'] ?? '') as string,
            duracionSegundos: (c['duracionSegundos'] ?? 0) as number,
            reproducciones:   (c['reproducciones'] ?? 0) as number,
          }))
        );

        this.topArtistas.set(
          (artistas as Record<string, unknown>[]).map(a => ({
            username:      (a['username'] ?? '') as string,
            nombre:        (a['nombre'] ?? a['name'] ?? '') as string,
            urlFotoPerfil: (a['urlFotoPerfil'] ?? a['urlAvatar'] ?? undefined) as string | undefined,
            reproducciones:(a['reproducciones'] ?? 0) as number,
          }))
        );

        this.topAlbumes.set(
          (albumes as Record<string, unknown>[]).map(a => ({
            id:             (a['id'] ?? 0) as number,
            titulo:         (a['titulo'] ?? '') as string,
            artista:        (a['artista'] ?? '') as string,
            urlPortada:     (a['urlPortada'] ?? a['caratula'] ?? '') as string,
            reproducciones: (a['reproducciones'] ?? 0) as number,
          }))
        );

        this.cargando.set(false);
      },
      error: () => {
        this.error.set('Error al cargar las estadísticas.');
        this.cargando.set(false);
      },
    });
  }

  protected reproducirCancion(cancion: ITopCancion): void {
    if (!cancion.urlCancion) return;
    const pistaCola: IPistaCola = {
      idPista: cancion.idPista,
      titulo: cancion.titulo,
      artistas: cancion.artistas,
      urlPortada: cancion.urlPortada,
      urlCancion: cancion.urlCancion,
      duracionSegundos: cancion.duracionSegundos,
      reproduciendo: true,
      orden: 0,
    };
    this.storage.SetCola([pistaCola]);
  }

  protected formatearDuracion(segundos: number): string {
    const m = Math.floor(segundos / 60);
    const s = segundos % 60;
    return `${m}:${s.toString().padStart(2, '0')}`;
  }
}



