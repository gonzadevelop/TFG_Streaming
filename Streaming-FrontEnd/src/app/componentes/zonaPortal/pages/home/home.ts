import { ChangeDetectionStrategy, Component, signal, computed } from '@angular/core';
import { RouterLink } from '@angular/router';

interface PlaylistCard {
  id: number;
  nombre: string;
  descripcion: string;
  emoji: string;
  color: string;
  canciones: number;
}

interface PodcastCard {
  id: number;
  titulo: string;
  autor: string;
  categoria: string;
  emoji: string;
  episodios: number;
  color: string;
}

interface ArtistaConcierto {
  id: number;
  nombre: string;
  fecha: string;
  ciudad: string;
  emoji: string;
  color: string;
}

interface NovedadCard {
  id: number;
  titulo: string;
  artista: string;
  tipo: 'Álbum' | 'Sencillo' | 'EP';
  emoji: string;
  color: string;
}

@Component({
  selector: 'app-home',
  imports: [RouterLink],
  templateUrl: './home.html',
  styleUrl: './home.css',
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class Home {
  protected readonly nombreUsuario = signal<string>('Usuario');

  protected readonly inicialAvatar = computed<string>(() =>
    this.nombreUsuario()[0]?.toUpperCase() ?? 'U'
  );

  protected readonly saludo = computed<string>(() => {
    const hora = 12; // valor fijo para evitar globals
    if (hora < 12) return '¡Buenos días';
    if (hora < 19) return '¡Buenas tardes';
    return '¡Buenas noches';
  });

  protected readonly playlists = signal<PlaylistCard[]>([
    { id: 1, nombre: 'Top 30 España', descripcion: 'Las 30 canciones más reproducidas en España ahora mismo', emoji: '🇪🇸', color: '#0b75c0', canciones: 30 },
    { id: 2, nombre: 'Virales del Mundo', descripcion: 'Los temas más virales del momento en todo el planeta', emoji: '🌍', color: '#1f9ed7', canciones: 30 },
    { id: 3, nombre: 'Éxitos del Verano', descripcion: 'Los hitazos del verano que no puedes dejar de escuchar', emoji: '☀️', color: '#33708a', canciones: 25 },
    { id: 4, nombre: 'Chill & Relax', descripcion: 'Música para relajarte y desconectar del mundo', emoji: '🌊', color: '#78bfda', canciones: 40 },
    { id: 5, nombre: 'Workout Boost', descripcion: 'La energía que necesitas para tu entreno', emoji: '💪', color: '#0b75c0', canciones: 35 },
    { id: 6, nombre: 'Novedades Semana', descripcion: 'Los lanzamientos más frescos de esta semana', emoji: '🆕', color: '#1f9ed7', canciones: 20 },
  ]);

  protected readonly podcasts = signal<PodcastCard[]>([
    { id: 1, titulo: 'Entiende Tu Mente', autor: 'Molo Cebrián & Luis Muiño', categoria: 'Psicología', emoji: '🧠', episodios: 320, color: '#0b75c0' },
    { id: 2, titulo: 'Cracks Podcast', autor: 'Oso Trava', categoria: 'Negocios', emoji: '🚀', episodios: 180, color: '#1f9ed7' },
    { id: 3, titulo: 'Nadie Sabe Nada', autor: 'Andreu Buenafuente & Berto Romero', categoria: 'Humor', emoji: '😂', episodios: 200, color: '#33708a' },
    { id: 4, titulo: 'Hardcore History', autor: 'Dan Carlin', categoria: 'Historia', emoji: '📜', episodios: 70, color: '#78bfda' },
  ]);

  protected readonly conciertos = signal<ArtistaConcierto[]>([
    { id: 1, nombre: 'Bad Bunny', fecha: '15 Jun 2026', ciudad: 'Madrid, RCDE Stadium', emoji: '🐰', color: '#0b75c0' },
    { id: 2, nombre: 'Rosalía', fecha: '22 Jun 2026', ciudad: 'Barcelona, Palau Sant Jordi', emoji: '🌹', color: '#1f9ed7' },
    { id: 3, nombre: 'Coldplay', fecha: '4 Jul 2026', ciudad: 'Madrid, Estadio Metropolitano', emoji: '🌈', color: '#33708a' },
    { id: 4, nombre: 'C. Tangana', fecha: '18 Jul 2026', ciudad: 'Sevilla, Estadio de La Cartuja', emoji: '🎸', color: '#78bfda' },
  ]);

  protected readonly novedades = signal<NovedadCard[]>([
    { id: 1, titulo: 'La Última Vez', artista: 'Bad Gyal', tipo: 'Sencillo', emoji: '🎵', color: '#0b75c0' },
    { id: 2, titulo: 'Origen', artista: 'Morat', tipo: 'Álbum', emoji: '💿', color: '#1f9ed7' },
    { id: 3, titulo: 'Neon Dreams', artista: 'The Weeknd', tipo: 'EP', emoji: '🌟', color: '#33708a' },
    { id: 4, titulo: 'Ritmo Latino', artista: 'J Balvin', tipo: 'Sencillo', emoji: '🎶', color: '#78bfda' },
  ]);
}
