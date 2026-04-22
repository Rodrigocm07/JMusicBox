package controller;

import model.Musicas;
import model.Playlists;
import dao.MusicasDAO;
import dao.PlaylistsDAO;
import util.MetadataExtractor;
import util.ConversorAudio;

import java.io.File;
import java.util.List;
import java.util.ArrayList;
import java.util.Random;
import java.util.HashSet;
import java.util.Set;

public class PlayerController {

    private final MusicasDAO musicasDAO;
    private final PlaylistsDAO playlistsDAO;
    private List<Musicas> playlistAtual;
    private int indiceAtual;
    
    // Configurações
    private boolean autoPlay = true;
    private boolean modoAleatorio = false;
    private Random random = new Random();
    
    // Histórico para modo aleatório (evita repetir)
    private Set<Integer> historicoAleatorio = new HashSet<>();
    private Integer ultimaMusicaAleatoria = null;

    public PlayerController() {
        this.musicasDAO = new MusicasDAO();
        this.playlistsDAO = new PlaylistsDAO();
        this.playlistAtual = new ArrayList<>();
        this.indiceAtual = 0;
    }

    // ==================== INICIALIZAÇÃO AUTOMÁTICA ====================
    
    public void inicializarSistema() {
        File pastaMusicas = new File("musicas");
        if (!pastaMusicas.exists()) {
            pastaMusicas.mkdir();
            return;
        }
        
        System.out.println("\n+==========================================+");
        System.out.println("|     INICIANDO CONVERSAO AUTOMATICA       |");
        System.out.println("+==========================================+");
        
        processarArquivosMP3(pastaMusicas);
    }
    
    private void processarArquivosMP3(File pasta) {
        File[] arquivosMP3 = pasta.listFiles((dir, nome) -> 
            nome.toLowerCase().endsWith(".mp3"));
        
        if (arquivosMP3 == null || arquivosMP3.length == 0) {
            System.out.println("\n>> Nenhum arquivo MP3 encontrado.");
            return;
        }
        
        System.out.println("\n>> Encontrados " + arquivosMP3.length + " arquivo(s) MP3.");
        System.out.println(">> Iniciando extracao de metadados e conversao...\n");
        
        int processados = 0;
        int convertidos = 0;
        int mantidosComoMP3 = 0;
        
        for (File mp3 : arquivosMP3) {
            processados++;
            System.out.println("[" + processados + "/" + arquivosMP3.length + "] " + mp3.getName());
            
            try {
                Musicas metadados = MetadataExtractor.extrairMetadadosMP3(mp3);
                System.out.println("   Titulo: " + metadados.getTitulo());
                System.out.println("   Artista: " + metadados.getArtista());
                System.out.println("   Album: " + metadados.getAlbum());
                System.out.println("   Duracao: " + metadados.getDuracao());
                
                String nomeWAV = mp3.getName().replaceAll("(?i)\\.mp3$", ".wav");
                File wav = new File(pasta, nomeWAV);
                
                boolean convertido = ConversorAudio.converterMP3paraWAV(mp3, wav);
                
                if (convertido && wav.exists() && wav.length() > 0) {
                    System.out.println("   >> Convertido para WAV: " + nomeWAV);
                    
                    metadados.setCaminho_arquivo(wav.getAbsolutePath());
                    
                    List<Musicas> existentes = musicasDAO.listarMusicas();
                    boolean existe = false;
                    for (Musicas m : existentes) {
                        if (m.getCaminho_arquivo().equals(wav.getAbsolutePath())) {
                            existe = true;
                            break;
                        }
                    }
                    
                    if (!existe) {
                        musicasDAO.adicionarMusica(metadados);
                        System.out.println("   >> Salvo no banco de dados");
                    }
                    
                    if (ConversorAudio.forcarExclusao(mp3)) {
                        System.out.println("   >> MP3 original excluido");
                        convertidos++;
                    } else {
                        System.out.println("   !! MP3 marcado para exclusao futura");
                    }
                    
                } else {
                    System.out.println("   !! Conversao falhou - mantendo como MP3");
                    
                    List<Musicas> existentes = musicasDAO.listarMusicas();
                    boolean existe = false;
                    for (Musicas m : existentes) {
                        if (m.getCaminho_arquivo().equals(mp3.getAbsolutePath())) {
                            existe = true;
                            break;
                        }
                    }
                    
                    if (!existe) {
                        metadados.setCaminho_arquivo(mp3.getAbsolutePath());
                        musicasDAO.adicionarMusica(metadados);
                        System.out.println("   >> Salvo como MP3 no banco");
                    }
                    
                    mantidosComoMP3++;
                }
                
            } catch (Exception e) {
                System.out.println("   !! Erro ao processar: " + e.getMessage());
                mantidosComoMP3++;
            }
            
            System.out.println();
        }
        
        System.out.println("+==========================================+");
        System.out.println("|           RESUMO DO PROCESSAMENTO        |");
        System.out.println("+==========================================+");
        System.out.println("   >> Convertidos para WAV: " + convertidos);
        System.out.println("   >> Mantidos como MP3: " + mantidosComoMP3);
        System.out.println("   >> Total processados: " + processados);
        System.out.println("+==========================================+\n");
    }

