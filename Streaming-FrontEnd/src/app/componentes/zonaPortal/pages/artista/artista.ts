import {
  ChangeDetectionStrategy,
  Component,
  OnInit,
  inject,
  signal,
  WritableSignal,
} from '@angular/core';
import { ActivatedRoute, Router } from '@angular/router';
import { IArtista } from '../../../../model/artista/IArtista';
import { ListaCanciones } from '../compartido/lista-canciones/lista-canciones';
import { AlbumCard } from '../compartido/album-card/album-card';
import { ArtistaService } from '../../../../services/artistaService';

@Component({
  selector: 'app-artista',
  imports: [ListaCanciones, AlbumCard],
  templateUrl: './artista.html',
  styleUrl: './artista.css',
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class Artista implements OnInit {
  private readonly artistaService: ArtistaService = inject(ArtistaService);
  private readonly router: Router = inject(Router);
  private readonly activatedRoute: ActivatedRoute = inject(ActivatedRoute);

  /**
   * Artista obtenido del backend
   */
  readonly artista: WritableSignal<IArtista | null> = signal<IArtista | null>(null);

  protected readonly cargando: WritableSignal<boolean> = signal(true);
  protected readonly error: WritableSignal<string | null> = signal<string | null>(null);
  protected readonly mostrarTodasLasCanciones: WritableSignal<boolean> = signal(false);

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
}

