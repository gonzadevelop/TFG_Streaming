package tfg.KeySound.controllers;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import tfg.KeySound.services.external.FirebaseService;
import tfg.KeySound.utils.AudioUtils;

@RestController
@RequestMapping("/firebase")
@RequiredArgsConstructor
public class FirebaseController {

    private final FirebaseService firebaseService;

    @PostMapping("/upload")
    public ResponseEntity<Void> uploadFile(
            @RequestParam("file") MultipartFile file,
            @RequestParam("name") String name) {
        String nombreArchivo = firebaseService.subirArchivo(file, name);
        if (nombreArchivo != null) {
            return ResponseEntity.ok().build();
        } else {
            return ResponseEntity.status(500).build();
        }
    }

    @PostMapping("/delete")
    public ResponseEntity<Void> deleteFile(@RequestParam("name") String name) {
        firebaseService.borrarArchivo(name);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/rename")
    public ResponseEntity<Void> renameFile(
            @RequestParam("oldName") String oldName,
            @RequestParam("newName") String newName) {
        firebaseService.renombrarArchivo(oldName, newName);
        // Aquí deberías subir el archivo con el nuevo nombre, pero necesitarías el contenido del archivo para hacerlo.
        // Esto es solo un ejemplo de cómo podrías manejarlo.
        return ResponseEntity.ok().build();
    }

    @PostMapping("/duration")
    public Integer obtenerDuracionArchivo(
            @RequestParam ("archivo") MultipartFile archivo) {
        return AudioUtils.obtenerDuracionSegundos(archivo);
    }
}
