package tfg.KeySound.services.external;

import com.google.cloud.storage.Blob;
import com.google.cloud.storage.Bucket;
import com.google.firebase.cloud.StorageClient;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import tfg.KeySound.exception.archivo.FileUploadException;

import java.io.IOException;
import java.util.UUID;

@Service
public class FirebaseService {

    public String subirArchivo(MultipartFile archivo, String nombreDestino) {
        // Obtener el bucket configurado en FirebaseConfig
        String nombreFinal = nombreDestino + "_" + UUID.randomUUID();
        Bucket bucket = StorageClient.getInstance().bucket();

        try {
            bucket.create(nombreFinal, archivo.getBytes(), archivo.getContentType());
        } catch (IOException e) {
            throw new FileUploadException();
        }

        return nombreFinal;
    }

    public void borrarArchivo(String nombreArchivo) {
        // Obtener el bucket configurado en FirebaseConfig
        Bucket bucket = StorageClient.getInstance().bucket();

        Blob blob = bucket.get(nombreArchivo);
        if (blob != null) {
            blob.delete();
        }
    }

    public void renombrarArchivo(String nombreArchivo, String nuevoNombre) {
        Bucket bucket = StorageClient.getInstance().bucket();

        Blob blob = bucket.get(nombreArchivo);
        if (blob != null) {
            // Copiar el archivo con el nuevo nombre
            Blob nuevoBlob = blob.copyTo(bucket.getName(), nuevoNombre).getResult();
            // Eliminar el archivo original
            blob.delete();
        }
    }

    public String obtenerUrlArchivoAudio(String nombreArchivo) {
        Bucket bucket = StorageClient.getInstance().bucket();

        // Generar URL pública del archivo
        // Formato: https://firebasestorage.googleapis.com/v0/b/{bucket}/o/{archivo}?alt=media
        return String.format(
                "https://firebasestorage.googleapis.com/v0/b/%s/o/%s?alt=media",
                bucket.getName(),
                nombreArchivo.replace("/", "%2F")
        );
    }

    public String obtenerUrlArchivoImagen(String nombreArchivo, String nombre) {
        if (nombreArchivo == null || nombreArchivo.isEmpty()) return "https://ui-avatars.com/api/?name=" + nombre.charAt(0) + "&background=0b75c0&bold=true&color=FFF&size=256";
        Bucket bucket = StorageClient.getInstance().bucket();

        // Generar URL pública del archivo
        // Formato: https://firebasestorage.googleapis.com/v0/b/{bucket}/o/{archivo}?alt=media
        return String.format(
                "https://firebasestorage.googleapis.com/v0/b/%s/o/%s?alt=media",
                bucket.getName(),
                nombreArchivo.replace("/", "%2F")
        );
    }
}