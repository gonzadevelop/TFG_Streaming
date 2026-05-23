import {
  ChangeDetectionStrategy,
  Component,
  inject,
  signal,
} from '@angular/core';
import {
  AbstractControl,
  FormArray,
  FormBuilder,
  FormControl,
  FormGroup,
  ReactiveFormsModule,
  Validators,
} from '@angular/forms';
import { AlbumService } from '../../../../../services/albumService';
import { ArtistaService } from '../../../../../services/artistaService';
import { CancionService } from '../../../../../services/cancionService';
import { IAlbumUpload, IAlbumUploadTrack } from '../../../../../model/album/IAlbumUpload';
import { IArtistaHome } from '../../../../../model/home/IArtistaHome';
import { ScrollRevealDirective } from '../../../../../shared/directives/scroll-reveal.directive';
import { KsLoaderComponent } from '../../cliente/compartido/ks-loader/ks-loader';
import { IExistingSong } from '../../../../../model/cancion/IExistingSong';

@Component({
  selector: 'app-artista-subir-album',
  imports: [ReactiveFormsModule, ScrollRevealDirective, KsLoaderComponent],
  templateUrl: './artista-subir-album.html',
  styleUrl: './artista-subir-album.css',
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class ArtistaSubirAlbum {
  private readonly fb = inject(FormBuilder);
  private readonly albumService = inject(AlbumService);
  private readonly artistaService = inject(ArtistaService);
  private readonly cancionService = inject(CancionService);
  private readonly searchTimers = new Map<number, ReturnType<typeof setTimeout>>();
  private readonly songSearchTimers = new Map<number, ReturnType<typeof setTimeout>>();

  protected readonly currentStep = signal<number>(1);
  protected readonly portadaFile = signal<File | null>(null);
  protected readonly portadaPreview = signal<string | null>(null);
  protected readonly isSaving = signal<boolean>(false);
  protected readonly errorMessage = signal<string>('');
  protected readonly successMessage = signal<string>('');
  protected readonly submitAttempted = signal<boolean>(false);
  protected readonly resultadosBusqueda = signal<IArtistaHome[][]>([[]]);
  protected readonly colaboradoresSeleccionados = signal<IArtistaHome[][]>([[]]);
  protected readonly queryBusqueda = signal<string[]>(['']);
  protected readonly cargandoBusqueda = signal<boolean[]>([false]);
  protected readonly resultadosCanciones = signal<IExistingSong[][]>([[]]);
  protected readonly cancionesSeleccionadas = signal<(IExistingSong | null)[]>([null]);
  protected readonly queryCanciones = signal<string[]>(['']);
  protected readonly cargandoCanciones = signal<boolean[]>([false]);

  protected readonly albumForm = this.fb.group({
    nombreAlbum: ['', [Validators.required, Validators.maxLength(100)]],
    fechaLanzamiento: ['', [Validators.required]],
    canciones: this.fb.array([this.crearCancionGroup()]),
  });

  protected get canciones(): FormArray {
    return this.albumForm.get('canciones') as FormArray;
  }

  protected agregarCancion(): void {
    this.canciones.push(this.crearCancionGroup());
    this.resultadosBusqueda.update(list => [...list, []]);
    this.colaboradoresSeleccionados.update(list => [...list, []]);
    this.queryBusqueda.update(list => [...list, '']);
    this.cargandoBusqueda.update(list => [...list, false]);
    this.resultadosCanciones.update(list => [...list, []]);
    this.cancionesSeleccionadas.update(list => [...list, null]);
    this.queryCanciones.update(list => [...list, '']);
    this.cargandoCanciones.update(list => [...list, false]);
  }

  protected quitarCancion(index: number): void {
    if (this.canciones.length > 1) {
      this.canciones.removeAt(index);
      this.resultadosBusqueda.update(list => list.filter((_, i) => i !== index));
      this.colaboradoresSeleccionados.update(list => list.filter((_, i) => i !== index));
      this.queryBusqueda.update(list => list.filter((_, i) => i !== index));
      this.cargandoBusqueda.update(list => list.filter((_, i) => i !== index));
      this.resultadosCanciones.update(list => list.filter((_, i) => i !== index));
      this.cancionesSeleccionadas.update(list => list.filter((_, i) => i !== index));
      this.queryCanciones.update(list => list.filter((_, i) => i !== index));
      this.cargandoCanciones.update(list => list.filter((_, i) => i !== index));
      this.searchTimers.forEach(timer => clearTimeout(timer));
      this.searchTimers.clear();
      this.songSearchTimers.forEach(timer => clearTimeout(timer));
      this.songSearchTimers.clear();
    }
  }

  protected onPortadaSelected(event: Event): void {
    const input = event.target as HTMLInputElement;
    const file = input.files?.[0] ?? null;
    if (!file) return;

    this.portadaFile.set(file);
    const reader = new FileReader();
    reader.onload = (e) => this.portadaPreview.set(e.target?.result as string);
    reader.readAsDataURL(file);
  }

  protected onArchivoSelected(index: number, event: Event): void {
    if (this.tieneCancionExistente(index)) return;

    const input = event.target as HTMLInputElement;
    const file = input.files?.[0] ?? null;
    const control = this.canciones.at(index).get('archivo') as FormControl<File | null>;
    control.setValue(file);
    control.markAsTouched();
  }

  protected limpiarPortada(): void {
    this.portadaFile.set(null);
    this.portadaPreview.set(null);
  }

  protected nextStep(): void {
    if (this.currentStep() === 1) {
      if (!this.validateStep1()) {
        this.errorMessage.set('Completa el nombre, la fecha y la portada antes de continuar.');
        return;
      }
      this.errorMessage.set('');
      this.currentStep.set(2);
      return;
    }

    if (this.currentStep() === 2) {
      if (!this.validateStep2()) {
        this.errorMessage.set('Revisa las pistas obligatorias antes de continuar.');
        return;
      }
      this.errorMessage.set('');
      this.currentStep.set(3);
    }
  }

  protected prevStep(): void {
    const step = this.currentStep();
    if (step > 1) this.currentStep.set(step - 1);
  }

  protected goToStep(step: number): void {
    if (step < 1 || step > 3) return;
    this.currentStep.set(step);
  }

  protected pasoActivo(step: number): boolean {
    return this.currentStep() === step;
  }

  protected submit(): void {
    if (!this.validateStep1() || !this.validateStep2()) {
      this.errorMessage.set('Completa los campos obligatorios antes de enviar.');
      return;
    }

    const nuevos = this.canciones.controls.filter(control => !control.get('idCancionExistente')?.value);
    const archivos = nuevos
      .map(control => control.get('archivo')?.value)
      .filter((file): file is File => !!file);

    if (archivos.length !== nuevos.length) {
      this.errorMessage.set('Las canciones nuevas deben incluir un archivo de audio.');
      return;
    }

    const canciones: IAlbumUploadTrack[] = this.canciones.controls.map(control => {
      const idExistente = control.get('idCancionExistente')?.value as number | null;
      return {
        titulo: (control.get('titulo')?.value ?? '').trim(),
        idArtistas: (control.get('colaboradoresIds')?.value ?? []) as number[],
        idCancionExistente: idExistente ?? undefined,
      };
    });

    const dto: IAlbumUpload = {
      nombreAlbum: (this.albumForm.value.nombreAlbum ?? '').trim(),
      fechaLanzamiento: this.normalizeFecha(this.albumForm.value.fechaLanzamiento ?? ''),
      canciones,
    };

    this.isSaving.set(true);
    this.errorMessage.set('');
    this.successMessage.set('');

    this.albumService.subirAlbum(dto, this.portadaFile()!, archivos).subscribe({
      next: () => {
        this.isSaving.set(false);
        this.successMessage.set('Álbum subido correctamente.');
        this.resetForm();
      },
      error: () => {
        this.isSaving.set(false);
        this.errorMessage.set('No se pudo subir el álbum. Inténtalo de nuevo.');
      },
    });
  }

  protected onBuscarColaborador(index: number, event: Event): void {
    if (this.tieneCancionExistente(index)) return;

    const value = (event.target as HTMLInputElement).value;
    this.queryBusqueda.update(list => list.map((q, i) => (i === index ? value : q)));

    const trimmed = value.trim();
    if (trimmed.length < 2) {
      this.setResultados(index, []);
      this.setCargando(index, false);
      return;
    }

    const existingTimer = this.searchTimers.get(index);
    if (existingTimer) clearTimeout(existingTimer);

    this.setCargando(index, true);
    const timer = setTimeout(() => {
      this.artistaService.buscarArtistas(trimmed).subscribe({
        next: (artistas) => {
          this.setResultados(index, artistas ?? []);
          this.setCargando(index, false);
        },
        error: () => {
          this.setResultados(index, []);
          this.setCargando(index, false);
        },
      });
    }, 300);

    this.searchTimers.set(index, timer);
  }

  protected seleccionarColaborador(index: number, artista: IArtistaHome): void {
    const actuales = this.colaboradoresSeleccionados()[index] ?? [];
    if (actuales.some(item => item.id === artista.id)) return;

    const updated = [...actuales, artista];
    this.colaboradoresSeleccionados.update(list => list.map((item, i) => (i === index ? updated : item)));
    this.actualizarIdsColaboradores(index, updated);
  }

  protected quitarColaborador(index: number, artistaId: number): void {
    const actuales = this.colaboradoresSeleccionados()[index] ?? [];
    const updated = actuales.filter(item => item.id !== artistaId);
    this.colaboradoresSeleccionados.update(list => list.map((item, i) => (i === index ? updated : item)));
    this.actualizarIdsColaboradores(index, updated);
  }

  protected resultadosPara(index: number): IArtistaHome[] {
    return this.resultadosBusqueda()[index] ?? [];
  }

  protected seleccionadosPara(index: number): IArtistaHome[] {
    return this.colaboradoresSeleccionados()[index] ?? [];
  }

  protected cargandoPara(index: number): boolean {
    return this.cargandoBusqueda()[index] ?? false;
  }

  protected queryPara(index: number): string {
    return this.queryBusqueda()[index] ?? '';
  }

  protected resultadosCancionesPara(index: number): IExistingSong[] {
    return this.resultadosCanciones()[index] ?? [];
  }

  protected cancionSeleccionadaPara(index: number): IExistingSong | null {
    return this.cancionesSeleccionadas()[index] ?? null;
  }

  protected cargandoCancionesPara(index: number): boolean {
    return this.cargandoCanciones()[index] ?? false;
  }

  protected queryCancionesPara(index: number): string {
    return this.queryCanciones()[index] ?? '';
  }

  protected onBuscarCancionExistente(index: number, event: Event): void {
    const value = (event.target as HTMLInputElement).value;
    this.queryCanciones.update(list => list.map((q, i) => (i === index ? value : q)));

    const trimmed = value.trim();
    if (trimmed.length < 2) {
      this.setResultadosCanciones(index, []);
      this.setCargandoCanciones(index, false);
      return;
    }

    const existingTimer = this.songSearchTimers.get(index);
    if (existingTimer) clearTimeout(existingTimer);

    this.setCargandoCanciones(index, true);
    const timer = setTimeout(() => {
      this.cancionService.buscarMisCanciones(trimmed).subscribe({
        next: (canciones) => {
          this.setResultadosCanciones(index, canciones ?? []);
          this.setCargandoCanciones(index, false);
        },
        error: () => {
          this.setResultadosCanciones(index, []);
          this.setCargandoCanciones(index, false);
        },
      });
    }, 300);

    this.songSearchTimers.set(index, timer);
  }

  protected seleccionarCancionExistente(index: number, cancion: IExistingSong): void {
    this.cancionesSeleccionadas.update(list => list.map((item, i) => (i === index ? cancion : item)));
    this.queryCanciones.update(list => list.map((q, i) => (i === index ? cancion.titulo : q)));
    this.setResultadosCanciones(index, []);

    const control = this.canciones.at(index);
    control.get('idCancionExistente')?.setValue(cancion.idCancion);
    control.get('titulo')?.setValue(cancion.titulo);
    control.get('colaboradoresIds')?.setValue([]);
    control.get('archivo')?.setValue(null);
    this.colaboradoresSeleccionados.update(list => list.map((item, i) => (i === index ? [] : item)));
    this.setArchivoRequerido(index, false);
  }

  protected limpiarCancionExistente(index: number): void {
    this.cancionesSeleccionadas.update(list => list.map((item, i) => (i === index ? null : item)));
    this.queryCanciones.update(list => list.map((q, i) => (i === index ? '' : q)));
    this.setResultadosCanciones(index, []);

    const control = this.canciones.at(index);
    control.get('idCancionExistente')?.setValue(null);
    control.get('titulo')?.setValue('');
    this.setArchivoRequerido(index, true);
  }

  protected tieneCancionExistente(index: number): boolean {
    return !!this.canciones.at(index).get('idCancionExistente')?.value;
  }

  protected tituloCancion(index: number): string {
    return (this.canciones.at(index).get('titulo')?.value ?? '').toString();
  }

  protected colaboradoresPreview(index: number): IArtistaHome[] {
    return this.colaboradoresSeleccionados()[index] ?? [];
  }

  protected cancionExistentePreview(index: number): IExistingSong | null {
    return this.cancionesSeleccionadas()[index] ?? null;
  }

  private setResultados(index: number, resultados: IArtistaHome[]): void {
    this.resultadosBusqueda.update(list => list.map((item, i) => (i === index ? resultados : item)));
  }

  private setCargando(index: number, value: boolean): void {
    this.cargandoBusqueda.update(list => list.map((item, i) => (i === index ? value : item)));
  }

  private setResultadosCanciones(index: number, resultados: IExistingSong[]): void {
    this.resultadosCanciones.update(list => list.map((item, i) => (i === index ? resultados : item)));
  }

  private setCargandoCanciones(index: number, value: boolean): void {
    this.cargandoCanciones.update(list => list.map((item, i) => (i === index ? value : item)));
  }

  private actualizarIdsColaboradores(index: number, colaboradores: IArtistaHome[]): void {
    const ids = colaboradores.map(item => item.id);
    const control = this.canciones.at(index).get('colaboradoresIds') as FormControl<number[]>;
    control.setValue(ids);
    control.markAsDirty();
  }

  private resetForm(): void {
    this.albumForm.reset();
    this.submitAttempted.set(false);
    this.currentStep.set(1);
    this.canciones.clear();
    this.canciones.push(this.crearCancionGroup());
    this.resultadosBusqueda.set([[]]);
    this.colaboradoresSeleccionados.set([[]]);
    this.queryBusqueda.set(['']);
    this.cargandoBusqueda.set([false]);
    this.resultadosCanciones.set([[]]);
    this.cancionesSeleccionadas.set([null]);
    this.queryCanciones.set(['']);
    this.cargandoCanciones.set([false]);
    this.searchTimers.forEach(timer => clearTimeout(timer));
    this.searchTimers.clear();
    this.songSearchTimers.forEach(timer => clearTimeout(timer));
    this.songSearchTimers.clear();
    this.limpiarPortada();
  }

  private crearCancionGroup(): FormGroup {
    return this.fb.group({
      titulo: ['', [Validators.required, Validators.maxLength(100)]],
      colaboradoresIds: new FormControl<number[]>([]),
      idCancionExistente: new FormControl<number | null>(null),
      archivo: new FormControl<File | null>(null, [Validators.required]),
    });
  }

  private setArchivoRequerido(index: number, requerido: boolean): void {
    const control = this.canciones.at(index).get('archivo') as FormControl<File | null>;
    control.clearValidators();
    if (requerido) {
      control.addValidators([Validators.required]);
    }
    control.updateValueAndValidity({ emitEvent: false });
  }

  private normalizeFecha(valor: string): string {
    if (!valor) return '';
    return valor.length === 16 ? `${valor}:00` : valor;
  }

  private validateStep1(): boolean {
    const nombre = this.albumForm.get('nombreAlbum');
    const fecha = this.albumForm.get('fechaLanzamiento');
    this.submitAttempted.set(true);
    nombre?.markAsTouched();
    fecha?.markAsTouched();

    return !!nombre && nombre.valid && !!fecha && fecha.valid && !!this.portadaFile();
  }

  private validateStep2(): boolean {
    this.submitAttempted.set(true);

    if (!this.canciones.length) return false;

    let valid = true;
    this.canciones.controls.forEach((control, index) => {
      const titulo = control.get('titulo');
      const archivo = control.get('archivo');

      titulo?.markAsTouched();
      archivo?.markAsTouched();

      if (!this.tieneCancionExistente(index)) {
        if (!titulo || titulo.invalid) valid = false;
        if (!archivo || archivo.invalid) valid = false;
      }
    });

    const nuevos = this.canciones.controls.filter(control => !control.get('idCancionExistente')?.value);
    const archivos = nuevos
      .map(control => control.get('archivo')?.value)
      .filter((file): file is File => !!file);

    if (archivos.length !== nuevos.length) valid = false;

    return valid;
  }

  protected mostrarErrorCampo(control: AbstractControl | null): boolean {
    if (!control) return false;
    return control.invalid && (control.touched || this.submitAttempted());
  }

  protected faltaPortada(): boolean {
    return !this.portadaFile() && this.submitAttempted();
  }
}
