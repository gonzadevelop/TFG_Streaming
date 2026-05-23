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
import { catchError, switchMap } from 'rxjs/operators';
import { EstadisticaService } from '../../../../../services/estadisticaService';
import { StorageGlobal } from '../../../../../services/storageGlobal';
import { UserService } from '../../../../../services/userService';
import { ITopCancion, ITopArtista, ITopAlbum } from '../../../../../model/estadistica/IEstadistica';
import IPistaCola from '../../../../../model/pista/IPistaCola';
import { KsLoaderComponent } from '../compartido/ks-loader/ks-loader';
import { ScrollRevealDirective } from '../../../../../shared/directives/scroll-reveal.directive';

@Component({
  selector: 'app-estadistica',
  imports: [RouterLink, KsLoaderComponent, ScrollRevealDirective],
  templateUrl: './estadistica.html',
  styleUrl: './estadistica.css',
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class Estadistica implements OnInit {
  private readonly estadisticaService = inject(EstadisticaService);
  private readonly storage = inject(StorageGlobal);
  private readonly userService = inject(UserService);

  protected readonly cargando = signal(true);
  protected readonly error = signal<string | null>(null);

  protected readonly segundosEscuchados = signal<number>(0);
  protected readonly topCanciones = signal<ITopCancion[]>([]);
  protected readonly topArtistas = signal<ITopArtista[]>([]);
  protected readonly topAlbumes = signal<ITopAlbum[]>([]);

  protected readonly horasEscuchadas = computed(() =>
    Math.floor(this.segundosEscuchados() / 3600)
  );

  protected readonly minutosRestantes = computed(() =>
    Math.floor((this.segundosEscuchados() % 3600) / 60)
  );

  protected readonly totalMinutos = computed(() =>
    Math.floor(this.segundosEscuchados() / 60)
  );

  ngOnInit(): void {
    forkJoin({
      minutos:   this.estadisticaService.getMinutosMes().pipe(catchError(() => of(0 as unknown))),
      canciones: this.estadisticaService.getTopCanciones().pipe(catchError(() => of([]))),
      artistas:  this.estadisticaService.getTopArtistas().pipe(catchError(() => of([]))),
      albumes:   this.estadisticaService.getTopAlbumes().pipe(catchError(() => of([]))),
    }).subscribe({
      next: ({ minutos, canciones, artistas, albumes }) => {
        // El backend devuelve segundos (segundosEscuchadosMes)
        let segundosTotal: number;
        if (typeof minutos === 'number') {
          segundosTotal = minutos;
        } else if (minutos && typeof minutos === 'object') {
          const obj = minutos as Record<string, unknown>;
          if (typeof obj['segundosEscuchadosMes'] === 'number') {
            segundosTotal = obj['segundosEscuchadosMes'] as number;
          } else if (typeof obj['segundos'] === 'number') {
            segundosTotal = obj['segundos'] as number;
          } else if (typeof obj['totalSegundos'] === 'number') {
            segundosTotal = obj['totalSegundos'] as number;
          } else if (typeof obj['minutos'] === 'number') {
            segundosTotal = (obj['minutos'] as number) * 60;
          } else {
            const firstNum = Object.values(obj).find(v => typeof v === 'number');
            segundosTotal = typeof firstNum === 'number' ? firstNum : 0;
          }
        } else {
          segundosTotal = 0;
        }
        this.segundosEscuchados.set(segundosTotal);

        const rawCanciones = canciones as Record<string, unknown>[];
        this.topCanciones.set(
          rawCanciones.map(c => {
            // artistas puede ser: string[], objeto[], o no existir
            const raw = c['artistas'];
            let artistasArray: string[] = [];
            if (Array.isArray(raw) && raw.length > 0) {
              artistasArray = raw.map((a: unknown) =>
                typeof a === 'string' ? a : ((a as Record<string, unknown>)['nombre'] ?? (a as Record<string, unknown>)['name'] ?? '') as string
              ).filter(Boolean);
            }
            if (artistasArray.length === 0 && c['artista']) {
              artistasArray = [(c['artista'] as string)];
            }
            return {
              idPista:          (c['idPista'] ?? c['id'] ?? 0) as number,
              titulo:           (c['titulo'] ?? '') as string,
              artistas:         artistasArray,
              artistasUsername: (() => {
                const raw2 = c['artistasUsername'] ?? c['artistaUsername'];
                if (Array.isArray(raw2) && raw2.length > 0) return raw2 as string[];
                const rawArt = c['artistas'];
                if (Array.isArray(rawArt) && rawArt.length > 0 && typeof rawArt[0] === 'object') {
                  return (rawArt as Record<string, unknown>[])
                    .map(a => (a['username'] ?? '') as string)
                    .filter(Boolean);
                }
                return undefined;
              })(),
              urlPortada:       (c['urlPortada'] ?? c['caratula'] ?? '') as string,
              urlCancion:       (c['urlCancion'] ?? c['url'] ?? '') as string,
              duracionSegundos: (c['duracionSegundos'] ?? 0) as number,
              reproducciones:   (c['reproducciones'] ?? 0) as number,
            };
          })
        );

        const rawArtistas = artistas as Record<string, unknown>[];
        const artistasMapeados: ITopArtista[] = rawArtistas.map(a => ({
            username:      (a['username'] ?? '') as string,
            nombre:        (a['nombre'] ?? a['name'] ?? '') as string,
            urlFotoPerfil: (a['urlFotoPerfil'] ?? a['urlAvatar'] ?? undefined) as string | undefined,
            reproducciones:(a['reproducciones'] ?? 0) as number,
        }));
        this.topArtistas.set(artistasMapeados);

        // Enriquecer con fotos de perfil en paralelo
        const perfilRequests = artistasMapeados.map(a =>
          a.username
            ? this.userService.getPerfilUsuario(a.username).pipe(catchError(() => of(null)))
            : of(null)
        );
        forkJoin(perfilRequests).subscribe(perfiles => {
          this.topArtistas.set(
            artistasMapeados.map((a, i) => ({
              ...a,
              urlFotoPerfil: perfiles[i]?.urlAvatar
                ? (perfiles[i]!.urlAvatar.includes('ui-avatars') ? undefined : perfiles[i]!.urlAvatar)
                : a.urlFotoPerfil,
            }))
          );
        });

        this.topAlbumes.set(
          (albumes as Record<string, unknown>[]).map(a => ({
            id:             (a['albumId'] ?? a['id'] ?? 0) as number,
            titulo:         (a['titulo'] ?? '') as string,
            artista:        (a['artista'] ?? '') as string,
            artistas:       ((a['artistas'] as string[])?.length ? a['artistas'] as string[] : (a['artista'] ? [a['artista'] as string] : [])),
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



