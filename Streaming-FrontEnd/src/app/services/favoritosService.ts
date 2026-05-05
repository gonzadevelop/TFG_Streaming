import { inject, Injectable, signal, WritableSignal } from '@angular/core';
import { PlaylistService } from './playlistService';
import { IPista } from '../model/pista/IPista';

/**
 * Servicio singleton que gestiona el estado global de las pistas favoritas.
 * Mantiene en memoria tanto el Set de IDs como la lista completa de IPista
 * para que lista-favoritos se actualice reactivamente sin peticiones extra.
 */
@Injectable({ providedIn: 'root' })
export class FavoritosService {
  private readonly playlistService = inject(PlaylistService);

  private readonly _favoritosIds: WritableSignal<Set<number>> = signal<Set<number>>(new Set());
  private readonly _favoritosPistas: WritableSignal<IPista[]> = signal<IPista[]>([]);

  /** Signal de solo lectura con el Set de IDs favoritos */
  readonly favoritosIds = this._favoritosIds.asReadonly();

  /** Signal de solo lectura con la lista completa de pistas favoritas */
  readonly favoritosPistas = this._favoritosPistas.asReadonly();

  private _cargado = false;

  /**
   * Carga los favoritos completos desde el backend.
   * Si ya están cargados, solo ejecuta onComplete sin volver a pedir al backend.
   * Pasa force:true para forzar la recarga (ej: al entrar en lista-favoritos).
   */
  cargarFavoritos(callbacks?: { onComplete?: () => void; onError?: (msg: string) => void; force?: boolean }): void {
    if (this._cargado && !callbacks?.force) {
      callbacks?.onComplete?.();
      return;
    }
    this.playlistService.getFavoritos().subscribe({
      next: (playlist) => {

        // Log temporal para identificar la estructura exacta que devuelve el backend

        if (playlist.pistas?.length) {
          console.log('[FavoritosService] Ejemplo artistas del backend:', playlist.pistas[0].artistas);
        }
        const pistas = (playlist.pistas ?? []).map(p => ({
          ...p,

          // El endpoint de favoritos puede devolver artistas como objetos con distintas claves.
          // Normalizamos a string[] probando los campos más comunes.

          artistas: (p.artistas as unknown as Array<string | Record<string, unknown>>)
            .map(a => {
              if (typeof a === 'string') return a;
              // Intentamos las claves más habituales en backends Spring Boot
              return (
                (a['nombreArtistico'] as string | undefined) ??
                (a['username'] as string | undefined) ??
                (a['nombre'] as string | undefined) ??
                (a['alias'] as string | undefined) ??
                (a['name'] as string | undefined) ??
                ''
              );
            })
            .filter(a => a !== ''),
        }));
        this._favoritosPistas.set(pistas);
        this._favoritosIds.set(new Set(pistas.map(p => p.idPista)));
        this._cargado = true;
        callbacks?.onComplete?.();
      },
      error: (err: unknown) => {
        console.error('Error cargando favoritos:', err);
        callbacks?.onError?.('No se pudieron cargar los favoritos.');
      },
    });
  }

  /** Comprueba si una pista está marcada como favorita */
  esFavorito(idPista: number): boolean {
    return this._favoritosIds().has(idPista);
  }

  /**
   * Alterna el estado de favorito de una pista.
   * Actualiza ambas signals de forma optimista y revierte en caso de error.
   */
  toggleFavorito(pista: IPista): void {
    const eraFavorito = this._favoritosIds().has(pista.idPista);

    // Actualización optimista de IDs
    this._favoritosIds.update(ids => {
      const nuevo = new Set(ids);
      if (eraFavorito) {
        nuevo.delete(pista.idPista);
      } else {
        nuevo.add(pista.idPista);
      }
      return nuevo;
    });

    // Actualización optimista de la lista de pistas
    this._favoritosPistas.update(lista => {
      if (eraFavorito) {
        return lista.filter(p => p.idPista !== pista.idPista);
      } else {
        return [...lista, pista];
      }
    });

    const peticion$ = eraFavorito
      ? this.playlistService.removeFavorito(pista.idPista)
      : this.playlistService.addFavorito(pista.idPista);

    peticion$.subscribe({
      error: (err: unknown) => {
        console.error('Error al cambiar favorito, revirtiendo:', err);
        // Revertir IDs
        this._favoritosIds.update(ids => {
          const revertido = new Set(ids);
          if (eraFavorito) {
            revertido.add(pista.idPista);
          } else {
            revertido.delete(pista.idPista);
          }
          return revertido;
        });
        // Revertir lista de pistas
        this._favoritosPistas.update(lista => {
          if (eraFavorito) {
            return [...lista, pista];
          } else {
            return lista.filter(p => p.idPista !== pista.idPista);
          }
        });
      },
    });
  }
}