    // ==================== CONFIGURAÇÕES ====================
    
    public boolean isAutoPlay() {
        return autoPlay;
    }

    public void setAutoPlay(boolean autoPlay) {
        this.autoPlay = autoPlay;
    }

    public boolean isModoAleatorio() {
        return modoAleatorio;
    }

    public void setModoAleatorio(boolean modoAleatorio) {
        this.modoAleatorio = modoAleatorio;
        if (!modoAleatorio) {
            // Limpa o histórico quando desativa modo aleatório
            historicoAleatorio.clear();
            ultimaMusicaAleatoria = null;
        }
    }

    // ==================== BIBLIOTECA ====================
    
    public List<Musicas> carregarBiblioteca() {
        return musicasDAO.listarMusicas();
    }

    public void escanearPastaMusicas(String pastaPath) {
        File pasta = new File(pastaPath);
        if (!pasta.exists()) {
            pasta.mkdir();
            return;
        }

        sincronizarBiblioteca(pastaPath);

        List<Musicas> musicasExistentes = musicasDAO.listarMusicas();

        for (File arquivo : pasta.listFiles()) {
            if (arquivo.isFile() && (arquivo.getName().endsWith(".mp3") || arquivo.getName().endsWith(".wav"))) {

                boolean jaExiste = false;
                for (Musicas m : musicasExistentes) {
                    if (m.getCaminho_arquivo().equals(arquivo.getAbsolutePath())) {
                        jaExiste = true;
                        break;
                    }
                }

                if (!jaExiste) {
                    Musicas musica = MetadataExtractor.extrairMetadados(arquivo);
                    musicasDAO.adicionarMusica(musica);
                }
            }
        }
    }

    public void sincronizarBiblioteca(String pastaPath) {
        File pasta = new File(pastaPath);
        if (!pasta.exists()) {
            return;
        }

        List<Musicas> musicasNoBanco = musicasDAO.listarMusicas();
        List<String> caminhosNaPasta = new ArrayList<>();

        for (File arquivo : pasta.listFiles()) {
            if (arquivo.isFile() && (arquivo.getName().endsWith(".mp3") || arquivo.getName().endsWith(".wav"))) {
                caminhosNaPasta.add(arquivo.getAbsolutePath());
            }
        }

        for (Musicas m : musicasNoBanco) {
            if (!caminhosNaPasta.contains(m.getCaminho_arquivo())) {
                musicasDAO.removerMusica(m);
            }
        }
    }

    // ==================== CONTROLE DA PLAYLIST ====================
    
    public void setIndiceAtual(int indice) {
        if (playlistAtual != null && indice >= 0 && indice < playlistAtual.size()) {
            this.indiceAtual = indice;
        }
    }

