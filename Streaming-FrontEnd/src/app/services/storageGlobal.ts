import {inject, Injectable, PLATFORM_ID, signal, untracked, WritableSignal} from '@angular/core';
import {isPlatformBrowser} from '@angular/common';
import IPistaReproduccion from '../model/pista/IPistaReproduccion';
import IPistaCola from '../model/pista/IPistaCola';

@Injectable ({ providedIn: 'root' })
export class StorageGlobal {
  private readonly _platformId = inject(PLATFORM_ID);

  private _audio: HTMLAudioElement | null = null;
  private _audioGeneration = 0;

  // Señales de estado del reproductor
  readonly reproduciendo = signal<boolean>(false);
  readonly tiempoActual  = signal<number>(0);
  readonly duracion      = signal<number>(0);
  readonly volumen       = signal<number>(1);
  readonly silenciado    = signal<boolean>(false);

  // Cola de reproducción
  readonly colaOriginal = signal<IPistaCola[]>([]);
  readonly cola = signal<IPistaCola[]>([]);
  readonly isShuffled = signal<boolean>(false);
  private readonly _currentColaIndex = signal<number>(-1);

  SetCola(lista: IPistaCola[]): void {
    this.colaOriginal.set(lista);
    if (this.isShuffled()) {
      this.aplicarShuffle(lista);
    } else {
      this.cola.set(lista);
    }
    const idx = this.cola().findIndex(p => p.reproduciendo);
    const playIdx = idx !== -1 ? idx : 0;
    this._currentColaIndex.set(playIdx);
    const toPlay = this.cola()[playIdx];
    if (toPlay) {
      this._reproducirEnIndice(playIdx);
    }
  }

  ToggleShuffle(): void {
    const shuffle = !this.isShuffled();
    this.isShuffled.set(shuffle);
    if (shuffle) {
      this.aplicarShuffle(this.colaOriginal());
    } else {
      const curr = this.cola().find(p => p.reproduciendo);
      const original = [...this.colaOriginal()].map(p => ({
        ...p,
        reproduciendo: curr ? p.idPista === curr.idPista : p.reproduciendo
      }));
      this.cola.set(original);
    }
  }

  private aplicarShuffle(lista: IPistaCola[]): void {
    const arr = [...lista];
    const curr = arr.find(p => p.reproduciendo);
    const withoutCurr = curr ? arr.filter(p => !p.reproduciendo) : arr;
    for (let i = withoutCurr.length - 1; i > 0; i--) {
      const j = Math.floor(Math.random() * (i + 1));
      [withoutCurr[i], withoutCurr[j]] = [withoutCurr[j], withoutCurr[i]];
    }
    if (curr) {
      this.cola.set([curr, ...withoutCurr]);
    } else {
      this.cola.set(withoutCurr);
    }
  }

  /** Reproduce a partir de elemento de la cola */
  private ReproducirConCola(pista: IPistaCola): void {
    const idx = this.cola().findIndex(p => p === pista || (p.idPista && p.idPista === pista.idPista && p.orden === pista.orden));
    if (idx !== -1) {
      this._reproducirEnIndice(idx);
    }
  }

  /** Reproduce la pista en el índice absoluto de la cola (público para componentes) */
  reproducirEnIndicePublico(idx: number): void {
    this._reproducirEnIndice(idx);
  }

  /** Reproduce la pista en el índice absoluto de la cola */
  private _reproducirEnIndice(idx: number): void {
    const colaActual = this.cola();
    if (idx < 0 || idx >= colaActual.length) return;

    this._currentColaIndex.set(idx);
    const lista = colaActual.map((p, i) => ({ ...p, reproduciendo: i === idx }));
    this.cola.set(lista);

    const pista = lista[idx];
    this.Reproducir({
      idPista: pista.idPista || 0,
      titulo: pista.titulo,
      artistas: pista.artistas,
      urlPortada: pista.urlPortada,
      urlCancion: pista.urlCancion,
      duracionSegundos: pista.duracionSegundos,
      reproduciendo: true
    });
  }

  /** Añade una pista al final de la cola */
  AgregarACola(pista: IPistaCola): void {
    this.colaOriginal.update(c => [...c, pista]);
    this.cola.update(c => [...c, pista]);
  }

