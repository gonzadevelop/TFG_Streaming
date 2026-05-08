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

  protected readonly colaRestante = computed(() => {
    const pistas = this.storage.cola();
    const currIdx = pistas.findIndex(p => p.reproduciendo);
    if (currIdx === -1) return pistas;
    return pistas.slice(currIdx + 1);
  });

  protected readonly pistaActual = computed(() => this.storage.GetReproduccion()());
  protected readonly tieneCola = computed(() => this.colaRestante().length > 0);

  reproducirDeCola(pistaId: number): void {
    const pistaActualInd = this.storage.cola().findIndex(p => p.idPista === pistaId);
    if (pistaActualInd === -1) return;

    // Simulate setting cola with reproduciendo adjusted
    const lista = this.storage.cola().map(p => ({
      ...p,
      reproduciendo: p.idPista === pistaId
    }));
    this.storage.cola.set(lista);

    const pista = lista[pistaActualInd];
    this.storage.Reproducir({
      idPista: pista.idPista || 0,
      titulo: pista.titulo,
      artistas: pista.artistas,
      urlPortada: pista.urlPortada,
      urlCancion: pista.urlCancion,
      duracionSegundos: pista.duracionSegundos,
      reproduciendo: true
    });
  }

  eliminar(pistaId: number): void {
    this.storage.cola.update(c => c.filter(p => p.idPista !== pistaId));
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
