import { Injectable, signal } from '@angular/core';

@Injectable({ providedIn: 'root' })
export class ContextMenuService {
  /** ID único del menú contextual activo. null = ninguno abierto */
  readonly activeMenuId = signal<string | null>(null);

  abrir(id: string): void {
    this.activeMenuId.set(id);
  }

  cerrar(): void {
    this.activeMenuId.set(null);
  }
}

