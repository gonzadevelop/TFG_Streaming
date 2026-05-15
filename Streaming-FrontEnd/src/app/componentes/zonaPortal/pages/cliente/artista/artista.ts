import {ChangeDetectionStrategy, Component, inject, OnInit} from '@angular/core';
import { ActivatedRoute, Router } from '@angular/router';
import { ListaCanciones } from '../compartido/lista-canciones/lista-canciones';
import { AlbumCard } from '../compartido/album-card/album-card';
import { IArtista } from '../../../../../model/artista/IArtista';
import { ArtistaService } from '../../../../../services/artistaService';
import { UserService } from '../../../../../services/userService';
import { signal, WritableSignal } from '@angular/core';

@Component({
  selector: 'app-artista',
  standalone: true,
  imports: [ListaCanciones, AlbumCard],
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
