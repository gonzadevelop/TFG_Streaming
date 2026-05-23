import { Directive, ElementRef, inject, input, OnDestroy, OnInit } from '@angular/core';
import { animate, stagger as animeStagger, createTimeline } from 'animejs';

@Directive({
  selector: '[scrollReveal]',
})
export class ScrollRevealDirective implements OnInit, OnDestroy {
  private readonly el = inject(ElementRef<HTMLElement>);
  private observer?: IntersectionObserver;

  readonly delay = input<number>(0);
  readonly duration = input<number>(550);
  readonly stagger = input<number>(0);
  readonly staggerSelector = input<string>('');

  ngOnInit(): void {
    const host = this.el.nativeElement;
    const useStagger = this.stagger() > 0;

    if (useStagger) {
      const selector = this.staggerSelector();
      const children: HTMLElement[] = selector
        ? Array.from(host.querySelectorAll(selector)) as HTMLElement[]
        : Array.from(host.children as HTMLCollectionOf<HTMLElement>);

      children.forEach((child) => {
        child.style.opacity = '0';
        child.style.transform = 'translateY(28px)';
      });
    } else {
      host.style.opacity = '0';
      host.style.transform = 'translateY(28px)';
    }

    this.observer = new IntersectionObserver(
      (entries) => {
        entries.forEach((entry) => {
          if (!entry.isIntersecting) return;

          if (useStagger) {
            const selector = this.staggerSelector();
            const children = selector
              ? Array.from(host.querySelectorAll(selector)) as HTMLElement[]
              : Array.from(host.children as HTMLCollectionOf<HTMLElement>);

            animate(children, {
              opacity: [0, 1],
              translateY: [28, 0],
              duration: this.duration(),
              delay: animeStagger(this.stagger(), { start: this.delay() }),
              ease: 'easeOutCubic',
            });
          } else {
            animate(host, {
              opacity: [0, 1],
              translateY: [28, 0],
              duration: this.duration(),
              delay: this.delay(),
              ease: 'easeOutCubic',
            });
          }

          this.observer?.unobserve(host);
        });
      },
      { threshold: 0.08 }
    );

    this.observer.observe(host);
  }

  ngOnDestroy(): void {
    this.observer?.disconnect();
  }
}





