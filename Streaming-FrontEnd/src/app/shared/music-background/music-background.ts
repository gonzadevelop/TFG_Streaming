import {
  Component,
  ChangeDetectionStrategy,
  AfterViewInit,
  OnDestroy,
  ElementRef,
  viewChild,
  PLATFORM_ID,
  inject,
} from '@angular/core';
import { isPlatformBrowser } from '@angular/common';

interface MusicNote {
  x: number;
  y: number;
  char: string;
  size: number;
  opacity: number;
  rotation: number;
  duration: number;
  delay: number;
  el?: HTMLElement;
}

@Component({
  selector: 'app-music-background',
  changeDetection: ChangeDetectionStrategy.OnPush,
  template: `
    <div class="music-bg" #container aria-hidden="true">
      <canvas #canvas class="music-bg__canvas"></canvas>
    </div>
  `,
  styles: [`
    :host {
      display: block;
      position: fixed;
      inset: 0;
      z-index: 0;
      pointer-events: none;
    }

    .music-bg {
      width: 100%;
      height: 100%;
      background: linear-gradient(135deg, #020b18 0%, #041428 40%, #061e3a 70%, #030d1f 100%);
      overflow: hidden;
    }

    .music-bg__canvas {
      position: absolute;
      inset: 0;
      width: 100%;
      height: 100%;
    }
  `],
})
export class MusicBackgroundComponent implements AfterViewInit, OnDestroy {
  private readonly platformId = inject(PLATFORM_ID);
  private readonly containerRef = viewChild.required<ElementRef<HTMLDivElement>>('container');
  private readonly canvasRef = viewChild.required<ElementRef<HTMLCanvasElement>>('canvas');

  private animationId = 0;
  private notes: MusicNoteParticle[] = [];
  private ctx!: CanvasRenderingContext2D;
  private canvas!: HTMLCanvasElement;
  private resizeObserver?: ResizeObserver;

  private readonly NOTE_CHARS = ['♩', '♪', '♫', '♬', '𝄞', '𝄢'];
  private readonly NOTE_COUNT = 30;

  ngAfterViewInit(): void {
    if (!isPlatformBrowser(this.platformId)) return;

    this.canvas = this.canvasRef().nativeElement;
    const context = this.canvas.getContext('2d');
    if (!context) return;
    this.ctx = context;

    this.resizeCanvas();
    this.initNotes();
    this.animate();

    this.resizeObserver = new ResizeObserver(() => {
      this.resizeCanvas();
    });
    this.resizeObserver.observe(document.documentElement);
  }

  ngOnDestroy(): void {
    if (this.animationId) {
      cancelAnimationFrame(this.animationId);
    }
    this.resizeObserver?.disconnect();
  }

  private resizeCanvas(): void {
    this.canvas.width = window.innerWidth;
    this.canvas.height = window.innerHeight;
  }

  private initNotes(): void {
    this.notes = [];
    for (let i = 0; i < this.NOTE_COUNT; i++) {
      this.notes.push(this.createNote(true));
    }
  }

  private createNote(randomY = false): MusicNoteParticle {
    const w = this.canvas.width;
    const h = this.canvas.height;
    return {
      x: Math.random() * w,
      y: randomY ? Math.random() * h : h + 40,
      char: this.NOTE_CHARS[Math.floor(Math.random() * this.NOTE_CHARS.length)],
      size: 14 + Math.random() * 22,
      opacity: 0.08 + Math.random() * 0.18,
      rotation: (Math.random() - 0.5) * 40,
      rotationSpeed: (Math.random() - 0.5) * 0.4,
      speedY: 0.3 + Math.random() * 0.7,
      speedX: (Math.random() - 0.5) * 0.4,
      pulse: Math.random() * Math.PI * 2,
      pulseSpeed: 0.01 + Math.random() * 0.02,
    };
  }

  private animate(): void {
    const ctx = this.ctx;
    const w = this.canvas.width;
    const h = this.canvas.height;

    ctx.clearRect(0, 0, w, h);

    for (let i = 0; i < this.notes.length; i++) {
      const note = this.notes[i];

      note.y -= note.speedY;
      note.x += note.speedX;
      note.rotation += note.rotationSpeed;
      note.pulse += note.pulseSpeed;

      const pulseOpacity = note.opacity + Math.sin(note.pulse) * 0.04;

      ctx.save();
      ctx.translate(note.x, note.y);
      ctx.rotate((note.rotation * Math.PI) / 180);
      ctx.globalAlpha = Math.max(0, pulseOpacity);
      ctx.fillStyle = '#4a9eff';
      ctx.font = `${note.size}px serif`;
      ctx.textAlign = 'center';
      ctx.textBaseline = 'middle';

      // Pequeño glow azul
      ctx.shadowColor = '#1e6fbf';
      ctx.shadowBlur = 8;
      ctx.fillText(note.char, 0, 0);
      ctx.restore();

      // Reiniciar si sale por arriba o los lados
      if (note.y < -40 || note.x < -40 || note.x > w + 40) {
        this.notes[i] = this.createNote(false);
        this.notes[i].x = Math.random() * w;
      }
    }

    this.animationId = requestAnimationFrame(() => this.animate());
  }
}

interface MusicNoteParticle {
  x: number;
  y: number;
  char: string;
  size: number;
  opacity: number;
  rotation: number;
  rotationSpeed: number;
  speedY: number;
  speedX: number;
  pulse: number;
  pulseSpeed: number;
}

