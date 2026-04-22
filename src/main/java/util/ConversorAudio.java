package util;

import java.io.*;

public class ConversorAudio {
    
    /**
     * Converte um arquivo MP3 para WAV usando FFmpeg
     * @param mp3 Arquivo MP3 de origem
     * @param wav Arquivo WAV de destino
     * @return true se a conversão foi bem sucedida, false caso contrário
     */
    public static boolean converterMP3paraWAVComFFmpeg(File mp3, File wav) {
        try {
            System.out.println("   🔄 Convertendo com FFmpeg...");
            
            // Verifica se o FFmpeg está disponível
            if (!verificarFFmpeg()) {
                System.out.println("   ⚠️  FFmpeg não encontrado no sistema");
                return false;
            }
            
            // Verifica se o WAV já existe
            if (wav.exists()) {
                System.out.println("   ⚠️  WAV já existe, pulando conversão");
                return true;
            }
            
            // Comando FFmpeg para conversão
            ProcessBuilder pb = new ProcessBuilder(
                "ffmpeg",
                "-i", mp3.getAbsolutePath(),      // Arquivo de entrada
                "-acodec", "pcm_s16le",            // Codec de áudio PCM 16-bit
                "-ar", "44100",                    // Sample rate 44.1kHz
                "-ac", "2",                        // 2 canais (stereo)
                "-y",                              // Sobrescreve arquivo se existir
                "-loglevel", "error",              // Mostra apenas erros
                wav.getAbsolutePath()              // Arquivo de saída
            );
            
            pb.redirectErrorStream(true);
            Process processo = pb.start();
            
            // Aguarda no máximo 60 segundos para a conversão
            boolean completou = processo.waitFor(60, java.util.concurrent.TimeUnit.SECONDS);
            
            if (completou && processo.exitValue() == 0 && wav.exists() && wav.length() > 0) {
                System.out.println("   ✅ Conversão com FFmpeg concluída!");
                return true;
            } else {
                processo.destroyForcibly();
                
                // Tenta limpar arquivo parcial se existir
                if (wav.exists()) {
                    wav.delete();
                }
                
                System.out.println("   ❌ FFmpeg falhou na conversão");
                return false;
            }
            
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            System.out.println("   ❌ Conversão interrompida: " + e.getMessage());
            return false;
        } catch (Exception e) {
            System.out.println("   ❌ Erro com FFmpeg: " + e.getMessage());
            return false;
        }
    }
    
    /**
     * Converte MP3 para WAV usando Java Sound API (método alternativo)
     * @param mp3 Arquivo MP3 de origem
     * @param wav Arquivo WAV de destino
     * @return true se a conversão foi bem sucedida, false caso contrário
     */
    public static boolean converterMP3paraWAVComJavaSound(File mp3, File wav) {
        try {
            System.out.println("   🔄 Tentando converter com Java Sound...");
            
            // Verifica se o WAV já existe
            if (wav.exists()) {
                System.out.println("   ⚠️  WAV já existe, pulando conversão");
                return true;
            }
            
            javax.sound.sampled.AudioInputStream mp3Stream = 
                javax.sound.sampled.AudioSystem.getAudioInputStream(mp3);
            
            javax.sound.sampled.AudioFormat baseFormat = mp3Stream.getFormat();
            
            // Cria formato PCM para WAV
            javax.sound.sampled.AudioFormat decodedFormat = 
                new javax.sound.sampled.AudioFormat(
                    javax.sound.sampled.AudioFormat.Encoding.PCM_SIGNED,
                    baseFormat.getSampleRate(),
                    16,
                    baseFormat.getChannels(),
                    baseFormat.getChannels() * 2,
                    baseFormat.getSampleRate(),
                    false
                );
            
            javax.sound.sampled.AudioInputStream decodedStream = 
                javax.sound.sampled.AudioSystem.getAudioInputStream(decodedFormat, mp3Stream);
            
            // Escreve o arquivo WAV
            javax.sound.sampled.AudioSystem.write(decodedStream, 
                javax.sound.sampled.AudioFileFormat.Type.WAVE, wav);
            
            decodedStream.close();
            mp3Stream.close();
            
            if (wav.exists() && wav.length() > 0) {
                System.out.println("   ✅ Conversão com Java Sound concluída!");
                return true;
            } else {
                System.out.println("   ❌ Java Sound falhou na conversão");
                return false;
            }
            
        } catch (javax.sound.sampled.UnsupportedAudioFileException e) {
            System.out.println("   ⚠️  Formato MP3 não suportado pelo Java Sound");
            return false;
        } catch (IOException e) {
            System.out.println("   ⚠️  Erro de I/O: " + e.getMessage());
            return false;
        } catch (Exception e) {
            System.out.println("   ❌ Erro no Java Sound: " + e.getMessage());
            return false;
        }
    }
    
    /**
     * Método principal de conversão - tenta FFmpeg primeiro, depois Java Sound
     * @param mp3 Arquivo MP3 de origem
     * @param wav Arquivo WAV de destino
     * @return true se a conversão foi bem sucedida, false caso contrário
     */
    public static boolean converterMP3paraWAV(File mp3, File wav) {
        // Tenta primeiro com FFmpeg (melhor qualidade)
        if (converterMP3paraWAVComFFmpeg(mp3, wav)) {
            return true;
        }
        
        // Se FFmpeg falhar, tenta com Java Sound
        System.out.println("   🔄 FFmpeg indisponível, tentando método alternativo...");
        return converterMP3paraWAVComJavaSound(mp3, wav);
    }
    
    /**
     * Verifica se o FFmpeg está instalado e acessível
     * @return true se FFmpeg está disponível, false caso contrário
     */
    private static boolean verificarFFmpeg() {
        try {
            ProcessBuilder pb = new ProcessBuilder("ffmpeg", "-version");
            Process processo = pb.start();
            int exitCode = processo.waitFor(5, java.util.concurrent.TimeUnit.SECONDS) ? 
                           processo.exitValue() : -1;
            return exitCode == 0;
        } catch (Exception e) {
            return false;
        }
    }
    
    /**
     * Força a exclusão de um arquivo com múltiplas tentativas
     * @param arquivo Arquivo a ser excluído
     * @return true se o arquivo foi excluído, false caso contrário
     */
    public static boolean forcarExclusao(File arquivo) {
        if (!arquivo.exists()) {
            return true;
        }
        
        // Tenta excluir imediatamente
        if (arquivo.delete()) {
            return true;
        }
        
        // Tenta várias vezes com garbage collection
        for (int i = 0; i < 5; i++) {
            System.gc(); // Força garbage collection
            
            try {
                Thread.sleep(100); // Aguarda 100ms
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
            
            if (arquivo.delete()) {
                return true;
            }
        }
        
        // Se ainda não conseguiu, marca para excluir na saída
        arquivo.deleteOnExit();
        return false;
    }
    
    /**
     * Verifica se um arquivo de áudio é válido
     * @param arquivo Arquivo a ser verificado
     * @return true se o arquivo é válido, false caso contrário
     */
    public static boolean verificarAudioValido(File arquivo) {
        if (!arquivo.exists() || arquivo.length() == 0) {
            return false;
        }
        
        try {
            javax.sound.sampled.AudioInputStream stream = 
                javax.sound.sampled.AudioSystem.getAudioInputStream(arquivo);
            stream.close();
            return true;
        } catch (Exception e) {
            return false;
        }
    }
}