    public void setPlaylistAtual(List<Musicas> playlist) {
        this.playlistAtual = playlist;
        this.indiceAtual = 0;
        // Limpa histórico quando muda de playlist
        historicoAleatorio.clear();
        ultimaMusicaAleatoria = null;
    }

    public Musicas getMusicaAtual() {
        if (playlistAtual == null || playlistAtual.isEmpty()) {
            return null;
        }
        return playlistAtual.get(indiceAtual);
    }

    public void next() {
        if (playlistAtual == null || playlistAtual.isEmpty()) {
            return;
        }
        
        if (playlistAtual.size() == 1) {
            indiceAtual = 0;
            return;
        }
        
        if (modoAleatorio) {
            // Modo aleatório com garantia de não repetir
            int novoIndice;
            
            // Se já tocou todas as músicas, reinicia o histórico
            if (historicoAleatorio.size() >= playlistAtual.size()) {
                historicoAleatorio.clear();
            }
            
            // Adiciona o índice atual ao histórico (se não for null)
            if (ultimaMusicaAleatoria != null) {
                historicoAleatorio.add(ultimaMusicaAleatoria);
            }
            
            // Escolhe um índice que não está no histórico
            do {
                novoIndice = random.nextInt(playlistAtual.size());
            } while (historicoAleatorio.contains(novoIndice) && historicoAleatorio.size() < playlistAtual.size());
            
            indiceAtual = novoIndice;
            ultimaMusicaAleatoria = novoIndice;
            
        } else {
            // Modo normal (sequencial)
            indiceAtual = (indiceAtual + 1) % playlistAtual.size();
        }
    }

    public void previous() {
        if (playlistAtual == null || playlistAtual.isEmpty()) {
            return;
        }
        
        if (playlistAtual.size() == 1) {
            indiceAtual = 0;
            return;
        }
        
        if (modoAleatorio) {
            // No modo aleatório, voltar também é aleatório (sem repetir)
            int novoIndice;
            
            if (historicoAleatorio.size() >= playlistAtual.size()) {
                historicoAleatorio.clear();
            }
            
            do {
                novoIndice = random.nextInt(playlistAtual.size());
            } while (historicoAleatorio.contains(novoIndice) && historicoAleatorio.size() < playlistAtual.size());
            
            indiceAtual = novoIndice;
            ultimaMusicaAleatoria = novoIndice;
            
        } else {
            // Modo normal
            indiceAtual--;
            if (indiceAtual < 0) {
                indiceAtual = playlistAtual.size() - 1;
            }
        }
    }
    
    /**
     * Verifica se deve tocar a próxima música automaticamente
     * Chamado quando a música atual termina
     */
    public boolean deveTocarProxima() {
        return autoPlay;
    }

    // ==================== PLAYLISTS ====================
    
    public List<Playlists> listarPlaylists() {
        return playlistsDAO.listarPlaylists();
    }

    public void criarPlaylist(String nome) {
        Playlists novaPlaylist = new Playlists(nome);
        playlistsDAO.adicionarPlaylist(novaPlaylist);
    }

    public List<Musicas> carregarPlaylist(int idPlaylist) {
        return playlistsDAO.listarMusicasDaPlaylist(idPlaylist);
    }

    public void adicionarMusicaNaPlaylist(int idPlaylist, int idMusica, int ordem) {
        playlistsDAO.adicionarMusicaNaPlaylist(idPlaylist, idMusica, ordem);
    }

    public void removerMusicaDaPlaylist(int idPlaylist, int idMusica) {
        playlistsDAO.removerMusicaDaPlaylist(idPlaylist, idMusica);
    }

    public int getTamanhoPlaylist() {
        return playlistAtual != null ? playlistAtual.size() : 0;
    }

    public int getIndiceAtual() {
        return indiceAtual + 1;
    }
    
    public void removerMusica(Musicas musica) {
        musicasDAO.removerMusica(musica);
    }
    
    public void atualizarMetadadosMusica(Musicas musica) {
        musicasDAO.editarMusica(musica);
    }
}