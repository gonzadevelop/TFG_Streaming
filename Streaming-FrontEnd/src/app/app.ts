import { Component, signal } from '@angular/core';
import { RouterOutlet } from '@angular/router';
import { MusicBackgroundComponent } from './shared/music-background/music-background';

@Component({
  selector: 'app-root',
  imports: [RouterOutlet, MusicBackgroundComponent],
  templateUrl: './app.html',
  styleUrl: './app.css',
})
export class App {
  protected readonly title = signal('Streaming-FrontEnd');
}
