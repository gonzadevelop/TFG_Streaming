import { ChangeDetectionStrategy, Component } from '@angular/core';

interface FooterLink {
  href: string;
  label: string;
}

@Component({
  selector: 'app-footer',
  imports: [],
  templateUrl: './footer.html',
  styleUrl: './footer.css',
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class Footer {
  readonly year = new Date().getFullYear();

  readonly links: FooterLink[] = [
    { href: '#', label: 'Privacidad' },
    { href: '#', label: 'Terminos' },
    { href: '#', label: 'Ayuda' },
  ];
}

