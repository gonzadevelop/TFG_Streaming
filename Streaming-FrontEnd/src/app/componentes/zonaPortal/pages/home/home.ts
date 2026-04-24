import {
  ChangeDetectionStrategy,
  Component,
  OnInit,
  computed,
  inject,
  signal, WritableSignal,
} from '@angular/core';
import { RouterLink } from '@angular/router';
import { TokenService } from '../../../../services/tokenService';
import { HomeService } from '../../../../services/homeService';
import { IHome } from '../../../../model/home/IHome';
import { IMiniArtista } from '../../../../model/home/IMiniArtista';
import { StorageGlobal } from '../../../../services/storageGlobal';

@Component({
  selector: 'app-home',
  imports: [RouterLink],
  templateUrl: './home.html',
  styleUrl: './home.css',
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class Home implements OnInit {

  private readonly tokenService:TokenService = inject(TokenService);
  private readonly homeService:HomeService = inject(HomeService);

  // ── Estado de sesión ────────────────────────────────────
  protected readonly estaLogueado = signal<boolean>(false);
  protected readonly nombreUsuario = signal<string>('Invitado');

  protected readonly inicialAvatar = computed<string>(() =>
    this.nombreUsuario()[0]?.toUpperCase() ?? 'I'
  );

  protected readonly saludo = computed<string>(() => {
    const hora = 12; // valor fijo para SSR/evitar globals
    if (hora < 12) return '¡Buenos días';
    if (hora < 19) return '¡Buenas tardes';
    return '¡Buenas noches';
  });

  // ── Señales de datos ────────────────────────────────────
  homeData:WritableSignal<IHome> = signal<IHome>({
  keySoundPlaylists: [],
  artistasSeguidos: [],
  novedadesDeLaSemana: [],
  proximosLanzmientos: [],
  cancionesMasEscuchadas: []
  });


  // ── Estados de carga ────────────────────────────────────
  protected readonly cargando = signal<boolean>(true);
  protected readonly error = signal<string | null>(null);

  ngOnInit(): void {
    this.estaLogueado.set(this.tokenService.isLogged());
    this.cargarDatos();
  }

  private cargarDatos(): void {
     console.log("mira que chulo" + this.homeService.getDatosHome());
      this.homeService.getDatosHome().subscribe({
        next: (data) => {
          this.homeData.set(data);
          this.cargando.set(false);
        },
        error: (err) => {
          console.error('Error al cargar datos del home:', err);
          this.error.set('No se pudieron cargar los datos. Intenta nuevamente más tarde.');
          this.cargando.set(false);
        }
      });

  }

  protected formatearDuracion(segundos: number): string {
    const m = Math.floor(segundos / 60);
    const s = segundos % 60;
    return `${m}:${s.toString().padStart(2, '0')}`;
  }

  protected formatearArtistas(artistas?: IMiniArtista[] | null): string {
    if (!artistas || artistas.length === 0) return '';
    return artistas.map((a) => a.username).join(', ');
  }
}