  /** Añade una pista justo después del último bloque prioritario (prioridad FIFO) */
  AgregarSiguienteEnCola(pista: IPistaCola): void {
    const insertarEnBloquePrioridad = (lista: IPistaCola[]): IPistaCola[] => {
      const currIdx = lista.findIndex(p => p.reproduciendo);
      if (currIdx === -1) return [...lista, { ...pista, esPrioridad: true }];

      // Busca el último índice del bloque de canciones prioritarias tras la actual
      let insertIdx = currIdx + 1;
      while (insertIdx < lista.length && lista[insertIdx].esPrioridad) {
        insertIdx++;
      }

      const result = [...lista];
      result.splice(insertIdx, 0, { ...pista, esPrioridad: true });
      return result;
    };
    this.colaOriginal.update(insertarEnBloquePrioridad);
    this.cola.update(insertarEnBloquePrioridad);
  }

  /** Elimina una pista de la cola por su índice */
  EliminarDeCola(index: number): void {
    this.cola.update(c => c.filter((_, i) => i !== index));
  }

  /** Vacía la cola completa */
  VaciarCola(): void {
    this.colaOriginal.set([]);
    this.cola.set([]);
  }

  /** Reproduce la siguiente pista de la cola (si hay) */
  ReproducirSiguienteDeCola(): void {
    const idx = this._currentColaIndex();
    const siguiente = idx + 1;
    if (siguiente < this.cola().length) {
      this._reproducirEnIndice(siguiente);
    }
  }

  /** Reproduce la anterior pista de la cola (si hay) */
  ReproducirAnteriorDeCola(): void {
    const idx = this._currentColaIndex();
    if (idx > 0) {
      this._reproducirEnIndice(idx - 1);
    }
  }

  private _token: WritableSignal<string> = signal<string>('');
  private _reproduccion: WritableSignal<IPistaReproduccion> = signal<IPistaReproduccion>(
    {
      idPista: 0,
      titulo: '',
      artistas: [],
      urlPortada: '',
      urlCancion: '',
      duracionSegundos: 0,
      reproduciendo: false,
    }
  );

  GetToken(): WritableSignal<string> {
    if (!this._token()) {
      const token = localStorage.getItem('token') || '';
      this._token.set(token);
    }
    return this._token;
  }

  SetToken(token: string): void {
    this._token.set(token);
    if (token) {
      localStorage.setItem('token', token);
    } else {
      localStorage.removeItem('token');
    }
  }

  GetReproduccion(): WritableSignal<IPistaReproduccion> {
    if (!this._reproduccion().urlCancion) {
      const datosReproduccion = JSON.parse(sessionStorage.getItem('reproduccion') || 'null');
      if (datosReproduccion) {
        untracked(() => this._reproduccion.set(datosReproduccion));
      }
    }
    return this._reproduccion;
  }

  SetReproduccion(reproduccion: IPistaReproduccion | null): void {
    this._reproduccion.update((reproduccionVieja: IPistaReproduccion) => {
      if (reproduccionVieja.urlCancion && reproduccion) {
        return { ...reproduccionVieja, ...reproduccion };
      } else {
        return reproduccion ?? {
          idPista: 0,
          titulo: '',
          artistas: [],
          urlPortada: '',
          urlCancion: '',
          duracionSegundos: 0,
          reproduciendo: false,
        };
      }
    });
    if (reproduccion) {
      sessionStorage.setItem('reproduccion', JSON.stringify(reproduccion));
    } else {
      sessionStorage.removeItem('reproduccion');
    }
  }

  /**
   * Carga la pista en el HTMLAudioElement sin iniciar la reproducción.
   * Útil para restaurar el estado tras un refresh de página.
   */
  CargarSinReproducir(pista: IPistaReproduccion): void {
    if (!isPlatformBrowser(this._platformId)) return;

    this._destruirAudio();
    this.SetReproduccion({ ...pista, reproduciendo: false });

    this._audio = new Audio(pista.urlCancion);
    this._audio.volume  = this.volumen();
    this._audio.muted   = this.silenciado();
    this._audio.preload = 'metadata';

    this._audio.addEventListener('loadedmetadata', () => {
      this.duracion.set(this._audio!.duration);
    });

    this._audio.addEventListener('timeupdate', () => {
      this.tiempoActual.set(this._audio!.currentTime);
    });

    this._audio.addEventListener('ended', () => {
      this.reproduciendo.set(false);
      this.tiempoActual.set(0);
      this._actualizarEstadoReproduccion(false);
    });

    this._audio.addEventListener('error', () => {
      console.error('❌ StorageGlobal - Error al cargar la canción:', pista.urlCancion);
      this.reproduciendo.set(false);
      this._actualizarEstadoReproduccion(false);
    });

    // NO llamamos a .play() — solo cargamos metadatos
    this.reproduciendo.set(false);
  }

