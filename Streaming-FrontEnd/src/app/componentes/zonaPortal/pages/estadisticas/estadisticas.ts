import { ChangeDetectionStrategy, Component, computed, signal } from '@angular/core';

type Rango = '7d' | '30d' | '12m';

interface Kpi {
  label: string;
  valor: string;
  detalle: string;
}

@Component({
  selector: 'app-estadisticas',
  imports: [],
  templateUrl: './estadisticas.html',
  styleUrl: './estadisticas.css',
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class EstadisticasComponent {
  readonly rango = signal<Rango>('30d');

  readonly kpis = computed<Kpi[]>(() => {
    const value = this.rango();
    if (value === '7d') {
      return [
        { label: 'Reproducciones', valor: '26.4K', detalle: '+8.1%' },
        { label: 'Oyentes unicos', valor: '8.9K', detalle: '+3.4%' },
        { label: 'Guardados', valor: '1.2K', detalle: '+5.0%' },
      ];
    }
    if (value === '12m') {
      return [
        { label: 'Reproducciones', valor: '3.2M', detalle: '+22.7%' },
        { label: 'Oyentes unicos', valor: '410K', detalle: '+17.2%' },
        { label: 'Guardados', valor: '48.3K', detalle: '+14.9%' },
      ];
    }
    return [
      { label: 'Reproducciones', valor: '315K', detalle: '+12.2%' },
      { label: 'Oyentes unicos', valor: '42.1K', detalle: '+6.8%' },
      { label: 'Guardados', valor: '6.7K', detalle: '+9.3%' },
    ];
  });

  setRango(value: Rango): void {
    this.rango.set(value);
  }
}

