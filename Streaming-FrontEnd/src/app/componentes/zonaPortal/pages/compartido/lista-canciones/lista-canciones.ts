import { ChangeDetectionStrategy, Component, inject, input } from '@angular/core';
import { MiniCancion } from '../mini-cancion/mini-cancion';
import { IPista } from '../../../../../model/pista/IPista';
import { StorageGlobal } from '../../../../../services/storageGlobal';
import IPistaCola from '../../../../../model/pista/IPistaCola';

@Component({
  selector: 'app-lista-canciones',
  imports: [MiniCancion],
  templateUrl: './lista-canciones.html',
  styleUrl: './lista-canciones.css',
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class ListaCanciones {
  readonly pistas = input.required<IPista[]>();
  readonly albumPortada = input<string>(''); // Portada del álbum para las canciones sin portada
  private readonly storage = inject(StorageGlobal);

  onReproducirPista(pista: IPista): void {
    const cola: IPistaCola[] = this.pistas().map((p, index) => ({
      ...p,
      urlPortada: p.urlPortada || this.albumPortada(),
      orden: index,
      reproduciendo: p.idPista === pista.idPista
    }));
    this.storage.SetCola(cola);
  }
}
