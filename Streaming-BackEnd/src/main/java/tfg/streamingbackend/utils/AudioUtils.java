package tfg.streamingbackend.utils;

import org.springframework.web.multipart.MultipartFile;

import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import java.io.*;
import java.util.Optional;

public class AudioUtils {

    /**
     * Obtiene la duración de un MultipartFile de audio en segundos (entero).
     * Soporta MP3 y WAV. Devuelve null si el archivo es null o está vacío.
     */
    public static Integer obtenerDuracionSegundos(MultipartFile file) {
        if (file == null || file.isEmpty()) return null;
        try {
            String filename = Optional.ofNullable(file.getOriginalFilename()).orElse("");

            File tmp = File.createTempFile("audio_temp_", getExtensionOrDefault(filename));
            tmp.deleteOnExit();
            try (InputStream in = file.getInputStream(); OutputStream out = new FileOutputStream(tmp)) {
                in.transferTo(out);
            }

            return obtenerDuracionSegundos(tmp);
        } catch (IOException e) {
            throw new RuntimeException("Error al procesar el archivo de audio: " + e.getMessage(), e);
        }
    }

    /**
     * Obtiene la duración de un archivo de audio en segundos (entero).
     * Soporta MP3 y WAV.
     */
    public static Integer obtenerDuracionSegundos(File file) throws IOException {
        if (file == null || !file.exists()) return null;

        String nameLower = file.getName().toLowerCase();
        if (nameLower.endsWith(".mp3")) {
            return obtenerDuracionMp3(file);
        } else if (nameLower.endsWith(".wav")) {
            return obtenerDuracionWav(file);
        } else {
            // intentar detectar con AudioSystem
            try (AudioInputStream ais = AudioSystem.getAudioInputStream(file)) {
                AudioFormat format = ais.getFormat();
                long frames = ais.getFrameLength();
                float frameRate = format.getFrameRate();
                if (frames > 0 && frameRate > 0) {
                    double seconds = frames / frameRate;
                    return (int) Math.round(seconds);
                }
            } catch (Exception e) {
                throw new IllegalArgumentException("Formato de audio no soportado o archivo corrupto: " + file.getName(), e);
            }

            throw new IllegalArgumentException("Formato de audio no soportado: " + file.getName());
        }
    }

    private static Integer obtenerDuracionMp3(File file) throws IOException {
        // Implementación sin dependencias externas: intenta leer ID3v2 y estimar duración para CBR
        try (RandomAccessFile raf = new RandomAccessFile(file, "r")) {
            // Saltar ID3v2 tag si existe
            raf.seek(0);
            byte[] header = new byte[10];
            raf.read(header);
            int pos = 0;
            if (header[0] == 'I' && header[1] == 'D' && header[2] == '3') {
                // tamaño synchsafe en bytes 6..9
                int size = ((header[6] & 0x7F) << 21) | ((header[7] & 0x7F) << 14) | ((header[8] & 0x7F) << 7) | (header[9] & 0x7F);
                pos = 10 + size;
            } else {
                pos = 0;
            }

            // Buscar primer frame sync (0xFFE) a partir de pos
            raf.seek(pos);
            int b1;
            while ((b1 = raf.read()) != -1) {
                if (b1 == 0xFF) {
                    int b2 = raf.read();
                    if (b2 == -1) break;
                    if ((b2 & 0xE0) == 0xE0) {
                        // encontrado sync, b2 contiene también parte del header
                        int headerBytes = ((b1 & 0xFF) << 8) | (b2 & 0xFF);
                        // leer otros dos bytes del header
                        int b3 = raf.read();
                        int b4 = raf.read();
                        if (b3 == -1 || b4 == -1) break;
                        int hdr = (headerBytes << 16) | ((b3 & 0xFF) << 8) | (b4 & 0xFF);
                        // extraer bitrate index
                        int bitrateIndex = (hdr >> 12) & 0xF; // 4 bits
                        int samplingRateIndex = (hdr >> 10) & 0x3; // 2 bits

                        int bitrate = bitrateFromIndex(bitrateIndex);
                        int sampleRate = sampleRateFromIndex(samplingRateIndex);

                        if (bitrate > 0 && sampleRate > 0) {
                            long fileSizeBytes = file.length();
                            // estimar segundos = filesize * 8 / (bitrate * 1000)
                            double seconds = (fileSizeBytes * 8.0) / (bitrate * 1000.0);
                            return (int) Math.round(seconds);
                        } else {
                            break;
                        }
                    } else {
                        raf.seek(raf.getFilePointer() - 1); // retroceder un byte
                    }
                }
            }

            throw new IOException("No se pudo determinar duración MP3: frame header no encontrado o bitrate desconocido");
        }
    }

    private static int bitrateFromIndex(int index) {
        // Tabla simplificada para MPEG1 Layer III (kbps)
        int[] table = {0,32,40,48,56,64,80,96,112,128,160,192,224,256,320,0};
        if (index < 0 || index >= table.length) return -1;
        return table[index];
    }

    private static int sampleRateFromIndex(int index) {
        int[] table = {44100, 48000, 32000, 0};
        if (index < 0 || index >= table.length) return -1;
        return table[index];
    }

    private static Integer obtenerDuracionWav(File file) throws IOException {
        try (AudioInputStream ais = AudioSystem.getAudioInputStream(file)) {
            AudioFormat format = ais.getFormat();
            long frames = ais.getFrameLength();
            float frameRate = format.getFrameRate();
            if (frames > 0 && frameRate > 0) {
                double seconds = frames / frameRate;
                return (int) Math.round(seconds);
            }
            throw new IOException("No se pudo determinar la duración del WAV: " + file.getName());
        } catch (Exception e) {
            throw new IOException("Error leyendo WAV: " + e.getMessage(), e);
        }
    }

    private static String getExtensionOrDefault(String filename) {
        int i = filename.lastIndexOf('.');
        if (i >= 0) return filename.substring(i);
        return ".tmp";
    }
}
