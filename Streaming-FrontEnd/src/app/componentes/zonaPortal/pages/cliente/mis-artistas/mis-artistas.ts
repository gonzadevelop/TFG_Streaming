import {
  ChangeDetectionStrategy,
  Component,
  inject,
  OnInit,
  signal,
  WritableSignal,
} from '@angular/core';
import { Router, RouterLink } from '@angular/router';
import { catchError, of } from 'rxjs';
import { ArtistaService } from '../../../../../services/artistaService';
import { IArtistaHome } from '../../../../../model/home/IArtistaHome';
import { KsLoaderComponent } from '../compartido/ks-loader/ks-loader';
import { ScrollRevealDirective } from '../../../../../shared/directives/scroll-reveal.directive';

@Component({
  selector: 'app-mis-artistas',
  imports: [RouterLink, KsLoaderComponent, ScrollRevealDirective],
  templateUrl: './mis-artistas.html',
  styleUrl: './mis-artistas.css',
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class MisArtistas implements OnInit {
  private readonly artistaService = inject(ArtistaService);
  private readonly router = inject(Router);

  protected readonly artistas: WritableSignal<IArtistaHome[]> = signal<IArtistaHome[]>([]);
  protected readonly cargando = signal<boolean>(true);
  protected readonly error = signal<string | null>(null);

  ngOnInit(): void {
    this.artistaService.getArtistasQueSigo().pipe(catchError(() => of([] as IArtistaHome[]))).subscribe({
      next: (data) => {
        this.artistas.set(data);
        this.cargando.set(false);
      },
      error: () => {
        this.error.set('No se pudieron cargar los artistas.');
        this.cargando.set(false);
      },
    });
  }

  protected navegarArtista(username: string): void {
    this.router.navigate(['/artistas', username]);
  }
}

