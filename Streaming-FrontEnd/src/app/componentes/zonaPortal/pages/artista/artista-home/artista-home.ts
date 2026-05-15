import {
  ChangeDetectionStrategy,
  Component,
  OnInit,
  computed,
  inject,
  signal,
} from '@angular/core';
import { RouterLink } from '@angular/router';
import { ArtistaService } from '../../../../../services/artistaService';
import { IMiAlbum } from '../../../../../model/album/IMiAlbum';

@Component({
  selector: 'app-artista-home',
  imports: [RouterLink],
  templateUrl: './artista-home.html',
  styleUrl: './artista-home.css',
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class ArtistaHome implements OnInit {
  private readonly artistaService = inject(ArtistaService);

  protected readonly albums = signal<IMiAlbum[]>([]);
  protected readonly isLoading = signal<boolean>(true);
  protected readonly errorMessage = signal<string>('');

  protected readonly totalAlbums = computed(() => this.albums().length);
  protected readonly borradores = computed(() =>
    this.albums().filter(a => a.esBorrador).length
  );
  protected readonly publicados = computed(() =>
    this.albums().filter(a => !a.esBorrador).length
  );

  ngOnInit(): void {
    this.cargarAlbums();
  }

  private cargarAlbums(): void {
    this.isLoading.set(true);
    this.errorMessage.set('');

    this.artistaService.getMisAlbums().subscribe({
      next: (albums) => {
        this.albums.set(albums ?? []);
        this.isLoading.set(false);
      },
      error: () => {
        this.errorMessage.set('No se pudieron cargar tus lanzamientos.');
        this.isLoading.set(false);
      },
    });
  }
}
