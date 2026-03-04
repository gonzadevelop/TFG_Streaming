package tfg.streamingbackend.firebase;

import com.google.cloud.storage.Blob;
import com.google.cloud.storage.Bucket;
import com.google.firebase.cloud.StorageClient;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@Service
public class FirebaseService {

    public void subirArchivo(MultipartFile archivo, String nombreDestino) throws IOException {
        // Obtener el bucket configurado en FirebaseConfig
        Bucket bucket = StorageClient.getInstance().bucket();

        if (bucket == null) {
            throw new IOException("El bucket por defecto no está configurado. Revisa FirebaseConfig.");
        }

        bucket.create(nombreDestino, archivo.getBytes(), archivo.getContentType());
    }

    public void borrarArchivo(String nombreArchivo) {
        // Obtener el bucket configurado en FirebaseConfig
        Bucket bucket = StorageClient.getInstance().bucket();

        Blob blob = bucket.get(nombreArchivo);
        if (blob != null) {
            blob.delete();
        }
    }

    public String obtenerUrlArchivo(String nombreArchivo) {
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