import { afterNextRender, ChangeDetectionStrategy, Component, computed, effect, inject, signal, OnDestroy } from '@angular/core';
import { CommonModule } from '@angular/common';
import { Router } from '@angular/router';
import { StorageGlobal } from '../../../../../services/storageGlobal';
import { Cola } from '../Cola/cola';
import { CancionService } from '../../../../../services/cancionService';

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
  private readonly cancionService = inject(CancionService);

  private readonly registeredTrackKey = signal<string | null>(null);

  constructor() {
    afterNextRender(() => {
      const pistaGuardada = this.storage.GetReproduccion()();
      if (pistaGuardada.urlCancion && !this.storage.reproduciendo()) {
        this.storage.CargarSinReproducir(pistaGuardada);
      }
    });

    effect(() => {
      const pista = this.storage.GetReproduccion()();
      const time = this.storage.tiempoActual();
      const trackKey = pista.idPista > 0 ? `${pista.idPista}:${pista.urlCancion}` : null;

      if (!trackKey) {
        this.registeredTrackKey.set(null);
        return;
      }

      if (this.registeredTrackKey() !== trackKey && time >= 30) {
        this.cancionService.setReproducirCancion(pista.idPista).subscribe({
          next: () => this.registeredTrackKey.set(trackKey),
          error: () => this.registeredTrackKey.set(trackKey),
        });
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

  // ─── Info de la cancin actual ────────────────────────────────────────────

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

  protected readonly isShuffled  = this.storage.isShuffled;
  protected readonly repeatMode  = this.storage.repeatMode;
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

  // ─── Mtodos de UI ───────────────────────────────────────────────────────

  toggleVisible(): void   { this.isVisible.update(v => !v); }
  toggleExpanded(): void  { this.isExpanded.update(v => !v); }
  toggleCola(): void      { this.colaVisible.update(v => !v); }
  toggleShuffle(): void   { this.storage.ToggleShuffle(); }

  toggleRepeat(): void {
    this.storage.repeatMode.update(m => {
      if (m === 'none') return 'all';
      if (m === 'all')  return 'one';
      return 'none';
    });
  }

  // ─── Mtodos de reproduccin (delegados al servicio) ─────────────────────

  togglePlay(): void  { this.storage.TogglePlay(); }
  toggleMute(): void  { this.storage.ToggleSilencio(); }

  nextTrack(): void { this.storage.ReproducirSiguienteDeCola(); }
  prevTrack(): void { this.storage.ReproducirAnteriorDeCola(); }

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
