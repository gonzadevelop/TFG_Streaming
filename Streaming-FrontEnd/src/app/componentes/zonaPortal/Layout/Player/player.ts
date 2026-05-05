import { afterNextRender, ChangeDetectionStrategy, Component, computed, inject, signal, OnDestroy } from '@angular/core';
import { CommonModule } from '@angular/common';
import { Router } from '@angular/router';
import { StorageGlobal } from '../../../../services/storageGlobal';
import { Cola } from '../Cola/cola';

@Component({
  selector: 'app-player',
  imports: [CommonModule, Cola],
  templateUrl: './player.html',
  styleUrl: './player.scss',
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class Player implements OnDestroy {
  private readonly storage = inject(StorageGlobal);
  private readonly router  = inject(Router);

  constructor() {
    afterNextRender(() => {
      const pistaGuardada = this.storage.GetReproduccion()();
      if (pistaGuardada.urlCancion && !this.storage.reproduciendo()) {
        this.storage.CargarSinReproducir(pistaGuardada);
      }
    });
  }

  // ─── Estado del reproductor (delegado al servicio) ────────────────────────

  protected readonly isPlaying   = this.storage.reproduciendo;
  protected readonly isMuted     = this.storage.silenciado;
  protected readonly volume      = computed(() => Math.round(this.storage.volumen() * 100));
  protected readonly currentTime = computed(() => Math.floor(this.storage.tiempoActual()));
  protected readonly duration    = computed(() => Math.floor(this.storage.duracion()));
  protected readonly progress    = this.currentTime;

  // ─── Info de la canción actual ────────────────────────────────────────────

  protected readonly currentSong = computed(() => {
    const pista = this.storage.GetReproduccion()();
    if (!pista.urlCancion) {
      return {
        title:   'Sin reproducción activa',
        artist:  'KeySound',
        artists: [] as string[],
        cover:   null as string | null,
        duracion: 0,
        activa:  false,
      };
    }
    return {
      title:   pista.titulo,
      artist:  pista.artistas.length > 0 ? pista.artistas.join(', ') : 'Desconocido',
      artists: pista.artistas,
      cover:   pista.urlPortada || null,
      duracion: pista.duracionSegundos,
      activa:  true,
    };
  });

  // ─── Estado local de UI ───────────────────────────────────────────────────

  protected readonly isShuffled  = signal(false);
  protected readonly repeatMode  = signal<'none' | 'all' | 'one'>('none');
  protected readonly isVisible   = signal(true);
  protected readonly isExpanded  = signal(false);
  protected readonly colaVisible = signal(false);

  // ─── Computadas de UI ────────────────────────────────────────────────────

  protected readonly colaLength = computed(() => this.storage.cola().length);

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

  toggleVisible(): void   { this.isVisible.update(v => !v); }
  toggleExpanded(): void  { this.isExpanded.update(v => !v); }
  toggleCola(): void      { this.colaVisible.update(v => !v); }
  toggleShuffle(): void   { this.isShuffled.update(v => !v); }

  toggleRepeat(): void {
    this.repeatMode.update(m => {
      if (m === 'none') return 'all';
      if (m === 'all')  return 'one';
      return 'none';
    });
  }

  // ─── Métodos de reproducción (delegados al servicio) ─────────────────────

  togglePlay(): void  { this.storage.TogglePlay(); }
  toggleMute(): void  { this.storage.ToggleSilencio(); }

  onVolumeChange(event: Event): void {
    const input = event.target as HTMLInputElement;
    this.storage.SetVolumen(Number(input.value) / 100);
  }

  onProgressClick(event: MouseEvent): void {
    const bar    = event.currentTarget as HTMLElement;
    const rect   = bar.getBoundingClientRect();
    const ratio  = (event.clientX - rect.left) / rect.width;
    this.storage.BuscarTiempo(ratio * this.duration());
  }

  // ─── Helpers ─────────────────────────────────────────────────────────────

  private formatTime(seconds: number): string {
    const m = Math.floor(seconds / 60);
    const s = Math.floor(seconds % 60);
    return `${m}:${s.toString().padStart(2, '0')}`;
  }

  ngOnDestroy(): void { this.storage.Pausar(); }

  protected navegarArtista(nombreArtista: string): void {
    this.router.navigate(['/artistas', nombreArtista]);
  }
}