  // ─── Reproducción ────────────────────────────────────────────────────────

  /**
   * Carga la pista indicada y comienza la reproducción. */
  Reproducir(pista: IPistaReproduccion): void {
    if (!isPlatformBrowser(this._platformId)) return;

    this._destruirAudio();
    const generation = ++this._audioGeneration;

    this.SetReproduccion(pista);

    this._audio = new Audio(pista.urlCancion);
    this._audio.volume = this.volumen();
    this._audio.muted  = this.silenciado();

    this._audio.addEventListener('loadedmetadata', () => {
      if (this._audioGeneration !== generation) return;
      this.duracion.set(this._audio!.duration);
    });

    this._audio.addEventListener('timeupdate', () => {
      if (this._audioGeneration !== generation) return;
      this.tiempoActual.set(this._audio!.currentTime);
    });

    this._audio.addEventListener('ended', () => {
      if (this._audioGeneration !== generation) return;
      this.reproduciendo.set(false);
      this.tiempoActual.set(0);
      this._actualizarEstadoReproduccion(false);
      this.ReproducirSiguienteDeCola();
    });

    this._audio.addEventListener('error', () => {
      console.error('❌ StorageGlobal - Error al cargar la canción:', pista.urlCancion);
      this.reproduciendo.set(false);
      this._actualizarEstadoReproduccion(false);
    });

    this._audio.play().then(() => {
      this.reproduciendo.set(true);
      this._actualizarEstadoReproduccion(true);
    }).catch(err => {
      console.error('❌ StorageGlobal - Error al reproducir:', err);
      this.reproduciendo.set(false);
    });
  }

  /** Pausa la reproducción en curso. */
  Pausar(): void {
    if (!this._audio || this._audio.paused) return;
    this._audio.pause();
    this.reproduciendo.set(false);
    this._actualizarEstadoReproduccion(false);
  }

  /** Reanuda la reproducción si estaba pausada. */
  Reanudar(): void {
    if (!this._audio || !this._audio.paused) return;
    this._audio.play().then(() => {
      this.reproduciendo.set(true);
      this._actualizarEstadoReproduccion(true);
    }).catch(err => console.error('❌ StorageGlobal - Error al reanudar:', err));
  }

  /** Alterna entre reproducir y pausar. */
  TogglePlay(): void {
    if (this.reproduciendo()) {
      this.Pausar();
    } else {
      this.Reanudar();
    }
  }

  /** Detiene la reproducción y reinicia la pista al inicio. */
  Parar(): void {
    if (!this._audio) return;
    this._audio.pause();
    this._audio.currentTime = 0;
    this.reproduciendo.set(false);
    this.tiempoActual.set(0);
    this._actualizarEstadoReproduccion(false);
  }

  /** Salta al instante indicado (en segundos). */
  BuscarTiempo(segundos: number): void {
    if (!this._audio) return;
    this._audio.currentTime = Math.max(0, Math.min(segundos, this._audio.duration || 0));
    this.tiempoActual.set(this._audio.currentTime);
  }

  /** Establece el volumen (0.0 – 1.0). */
  SetVolumen(nivel: number): void {
    const v = Math.max(0, Math.min(1, nivel));
    this.volumen.set(v);
    if (this._audio) this._audio.volume = v;
  }

  /** Activa o desactiva el silencio. */
  ToggleSilencio(): void {
    const nuevo = !this.silenciado();
    this.silenciado.set(nuevo);
    if (this._audio) this._audio.muted = nuevo;
  }

  // ─── Helpers privados ────────────────────────────────────────────────────

  /** Actualiza el campo `reproduciendo` dentro de la señal de pista. */
  private _actualizarEstadoReproduccion(estado: boolean): void {
    this._reproduccion.update(r => ({ ...r, reproduciendo: estado }));
  }

  /** Destruye la instancia de Audio anterior liberando recursos. */
  private _destruirAudio(): void {
    if (!this._audio) return;
    this._audio.pause();
    this._audio.src = '';
    this._audio.load();
    this._audio = null;
    this.reproduciendo.set(false);
    this.tiempoActual.set(0);
    this.duracion.set(0);
  }

}
