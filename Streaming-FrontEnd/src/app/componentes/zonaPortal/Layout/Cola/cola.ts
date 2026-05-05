import { ChangeDetectionStrategy, Component, computed, inject, input, output } from '@angular/core';
import { StorageGlobal } from '../../../../services/storageGlobal';

@Component({
  selector: 'app-cola',
  imports: [],
  templateUrl: './cola.html',
  styleUrl: './cola.css',
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class Cola {
  private readonly storage = inject(StorageGlobal);

  readonly isOpen = input<boolean>(false);
  readonly cerrar = output<void>();

  protected readonly cola = this.storage.cola;
  protected readonly pistaActual = computed(() => this.storage.GetReproduccion()());
  protected readonly tieneCola = computed(() => this.cola().length > 0);

  reproducirDeCola(index: number): void {
    const pistas = this.cola();
    const pista = pistas[index];
    if (!pista) return;
    // Eliminar todas las pistas anteriores incluyendo la seleccionada
    this.storage.cola.update(c => c.filter((_, i) => i > index));
    this.storage.Reproducir(pista);
  }

  eliminar(index: number): void {
    this.storage.EliminarDeCola(index);
  }

  vaciarCola(): void {
    this.storage.VaciarCola();
  }

  onCerrar(): void {
    this.cerrar.emit();
  }

  formatTime(seconds: number): string {
    const m = Math.floor(seconds / 60);
    const s = Math.floor(seconds % 60);
    return `${m}:${s.toString().padStart(2, '0')}`;
  }
}

