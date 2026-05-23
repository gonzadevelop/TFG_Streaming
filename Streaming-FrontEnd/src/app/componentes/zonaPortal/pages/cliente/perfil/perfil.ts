import { ChangeDetectionStrategy, Component, inject } from '@angular/core';
import { UserService } from '../../../../../services/userService';
import { SidebarService } from '../../../../../services/SidebarService';
import { TokenService } from '../../../../../services/tokenService';
import { IResponseUsuario } from '../../../../../model/IResponseUsuario';
import { FormControl, FormGroup, ReactiveFormsModule, Validators } from '@angular/forms';
import { RouterLink } from '@angular/router';
import { signal, computed } from '@angular/core';
import { ScrollRevealDirective } from '../../../../../shared/directives/scroll-reveal.directive';
import { KsLoaderComponent } from '../compartido/ks-loader/ks-loader';

@Component({
  selector: 'app-perfil',
  imports: [ReactiveFormsModule, RouterLink, ScrollRevealDirective, KsLoaderComponent],
  templateUrl: './perfil.html',
  styleUrl: './perfil.css',
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class Perfil {
  private readonly userService = inject(UserService);
  private readonly sidebarService = inject(SidebarService);
  private readonly tokenService = inject(TokenService);

  protected readonly rol = computed<'artista' | 'oyente'>(() =>
    this.tokenService.getPrimaryRole() === 'ROLE_ARTISTA' ? 'artista' : 'oyente'
  );

  protected readonly perfil = signal<IResponseUsuario | null>(null);
  protected readonly isLoading = signal<boolean>(true);
  protected readonly isSaving = signal<boolean>(false);
  protected readonly isUploadingAvatar = signal<boolean>(false);
  protected readonly errorMessage = signal<string>('');
  protected readonly successMessage = signal<string>('');
  protected readonly modoEdicion = signal<boolean>(false);
  protected readonly avatarPreview = signal<string | null>(null);
  protected readonly isDragOver = signal<boolean>(false);

  private successTimer: ReturnType<typeof setTimeout> | null = null;
  private errorTimer: ReturnType<typeof setTimeout> | null = null;

  private showSuccess(msg: string): void {
    if (this.successTimer) clearTimeout(this.successTimer);
    this.successMessage.set(msg);
    this.successTimer = setTimeout(() => this.successMessage.set(''), 5000);
  }

  private showError(msg: string): void {
    if (this.errorTimer) clearTimeout(this.errorTimer);
    this.errorMessage.set(msg);
    this.errorTimer = setTimeout(() => this.errorMessage.set(''), 5000);
  }

  protected cerrarSuccess(): void {
    if (this.successTimer) clearTimeout(this.successTimer);
    this.successMessage.set('');
  }

  protected cerrarError(): void {
    if (this.errorTimer) clearTimeout(this.errorTimer);
    this.errorMessage.set('');
  }

  ngOnDestroy(): void {
    if (this.successTimer) clearTimeout(this.successTimer);
    if (this.errorTimer) clearTimeout(this.errorTimer);
  }

  protected readonly inicialAvatar = computed<string>(() => {
    const preview = this.avatarPreview();
    if (preview) return '';
    const url = this.perfil()?.urlAvatar;
    if (url && !url.includes('ui-avatars')) return '';
    return this.perfil()?.username?.[0]?.toUpperCase() ?? 'U';
  });

  protected readonly avatarSrc = computed<string | null>(() => {
    const preview = this.avatarPreview();
    if (preview) return preview;
    const url = this.perfil()?.urlAvatar;
    if (url && !url.includes('ui-avatars')) return url;
    return null;
  });

  protected readonly perfilForm = new FormGroup({
    email: new FormControl('', [Validators.required, Validators.email]),
    biografia: new FormControl('', [Validators.maxLength(300)]),
  });

  ngOnInit(): void {
    this.cargarPerfil();
  }

  private cargarPerfil(): void {
    this.isLoading.set(true);
    this.sidebarService.getUsername().subscribe({
      next: (username) => {
        this.userService.getPerfilUsuario(username).subscribe({
          next: (data) => {
            this.perfil.set(data);
            this.perfilForm.patchValue({
              email: data.email,
              biografia: data.biografia ?? '',
            });
            this.isLoading.set(false);
          },
          error: () => {
            this.showError('No se pudo cargar el perfil.');
            this.isLoading.set(false);
          },
        });
      },
      error: () => {
        this.showError('No se pudo obtener el nombre de usuario.');
        this.isLoading.set(false);
      },
    });
  }

  protected activarEdicion(): void {
    this.modoEdicion.set(true);
    this.successMessage.set('');
    this.errorMessage.set('');
  }

  protected cancelarEdicion(): void {
    const p = this.perfil();
    if (p) {
      this.perfilForm.patchValue({ email: p.email, biografia: p.biografia ?? '' });
    }
    this.modoEdicion.set(false);
    this.errorMessage.set('');
  }

  protected guardarCambios(): void {
    if (this.perfilForm.invalid) return;
    this.isSaving.set(true);
    this.errorMessage.set('');

    const { email, biografia } = this.perfilForm.value;
    this.userService.actualizarPerfil({ email: email ?? '', biografia: biografia ?? '' }).subscribe({
      next: (updated) => {
        this.perfil.set(updated);
        this.modoEdicion.set(false);
        this.isSaving.set(false);
        this.showSuccess('¡Perfil actualizado correctamente!');
      },
      error: () => {
        this.showError('Error al guardar los cambios. Inténtalo de nuevo.');
        this.isSaving.set(false);
      },
    });
  }

  // ── Drag & Drop Avatar ──────────────────────────────────────────────────

  protected onDragOver(event: DragEvent): void {
    event.preventDefault();
    this.isDragOver.set(true);
  }

  protected onDragLeave(): void {
    this.isDragOver.set(false);
  }

  protected onDrop(event: DragEvent): void {
    event.preventDefault();
    this.isDragOver.set(false);
    const file = event.dataTransfer?.files?.[0];
    if (file) this.procesarArchivoAvatar(file);
  }

  protected onFileSelected(event: Event): void {
    const input = event.target as HTMLInputElement;
    const file = input.files?.[0];
    if (file) this.procesarArchivoAvatar(file);
  }

  private procesarArchivoAvatar(file: File): void {
    if (!file.type.startsWith('image/')) {
      this.showError('Solo se permiten archivos de imagen.');
      return;
    }
    if (file.size > 5 * 1024 * 1024) {
      this.showError('La imagen no puede superar 5 MB.');
      return;
    }

    // Mostrar preview inmediato
    const reader = new FileReader();
    reader.onload = (e) => {
      this.avatarPreview.set(e.target?.result as string);
    };
    reader.readAsDataURL(file);

    // Subir al servidor
    this.isUploadingAvatar.set(true);
    this.errorMessage.set('');
    this.userService.actualizarAvatar(file).subscribe({
      next: (urlAvatar) => {
        const p = this.perfil();
        if (p) this.perfil.set({ ...p, urlAvatar });
        this.sidebarService.avatarUrl.set(urlAvatar);
        this.isUploadingAvatar.set(false);
        this.showSuccess('¡Foto de perfil actualizada!');
      },
      error: () => {
        this.avatarPreview.set(null);
        this.showError('Error al subir la imagen. Inténtalo de nuevo.');
        this.isUploadingAvatar.set(false);
      },
    });
  }
}
