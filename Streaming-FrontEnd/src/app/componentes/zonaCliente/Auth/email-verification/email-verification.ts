import {
  ChangeDetectionStrategy,
  Component,
  inject,
  input,
  InputSignal,
  OnDestroy,
  signal,
  WritableSignal
} from '@angular/core';
import {RouterLink} from '@angular/router';
import {Subscription} from 'rxjs';
import {AuthService} from '../../../../services/authService';

@Component({
  selector: 'app-email-verification',
  imports: [RouterLink],
  templateUrl: './email-verification.html',
  styleUrl: './email-verification.css',
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class EmailVerification implements OnDestroy {
  private authService = inject(AuthService);
  private suscripcion?: Subscription;

  public email: InputSignal<string> = input<string>('');

  protected reenvioExitoso: WritableSignal<boolean> = signal(false);
  protected reenvioError: WritableSignal<string> = signal('');
  protected isLoading: WritableSignal<boolean> = signal(false);

  reenviarCorreo(): void {
    if (!this.email() || this.isLoading()) return;

    this.isLoading.set(true);
    this.reenvioExitoso.set(false);
    this.reenvioError.set('');

    this.suscripcion = this.authService.resendVerificationEmail(this.email()).subscribe({
      next: (): void => {
        this.reenvioExitoso.set(true);
        this.isLoading.set(false);
      },
      error: (): void => {
        this.reenvioError.set('No se pudo reenviar el correo. Inténtalo de nuevo.');
        this.isLoading.set(false);
      }
    });
  }

  ngOnDestroy(): void {
    this.suscripcion?.unsubscribe();
  }
}
