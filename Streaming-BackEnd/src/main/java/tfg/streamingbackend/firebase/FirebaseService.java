package tfg.streamingbackend.firebase;

import com.google.cloud.storage.Blob;
import com.google.cloud.storage.Bucket;
import com.google.firebase.cloud.StorageClient;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@Service
public class FirebaseService {

    public String subirArchivo(MultipartFile archivo, String nombreDestino) throws IOException {
        // Obtener el bucket configurado en FirebaseConfig
        Bucket bucket = StorageClient.getInstance().bucket();

        if (bucket == null) {
            throw new IOException("El bucket por defecto no está configurado. Revisa FirebaseConfig.");
        }

        bucket.create(nombreDestino, archivo.getBytes(), archivo.getContentType());

        // Devolver el nombre del archivo subido para que pueda ser almacenado en la base de datos
        return nombreDestino;
    }

    public void borrarArchivo(String nombreArchivo) {
        // Obtener el bucket configurado en FirebaseConfig
        Bucket bucket = StorageClient.getInstance().bucket();

        Blob blob = bucket.get(nombreArchivo);
        if (blob != null) {
            blob.delete();
        }
    }
}