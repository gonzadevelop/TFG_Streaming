import { ChangeDetectionStrategy, Component, inject, input } from '@angular/core';
import { MiniCancion } from '../mini-cancion/mini-cancion';
import { IPista } from '../../../../../../model/pista/IPista';
import { StorageGlobal } from '../../../../../../services/storageGlobal';
import IPistaCola from '../../../../../../model/pista/IPistaCola';

@Component({
  selector: 'app-lista-canciones',
  imports: [MiniCancion],
  templateUrl: './lista-canciones.html',
  styleUrl: './lista-canciones.css',
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class ListaCanciones {
  readonly pistas = input.required<IPista[]>();
  readonly portadaFallback = input<string>('');
  private readonly storage = inject(StorageGlobal);

  onReproducirPista(pista: IPista): void {
    const pistasList = this.pistas();
    const fallback = this.portadaFallback();
    const clickedIndex = pistasList.findIndex(p => p === pista || (p.urlCancion && p.urlCancion === pista.urlCancion));
    const resolvedIndex = clickedIndex !== -1 ? clickedIndex : 0;

    const cola: IPistaCola[] = pistasList.map((p, index) => ({
      ...p,
      urlPortada: p.urlPortada || fallback,
      orden: index,
      reproduciendo: index === resolvedIndex
    }));
    this.storage.SetCola(cola);
  }
}
