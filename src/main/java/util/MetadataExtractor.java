package util;

import model.Musicas;
import org.jaudiotagger.audio.mp3.MP3File;
import org.jaudiotagger.tag.FieldKey;
import org.jaudiotagger.tag.Tag;
import org.jaudiotagger.tag.id3.AbstractID3v2Tag;

import javax.sound.sampled.*;
import java.io.File;

public class MetadataExtractor {
    
    public static Musicas extrairMetadados(File arquivoAudio) {
        if (arquivoAudio.getName().toLowerCase().endsWith(".mp3")) {
            return extrairMetadadosMP3(arquivoAudio);
        }
        return extrairMetadadosBasico(arquivoAudio);
    }
    
    public static Musicas extrairMetadadosMP3(File arquivoMP3) {
        String titulo = arquivoMP3.getName().replaceFirst("[.][^.]+$", "");
        String artista = "Desconhecido";
        String album = "Desconhecido";
        int ano = 0;
        String genero = "Desconhecido";
        String duracao = "0:00";
        
        try {
            MP3File mp3File = new MP3File(arquivoMP3);
            Tag tag = null;
            
            // Obtém a tag disponível (ID3v2, ID3v1 ou genérica)
            if (mp3File.hasID3v2Tag()) {
                tag = mp3File.getID3v2Tag();
                System.out.println("   📝 Tags ID3v2 encontradas!");
            } else if (mp3File.hasID3v1Tag()) {
                tag = mp3File.getID3v1Tag();
                System.out.println("   📝 Tags ID3v1 encontradas!");
            } else {
                tag = mp3File.getTag();
                if (tag != null) {
                    System.out.println("   📝 Tags genéricas encontradas!");
                }
            }
            
            if (tag != null) {
                // Título
                String tagTitulo = tag.getFirst(FieldKey.TITLE);
                if (tagTitulo != null && !tagTitulo.isEmpty()) {
                    titulo = tagTitulo;
                }
                
                // Artista
                String tagArtista = tag.getFirst(FieldKey.ARTIST);
                if (tagArtista != null && !tagArtista.isEmpty()) {
                    artista = tagArtista;
                }
                
                // Álbum
                String tagAlbum = tag.getFirst(FieldKey.ALBUM);
                if (tagAlbum != null && !tagAlbum.isEmpty()) {
                    album = tagAlbum;
                }
                
                // Ano
                String tagAno = tag.getFirst(FieldKey.YEAR);
                if (tagAno != null && !tagAno.isEmpty()) {
                    ano = parseAno(tagAno);
                }
                
                // Gênero
                String tagGenero = tag.getFirst(FieldKey.GENRE);
                if (tagGenero != null && !tagGenero.isEmpty()) {
                    genero = tagGenero;
                }
                
                System.out.println("   ✅ Título: " + titulo);
                System.out.println("      Artista: " + artista);
                System.out.println("      Ano: " + (ano > 0 ? ano : "Não informado"));
                System.out.println("      Gênero: " + genero);
            } else {
                System.out.println("   ⚠️  Nenhuma tag ID3 encontrada");
            }
            
            // Duração
            if (mp3File.getAudioHeader() != null) {
                int duracaoSegundos = mp3File.getAudioHeader().getTrackLength();
                int minutos = duracaoSegundos / 60;
                int segundos = duracaoSegundos % 60;
                duracao = String.format("%d:%02d", minutos, segundos);
            }
            
        } catch (Exception e) {
            System.out.println("   ❌ Erro ao ler metadados: " + e.getMessage());
        }
        
        return new Musicas(titulo, artista, album, ano, genero, duracao, arquivoMP3.getAbsolutePath());
    }
    
    private static int parseAno(String anoStr) {
        if (anoStr == null || anoStr.trim().isEmpty()) {
            return 0;
        }
        
        try {
            // Remove caracteres não numéricos
            String numeros = anoStr.replaceAll("[^0-9]", "");
            if (numeros.length() >= 4) {
                int ano = Integer.parseInt(numeros.substring(0, 4));
                if (ano >= 1900 && ano <= 2030) {
                    return ano;
                }
            }
        } catch (NumberFormatException e) {
            // Ignora
        }
        
        return 0;
    }
    
    private static Musicas extrairMetadadosBasico(File arquivoAudio) {
        String nomeArquivo = arquivoAudio.getName();
        String titulo = nomeArquivo.substring(0, nomeArquivo.lastIndexOf('.'));
        String artista = "Desconhecido";
        String album = "Desconhecido";
        int ano = 0;
        String genero = "Desconhecido";
        String duracao = "0:00";
        
        // Tenta extrair do nome do arquivo (formato: "Artista - Título")
        if (titulo.contains(" - ")) {
            String[] partes = titulo.split(" - ", 2);
            artista = partes[0].trim();
            titulo = partes[1].trim();
        }
        
        // Tenta extrair duração
        try {
            AudioInputStream audioStream = AudioSystem.getAudioInputStream(arquivoAudio);
            AudioFormat format = audioStream.getFormat();
            
            long frames = audioStream.getFrameLength();
            if (frames > 0 && format.getFrameRate() > 0) {
                double duracaoEmSegundos = frames / format.getFrameRate();
                int minutos = (int) (duracaoEmSegundos / 60);
                int segundos = (int) (duracaoEmSegundos % 60);
                duracao = String.format("%d:%02d", minutos, segundos);
            }
            
            audioStream.close();
            
        } catch (Exception e) {
            // Ignora
        }
        
        return new Musicas(titulo, artista, album, ano, genero, duracao, arquivoAudio.getAbsolutePath());
    }
}