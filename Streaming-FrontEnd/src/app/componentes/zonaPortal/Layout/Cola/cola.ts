import { ChangeDetectionStrategy, Component, computed, inject, input, output, signal } from '@angular/core';
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

  protected readonly dragOverIndex = signal<number | null>(null);
  private dragFromIndex: number | null = null;

  protected readonly colaRestante = computed(() => {
    const pistas = this.storage.cola();
    const currIdx = pistas.findIndex(p => p.reproduciendo);
    if (currIdx === -1) return pistas;
    return pistas.slice(currIdx + 1);
  });

  protected readonly pistaActual = computed(() => this.storage.GetReproduccion()());
  protected readonly tieneCola = computed(() => this.colaRestante().length > 0);

  // ── Drag & Drop ──────────────────────────────────────────────────────────

  protected onDragStart(event: DragEvent, index: number): void {
    this.dragFromIndex = index;
    event.dataTransfer?.setData('text/plain', String(index));
    if (event.dataTransfer) event.dataTransfer.effectAllowed = 'move';
  }

  protected onDragOver(event: DragEvent, index: number): void {
    event.preventDefault();
    if (event.dataTransfer) event.dataTransfer.dropEffect = 'move';
    this.dragOverIndex.set(index);
  }

  protected onDragLeave(): void {
    this.dragOverIndex.set(null);
  }

  protected onDrop(event: DragEvent, toRelativeIndex: number): void {
    event.preventDefault();
    this.dragOverIndex.set(null);

    const fromRelative = this.dragFromIndex;
    this.dragFromIndex = null;
    if (fromRelative === null || fromRelative === toRelativeIndex) return;

    const cola = this.storage.cola();
    const currIdx = cola.findIndex(p => p.reproduciendo);
    const offset = currIdx === -1 ? 0 : currIdx + 1;

    const fromAbs = fromRelative + offset;
    const toAbs   = toRelativeIndex + offset;

    const nueva = [...cola];
    const [moved] = nueva.splice(fromAbs, 1);
    nueva.splice(toAbs, 0, moved);

    this.storage.cola.set(nueva);
    this.storage.colaOriginal.set(nueva);
  }

  protected onDragEnd(): void {
    this.dragOverIndex.set(null);
    this.dragFromIndex = null;
  }

  // ── Reproducción / eliminación ───────────────────────────────────────────

  reproducirDeCola(pistaId: number): void {
    const pistaActualInd = this.storage.cola().findIndex(p => p.idPista === pistaId);
    if (pistaActualInd === -1) return;

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
