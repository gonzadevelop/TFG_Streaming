import { afterNextRender, ChangeDetectionStrategy, Component, computed, inject, signal, OnDestroy } from '@angular/core';
import { CommonModule } from '@angular/common';
import { StorageGlobal } from '../../../../services/storageGlobal';

@Component({
  selector: 'app-player',
  imports: [CommonModule],
  templateUrl: './player.html',
  styleUrl: './player.scss',
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class Player implements OnDestroy {
  private readonly storage = inject(StorageGlobal);

  constructor() {
    // Tras el primer render (solo browser), restaurar la canción guardada en sessionStorage
    afterNextRender(() => {
      const pistaGuardada = this.storage.GetReproduccion()();
      if (pistaGuardada.urlCancion && !this.storage.reproduciendo()) {
        this.storage.CargarSinReproducir(pistaGuardada);
      }
    });
  }

  // ─── Estado del reproductor (delegado al servicio) ────────────────────────

  /** true si hay audio reproduciéndose */
  protected readonly isPlaying = this.storage.reproduciendo;

  /** true si el audio está silenciado */
  protected readonly isMuted = this.storage.silenciado;

  /** Volumen en rango 0-100 para el input[range] del template */
  protected readonly volume = computed(() => Math.round(this.storage.volumen() * 100));

  /** Tiempo actual en segundos */
  protected readonly currentTime = computed(() => Math.floor(this.storage.tiempoActual()));

  /** Duración total en segundos */
  protected readonly duration = computed(() => Math.floor(this.storage.duracion()));

  /** Progreso en segundos (alias de currentTime para la barra) */
  protected readonly progress = this.currentTime;

  // ─── Info de la canción actual ────────────────────────────────────────────

  protected readonly currentSong = computed(() => {
    const pista = this.storage.GetReproduccion()();
    if (!pista.urlCancion) {
      return {
        title:    'Sin reproducción activa',
        artist:   'KeySound',
        cover:    null as string | null,
        duracion: 0,
        activa:   false,
      };
    }
    return {
      title:    pista.titulo,
      artist:   pista.artistas.length > 0 ? pista.artistas.join(', ') : 'Desconocido',
      cover:    pista.urlPortada || null,
      duracion: pista.duracionSegundos,
      activa:   true,
    };
  });

  // ─── Estado local de UI ───────────────────────────────────────────────────

  protected readonly isShuffled  = signal(false);
  protected readonly repeatMode  = signal<'none' | 'all' | 'one'>('none');
  protected readonly isVisible   = signal(true);
  protected readonly isExpanded  = signal(false);

  // ─── Computadas de UI ────────────────────────────────────────────────────

  protected readonly progressPercent = computed(() =>
    this.duration() > 0 ? (this.progress() / this.duration()) * 100 : 0
  );

  protected readonly formattedCurrent  = computed(() => this.formatTime(this.currentTime()));
  protected readonly formattedDuration = computed(() => this.formatTime(this.duration()));

  protected readonly repeatIcon = computed(() => {
    switch (this.repeatMode()) {
      case 'one': return 'repeat_one';
      case 'all': return 'repeat';
      default:    return 'repeat';
    }
  });

  // ─── Métodos de UI ───────────────────────────────────────────────────────

  toggleVisible(): void {
    this.isVisible.update(v => !v);
  }

  toggleExpanded(): void {
    this.isExpanded.update(v => !v);
  }

  toggleShuffle(): void {
    this.isShuffled.update(v => !v);
  }

  toggleRepeat(): void {
    this.repeatMode.update(m => {
      if (m === 'none') return 'all';
      if (m === 'all')  return 'one';
      return 'none';
    });
  }

  // ─── Métodos de reproducción (delegados al servicio) ─────────────────────

  togglePlay(): void {
    this.storage.TogglePlay();
  }

  toggleMute(): void {
    this.storage.ToggleSilencio();
  }

  onVolumeChange(event: Event): void {
    const input = event.target as HTMLInputElement;
    // El input trabaja en 0-100; el servicio espera 0.0-1.0
    this.storage.SetVolumen(Number(input.value) / 100);
  }

  onProgressClick(event: MouseEvent): void {
    const bar   = event.currentTarget as HTMLElement;
    const rect  = bar.getBoundingClientRect();
    const ratio = (event.clientX - rect.left) / rect.width;
    const newTime = ratio * this.duration();
    this.storage.BuscarTiempo(newTime);
  }

  // ─── Helpers ─────────────────────────────────────────────────────────────

  private formatTime(seconds: number): string {
    const m = Math.floor(seconds / 60);
    const s = Math.floor(seconds % 60);
    return `${m}:${s.toString().padStart(2, '0')}`;
  }

  ngOnDestroy(): void {
    this.storage.Pausar();
  }
}
