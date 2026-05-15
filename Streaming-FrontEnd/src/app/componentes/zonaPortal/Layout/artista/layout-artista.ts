import { ChangeDetectionStrategy, Component, signal } from '@angular/core';
import { RouterOutlet } from '@angular/router';
import { SidebarArtista } from './sidebar-artista';

@Component({
  selector: 'app-layout-artista',
  imports: [RouterOutlet, SidebarArtista],
  templateUrl: './layout-artista.html',
  styleUrl: './layout-artista.css',
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class LayoutArtista {
  readonly sidebarOpen = signal(true);
}
