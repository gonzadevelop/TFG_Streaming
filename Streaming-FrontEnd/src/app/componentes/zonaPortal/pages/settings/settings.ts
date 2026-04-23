import { ChangeDetectionStrategy, Component, DestroyRef, computed, inject, signal } from '@angular/core';
import { FormBuilder, ReactiveFormsModule, Validators } from '@angular/forms';
import { ActivatedRoute } from '@angular/router';
import { finalize } from 'rxjs';
import { takeUntilDestroyed } from '@angular/core/rxjs-interop';
import {
  ArtistProfileSettings,
  ListenerProfileSettings,
  SettingsRole,
  UpdateUserSettingsPayload,
  UserService,
  UserSettings,
} from '../../../../services/userService';

@Component({
  selector: 'app-settings',
  imports: [ReactiveFormsModule],
  templateUrl: './settings.html',
  styleUrl: './settings.css',
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class SettingsComponent {
  private readonly fb = inject(FormBuilder);
  private readonly userService = inject(UserService);
  private readonly destroyRef = inject(DestroyRef);
  private readonly route = inject(ActivatedRoute);

  readonly role = signal<SettingsRole>('listener');
  readonly isLoading = signal<boolean>(true);
  readonly isSaving = signal<boolean>(false);
  readonly errorMessage = signal<string>('');
  readonly successMessage = signal<string>('');
  readonly activeSection = signal<'account' | 'profile'>('account');

  private latestSettings: UserSettings | null = null;

  readonly settingsForm = this.fb.nonNullable.group({
    account: this.fb.nonNullable.group({
      email: ['', [Validators.required, Validators.email]],
      password: ['', [Validators.minLength(8)]],
    }),
    artistProfile: this.fb.nonNullable.group({
      bio: ['', [Validators.maxLength(300)]],
      instagram: [''],
      youtube: [''],
      tiktok: [''],
    }),
    listenerProfile: this.fb.nonNullable.group({
      favoriteGenres: [''],
      explicitContent: [false],
      releaseNotifications: [true],
    }),
  });

  readonly pageSubtitle = computed(() =>
    this.role() === 'artist'
      ? 'Gestiona tu cuenta y la informacion publica de tu proyecto artistico.'
      : 'Gestiona tu cuenta y personaliza la experiencia de escucha como oyente.'
  );

  constructor() {
    this.listenSectionFromRoute();
    this.loadSettings();
  }

  private listenSectionFromRoute(): void {
    this.route.queryParamMap
      .pipe(takeUntilDestroyed(this.destroyRef))
      .subscribe((params) => {
        const section = params.get('section');
        this.activeSection.set(section === 'profile' ? 'profile' : 'account');
      });
  }

  saveSettings(): void {
    this.successMessage.set('');
    this.errorMessage.set('');

    if (this.settingsForm.invalid) {
      this.settingsForm.markAllAsTouched();
      return;
    }

    this.isSaving.set(true);
    this.userService
      .updateSettings(this.buildPayload())
      .pipe(
        finalize(() => this.isSaving.set(false)),
        takeUntilDestroyed(this.destroyRef)
      )
      .subscribe({
        next: (settings) => {
          this.applySettings(settings);
          this.successMessage.set('Configuracion guardada correctamente.');
        },
        error: () => {
          this.errorMessage.set('No se pudo guardar la configuracion. Intentalo de nuevo.');
        },
      });
  }

  setSection(section: 'account' | 'profile'): void {
    this.activeSection.set(section);
  }

  discardChanges(): void {
    this.successMessage.set('');
    this.errorMessage.set('');

    if (this.latestSettings) {
      this.applySettings(this.latestSettings);
      return;
    }

    this.hydrateFromLocalFallback();
  }

  private loadSettings(): void {
    this.userService
      .getSettings()
      .pipe(
        finalize(() => this.isLoading.set(false)),
        takeUntilDestroyed(this.destroyRef)
      )
      .subscribe({
        next: (settings) => this.applySettings(settings),
        error: () => {
          this.hydrateFromLocalFallback();
          this.errorMessage.set('No se pudieron cargar los ajustes remotos. Se muestran datos locales.');
        },
      });
  }

  private applySettings(settings: UserSettings): void {
    this.latestSettings = settings;
    this.role.set(settings.role);

    this.settingsForm.controls.account.patchValue({
      email: settings.account.email,
      password: '',
    });

    if (settings.role === 'artist') {
      const profile = settings.profile as ArtistProfileSettings;
      this.settingsForm.controls.artistProfile.patchValue({
        bio: profile.bio ?? '',
        instagram: profile.instagram ?? '',
        youtube: profile.youtube ?? '',
        tiktok: profile.tiktok ?? '',
      });
      return;
    }

    const profile = settings.profile as ListenerProfileSettings;
    this.settingsForm.controls.listenerProfile.patchValue({
      favoriteGenres: profile.favoriteGenres ?? '',
      explicitContent: profile.explicitContent ?? false,
      releaseNotifications: profile.releaseNotifications ?? true,
    });
  }

  private buildPayload(): UpdateUserSettingsPayload {
    const account = this.settingsForm.controls.account.getRawValue();
    const payload: UpdateUserSettingsPayload = {
      account: {
        email: account.email,
      },
      role: this.role(),
      profile:
        this.role() === 'artist'
          ? this.settingsForm.controls.artistProfile.getRawValue()
          : this.settingsForm.controls.listenerProfile.getRawValue(),
    };

    if (account.password.trim()) {
      payload.account.password = account.password.trim();
    }

    return payload;
  }

  private hydrateFromLocalFallback(): void {
    const localRole = window.localStorage.getItem('role');
    this.role.set(localRole === 'artist' ? 'artist' : 'listener');

    this.settingsForm.controls.account.patchValue({
      email: window.localStorage.getItem('email') ?? '',
      password: '',
    });

    const savedBio = window.localStorage.getItem('artistBio') ?? '';
    this.settingsForm.controls.artistProfile.patchValue({
      bio: savedBio,
      instagram: '',
      youtube: '',
      tiktok: '',
    });
  }
}

