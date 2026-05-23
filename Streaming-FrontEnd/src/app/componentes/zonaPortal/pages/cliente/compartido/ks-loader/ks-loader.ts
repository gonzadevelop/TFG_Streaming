import {
  Component,
  ChangeDetectionStrategy,
  input,
  effect,
  ElementRef,
  viewChild,
  OnDestroy,
  PLATFORM_ID,
  inject,
} from '@angular/core';
import { isPlatformBrowser } from '@angular/common';

@Component({
  selector: 'app-ks-loader',
  changeDetection: ChangeDetectionStrategy.OnPush,
  template: `
    <div
      class="ks-loader"
      role="status"
      [attr.aria-label]="mensaje()"
      [class.ks-loader--visible]="visible()"
    >
      <div class="ks-loader__stage">
        <!-- Logo sin color (base) -->
        <img
          class="ks-loader__logo-base"
          src="/img/KeySound_logo_outcolor.png"
          alt=""
          aria-hidden="true"
          width="500"
          height="500"
        />

        <!-- Logo con color (máscara que se va revelando) -->
        <div class="ks-loader__fill-wrap" #fillWrap>
          <img
            class="ks-loader__logo-color"
            src="/img/KeySound_logo_definitive.png"
            alt=""
            aria-hidden="true"
            width="500"
            height="500"
          />
        </div>
      </div>

      <p class="ks-loader__msg visually-hidden">{{ mensaje() }}</p>
    </div>
  `,
  styleUrl: './ks-loader.css',
})
export class KsLoaderComponent implements OnDestroy {
  /** Controla si el loader es visible */
  visible = input<boolean>(true);
  /** Mensaje de accesibilidad */
  mensaje = input<string>('Cargando…');
  /** Progreso de 0 a 100. Si no se pasa, se anima en bucle */
  progreso = input<number | undefined>(undefined);

  private readonly fillWrap = viewChild<ElementRef<HTMLDivElement>>('fillWrap');
  private readonly isBrowser = isPlatformBrowser(inject(PLATFORM_ID));

  private animeInstance: unknown = null;
  private currentHeight = 0;
  private animeFn: ((params: Record<string, unknown>) => unknown) | null = null;

  constructor() {
    effect(() => {
      const isVisible = this.visible();
      const prog = this.progreso();

      if (!this.isBrowser) return;

      if (isVisible) {
        this.startAnimation(prog);
      } else {
        this.stopAnimation();
      }
    });
  }

  private async startAnimation(prog?: number): Promise<void> {
    if (!this.animeFn) {
      this.animeFn = await this.loadAnime();
    }
    const anime = this.animeFn;
    if (!anime) return;

    const wrap = this.fillWrap()?.nativeElement;
    if (!wrap) return;

    this.stopAnimation();

    if (prog !== undefined) {
      const targetHeight = prog;
      this.animeInstance = anime({
        targets: wrap,
        clipPath: [`inset(${100 - this.currentHeight}% 0% 0% 0%)`, `inset(${100 - targetHeight}% 0% 0% 0%)`],
        duration: 600,
        ease: 'outQuart',
      });
      this.currentHeight = targetHeight;
    } else {
      wrap.style.clipPath = 'inset(100% 0% 0% 0%)';
      this.animeInstance = anime({
        targets: wrap,
        clipPath: ['inset(100% 0% 0% 0%)', 'inset(0% 0% 0% 0%)'],
        duration: 1800,
        ease: 'inOutSine',
        alternate: true,
        loop: true,
      });
    }
  }

  private stopAnimation(): void {
    if (this.animeInstance && typeof (this.animeInstance as { pause?: () => void }).pause === 'function') {
      (this.animeInstance as { pause: () => void }).pause();
    }
    this.animeInstance = null;
  }

  private async loadAnime(): Promise<((params: Record<string, unknown>) => unknown) | null> {
    try {
      const mod = await import('animejs');
      const fn =
        (mod as unknown as Record<string, unknown>)['animate'] ??
        (mod as unknown as Record<string, unknown>)['default'] ??
        mod;
      return typeof fn === 'function' ? (fn as (params: Record<string, unknown>) => unknown) : null;
    } catch {
      return null;
    }
  }

  ngOnDestroy(): void {
    this.stopAnimation();
  }
}



