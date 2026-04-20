import { ChangeDetectionStrategy, Component, signal, computed, OnDestroy } from '@angular/core';
import { CommonModule } from '@angular/common';

@Component({
  selector: 'app-player',
  imports: [CommonModule],
  templateUrl: './player.html',
  styleUrl: './player.scss',
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class Player implements OnDestroy {
  protected readonly isPlaying = signal(false);
  protected readonly isMuted = signal(false);
  protected readonly volume = signal(80);
  protected readonly progress = signal(0);
  protected readonly currentTime = signal(0);
  protected readonly duration = signal(210);
  protected readonly isShuffled = signal(false);
  protected readonly repeatMode = signal<'none' | 'all' | 'one'>('none');
  protected readonly isVisible = signal(true);
  protected readonly isExpanded = signal(false);

  protected readonly currentSong = signal({
    title: 'Sin reproducción activa',
    artist: 'KeySound',
    cover: null as string | null,
  });

  protected readonly progressPercent = computed(() =>
    this.duration() > 0 ? (this.progress() / this.duration()) * 100 : 0
  );

  protected readonly formattedCurrent = computed(() => this.formatTime(this.currentTime()));
  protected readonly formattedDuration = computed(() => this.formatTime(this.duration()));

  protected readonly repeatIcon = computed(() => {
    switch (this.repeatMode()) {
      case 'one': return 'repeat_one';
      case 'all': return 'repeat';
      default: return 'repeat';
    }
  });

  private intervalId: ReturnType<typeof setInterval> | null = null;

  toggleVisible(): void {
    this.isVisible.update(v => !v);
  }

  toggleExpanded(): void {
    this.isExpanded.update(v => !v);
  }

  togglePlay(): void {
    this.isPlaying.update(v => !v);
    if (this.isPlaying()) {
      this.startProgress();
    } else {
      this.stopProgress();
    }
  }

  toggleMute(): void {
    this.isMuted.update(v => !v);
  }

  toggleShuffle(): void {
    this.isShuffled.update(v => !v);
  }

  toggleRepeat(): void {
    this.repeatMode.update(m => {
      if (m === 'none') return 'all';
      if (m === 'all') return 'one';
      return 'none';
    });
  }

  onVolumeChange(event: Event): void {
    const input = event.target as HTMLInputElement;
    this.volume.set(Number(input.value));
    if (Number(input.value) > 0) this.isMuted.set(false);
  }

  onProgressClick(event: MouseEvent): void {
    const bar = event.currentTarget as HTMLElement;
    const rect = bar.getBoundingClientRect();
    const ratio = (event.clientX - rect.left) / rect.width;
    const newTime = Math.round(ratio * this.duration());
    this.currentTime.set(newTime);
    this.progress.set(newTime);
  }

  private startProgress(): void {
    this.intervalId = setInterval(() => {
      if (this.currentTime() < this.duration()) {
        this.currentTime.update(t => t + 1);
        this.progress.update(p => p + 1);
      } else {
        this.stopProgress();
        this.isPlaying.set(false);
      }
    }, 1000);
  }

  private stopProgress(): void {
    if (this.intervalId !== null) {
      clearInterval(this.intervalId);
      this.intervalId = null;
    }
  }

  private formatTime(seconds: number): string {
    const m = Math.floor(seconds / 60);
    const s = seconds % 60;
    return `${m}:${s.toString().padStart(2, '0')}`;
  }

  ngOnDestroy(): void {
    this.stopProgress();
  }
}
