package tfg.KeySound.utils;

import org.springframework.web.multipart.MultipartFile;
import tfg.KeySound.exception.archivo.AudioProcessingException;

import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import java.io.*;
import java.nio.charset.StandardCharsets;
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
            throw new AudioProcessingException(e.getMessage());
        }
    }

    private static Integer obtenerDuracionSegundos(File file) throws IOException {
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
        // Implementación mejorada: parseo de header MPEG (version/layer), detección de Xing/VBRI y cálculo por frames si es posible.
        try (RandomAccessFile raf = new RandomAccessFile(file, "r")) {
            // Saltar ID3v2 tag si existe
            raf.seek(0);
            byte[] header = new byte[10];
            raf.read(header);
            int pos;
            if (header[0] == 'I' && header[1] == 'D' && header[2] == '3') {
                // tamaño synchsafe en bytes 6..9
                int size = ((header[6] & 0x7F) << 21) | ((header[7] & 0x7F) << 14) | ((header[8] & 0x7F) << 7) | (header[9] & 0x7F);
                pos = 10 + size;
            } else {
                pos = 0;
            }

            // Buscar primer frame sync (0xFFF) a partir de pos
            raf.seek(pos);
            int b1;
            while ((b1 = raf.read()) != -1) {
                if (b1 == 0xFF) {
                    int b2 = raf.read();
                    if (b2 == -1) break;
                    if ((b2 & 0xE0) == 0xE0) {
                        int b3 = raf.read();
                        int b4 = raf.read();
                        if (b3 == -1 || b4 == -1) break;
                        int hdr = ((b1 & 0xFF) << 24) | ((b2 & 0xFF) << 16) | ((b3 & 0xFF) << 8) | (b4 & 0xFF);

                        int versionBits = (hdr >> 19) & 0x3; // 00-MPEG2.5,01-reserved,10-MPEG2,11-MPEG1
                        int layerBits = (hdr >> 17) & 0x3;   // 01-Layer3,10-Layer2,11-Layer1
                        int bitrateIndex = (hdr >> 12) & 0xF;
                        int samplingRateIndex = (hdr >> 10) & 0x3;
                        int channelMode = (hdr >> 6) & 0x3;

                        int version; // 1 = MPEG1, 2 = MPEG2, 25 = MPEG2.5
                        if (versionBits == 3) version = 1; else if (versionBits == 2) version = 2; else if (versionBits == 0) version = 25; else version = -1;

                        int layer; // 1 = Layer I, 2 = Layer II, 3 = Layer III
                        if (layerBits == 3) layer = 1; else if (layerBits == 2) layer = 2; else if (layerBits == 1) layer = 3; else layer = -1;

                        int bitrate = bitrateKbps(version, layer, bitrateIndex);
                        int sampleRate = sampleRateFor(version, samplingRateIndex);

                        if (bitrate <= 0 || sampleRate <= 0) {
                            break; // intentar siguiente posible frame
                        }

                        int samplesPerFrame;
                        if (layer == 1) samplesPerFrame = 384;
                        else if (layer == 2) samplesPerFrame = 1152;
                        else { // Layer III
                            samplesPerFrame = (version == 1) ? 1152 : 576;
                        }

                        // intentar detectar encabezado Xing/Info (VBR) y VBRI
                        long frameStart = raf.getFilePointer() - 4; // nos situamos en el inicio del frame (leímos 4 bytes)

                        // calcular tamaño side info
                        int sideInfoSize;
                        if (version == 1) {
                            sideInfoSize = (channelMode == 3) ? 17 : 32; // mono:17, stereo:32 (MPEG1)
                        } else {
                            sideInfoSize = (channelMode == 3) ? 9 : 17; // MPEG2/2.5
                        }

                        // Comprobar Xing/Info
                        try {
                            long xingOffset = frameStart + 4 + sideInfoSize;
                            raf.seek(xingOffset);
                            byte[] tag = new byte[4];
                            raf.readFully(tag);
                            String tagStr = new String(tag, StandardCharsets.ISO_8859_1);
                            if ("Xing".equals(tagStr) || "Info".equals(tagStr)) {
                                // flags
                                int flags = raf.readInt();
                                int totalFrames = -1;
                                if ((flags & 0x1) != 0) { // frames flag
                                    totalFrames = raf.readInt();
                                }
                                if (totalFrames > 0) {
                                    double seconds = (totalFrames * (double) samplesPerFrame) / sampleRate;
                                    return (int) Math.round(seconds);
                                }
                            } else {
                                // Comprobar VBRI en offset 4 + 32 (a menudo)
                                long vbriOffset = frameStart + 4 + 32;
                                raf.seek(vbriOffset);
                                raf.readFully(tag);
                                tagStr = new String(tag, StandardCharsets.ISO_8859_1);
                                if ("VBRI".equals(tagStr)) {
                                    // estructura VBRI: 4 bytes 'VBRI', 2 bytes version, 2 bytes delay, 2 bytes quality, 4 bytes size, 4 bytes frames
                                    raf.skipBytes(6); // version(2), delay(2), quality(2)
                                    raf.readInt(); // tamaño en bytes (ignoramos)
                                    int totalFrames = raf.readInt();
                                    if (totalFrames > 0) {
                                        double seconds = (totalFrames * (double) samplesPerFrame) / sampleRate;
                                        return (int) Math.round(seconds);
                                    }
                                }
                            }
                        } catch (Exception ignored) {
                            // si falla la detección VBR, seguimos al fallback
                        }

                        // Fallback para CBR: estimar por tamaño de archivo y bitrate
                        long fileSizeBytes = file.length();
                        long audioDataBytes = fileSizeBytes;
                        // restar ID3v2 ya calculado (pos) y posible ID3v1 (128 bytes al final)
                        if (pos > 0) audioDataBytes = fileSizeBytes - pos;
                        try {
                            if (fileSizeBytes > 128) {
                                raf.seek(fileSizeBytes - 128);
                                byte[] last = new byte[3];
                                raf.readFully(last);
                                if (last[0] == 'T' && last[1] == 'A' && last[2] == 'G') {
                                    audioDataBytes -= 128;
                                }
                            }
                        } catch (Exception ignored) {
                        }

                        double seconds = (audioDataBytes * 8.0) / (bitrate * 1000.0);
                        return (int) Math.round(seconds);

                    } else {
                        raf.seek(raf.getFilePointer() - 1); // retroceder un byte
                    }
                }
            }

            throw new IOException("No se pudo determinar duración MP3: frame header no encontrado o bitrate desconocido");
        }
    }

    // Tablas completas de bitrate según version y layer
    private static int bitrateKbps(int version, int layer, int index) {
        // index 0 = free, 15 = bad
        if (index <= 0 || index >= 15) return -1;
        // version: 1=MPEG1, 2=MPEG2, 25=MPEG2.5
        // layer: 1=LayerI, 2=LayerII, 3=LayerIII
        // tablas tomadas de referencia estándar
        int[][][] table = new int[][][]{
                // MPEG1 (version==1)
                {
                        // Layer I
                        {0,32,64,96,128,160,192,224,256,288,320,352,384,416,448,0},
                        // Layer II
                        {0,32,48,56,64,80,96,112,128,160,192,224,256,320,384,0},
                        // Layer III
                        {0,32,40,48,56,64,80,96,112,128,160,192,224,256,320,0}
                },
                // MPEG2 / MPEG2.5 (version==2 or 25)
                {
                        // Layer I
                        {0,32,48,56,64,80,96,112,128,144,160,176,192,224,256,0},
                        // Layer II
                        {0,8,16,24,32,40,48,56,64,80,96,112,128,144,160,0},
                        // Layer III
                        {0,8,16,24,32,40,48,56,64,80,96,112,128,144,160,0}
                }
        };

        int versionIndex = (version == 1) ? 0 : 1;
        int layerIndex = (layer == 1) ? 0 : (layer == 2) ? 1 : 2;
        return table[versionIndex][layerIndex][index];
    }

    private static int sampleRateFor(int version, int index) {
        if (index < 0 || index > 2) return -1;
        // index: 0,1,2
        if (version == 1) {
            int[] v1 = {44100,48000,32000};
            return v1[index];
        } else if (version == 2) {
            int[] v2 = {22050,24000,16000};
            return v2[index];
        } else if (version == 25) {
            int[] v25 = {11025,12000,8000};
            return v25[index];
        }
        return -1;
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
