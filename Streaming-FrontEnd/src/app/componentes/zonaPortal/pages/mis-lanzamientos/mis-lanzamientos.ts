import { ChangeDetectionStrategy, Component, signal } from '@angular/core';

interface LanzamientoItem {
  id: number;
  titulo: string;
  tipo: 'Single' | 'EP' | 'Album';
  fecha: string;
  estado: 'Publicado' | 'Borrador';
  visible: boolean;
}

@Component({
  selector: 'app-mis-lanzamientos',
  imports: [],
  templateUrl: './mis-lanzamientos.html',
  styleUrl: './mis-lanzamientos.css',
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class MisLanzamientosComponent {
  readonly statusMessage = signal<string>('');

  readonly lanzamientos = signal<LanzamientoItem[]>([
    { id: 1, titulo: 'Ciudad Neon', tipo: 'Single', fecha: '12 Mar 2026', estado: 'Publicado', visible: true },
    { id: 2, titulo: 'Luz de Medianoche', tipo: 'EP', fecha: '27 Ene 2026', estado: 'Publicado', visible: true },
    { id: 3, titulo: 'Sincronias', tipo: 'Album', fecha: '09 Abr 2026', estado: 'Borrador', visible: false },
  ]);

  toggleVisibility(id: number): void {
    this.lanzamientos.update((items) =>
      items.map((item) => (item.id === id ? { ...item, visible: !item.visible } : item))
    );
    this.statusMessage.set('Visibilidad actualizada.');
  }

  toggleState(id: number): void {
    this.lanzamientos.update((items) =>
      items.map((item) =>
        item.id === id
          ? { ...item, estado: item.estado === 'Publicado' ? 'Borrador' : 'Publicado' }
          : item
      )
    );
    this.statusMessage.set('Estado del lanzamiento actualizado.');
  }
}

