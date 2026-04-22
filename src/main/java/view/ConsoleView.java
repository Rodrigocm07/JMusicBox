package view;

import model.Musicas;
import model.Playlists;
import controller.PlayerController;
import controller.MusicPlayer;

import java.io.IOException;
import java.util.List;
import java.util.Scanner;

public class ConsoleView {

    private final PlayerController controller;
    private final Scanner scanner;
    private List<Musicas> playlistAtual;

    public ConsoleView() {
        this.controller = new PlayerController();
        this.scanner = new Scanner(System.in);
    }

    public static void main(String[] args) {
        ConsoleView view = new ConsoleView();
        view.iniciar();
    }

    public void iniciar() {
        System.out.println("+===========================================+");
        System.out.println("|       JMusicBox - Console Player          |");
        System.out.println("+===========================================+");

        controller.inicializarSistema();
        controller.escanearPastaMusicas("musicas");
        playlistAtual = controller.carregarBiblioteca();
        controller.setPlaylistAtual(playlistAtual);

        System.out.println("\n>> Biblioteca carregada com " + playlistAtual.size() + " musica(s)!");

        while (true) {
            exibirPlayer();
            System.out.print("\n>> Comando: ");
            String comando = scanner.nextLine().toLowerCase().trim();

            switch (comando) {
                case "play":
                case "p":
                    if (MusicPlayer.isTocando()) {
                        MusicPlayer.pausar();
                        System.out.println("|| Musica pausada");
                    } else if (MusicPlayer.isPausado()) {
                        MusicPlayer.retomar();
                        System.out.println("> Musica retomada");
                    } else {
                        play();
                    }
                    break;

                case "pause":
                    MusicPlayer.pausar();
                    System.out.println("|| Musica pausada");
                    break;

                case "next":
                case "n":
                    next();
                    break;

                case "prev":
                case "b":
                    previous();
                    break;

                case "stop":
                case "s":
                    stop();
                    break;

                case "list":
                case "l":
                    listarMusicas();
                    break;

                case "playlists":
                    listarPlaylists();
                    break;

                case "criar":
                    criarPlaylist();
                    break;

                case "carregar":
                    carregarPlaylist();
                    break;

                case "add":
                    adicionarMusicaNaPlaylist();
                    break;

                case "autoplay":
                    controller.setAutoPlay(!controller.isAutoPlay());
                    System.out.println(">> AutoPlay: " + (controller.isAutoPlay() ? "ON" : "OFF"));
                    esperarEnter();
                    break;

                case "random":
                case "aleatorio":
                    controller.setModoAleatorio(!controller.isModoAleatorio());
                    System.out.println(">> Modo Aleatorio: " + (controller.isModoAleatorio() ? "ON" : "OFF"));
                    esperarEnter();
                    break;

                case "voltar":
                case "v":
                    voltarParaBiblioteca();
                    break;

                case "rescan":
                case "r":
                    System.out.println("\n>> Reescanendo pasta...");
                    controller.escanearPastaMusicas("musicas");
                    playlistAtual = controller.carregarBiblioteca();
                    controller.setPlaylistAtual(playlistAtual);
                    System.out.println(">> Biblioteca atualizada com " + playlistAtual.size() + " musicas!");
                    esperarEnter();
                    break;

                case "info":
                case "i":
                    mostrarInfo();
                    break;

                case "quit":
                case "q":
                    MusicPlayer.parar();
                    System.out.println("\n>> Ate logo!");
                    scanner.close();
                    System.exit(0);
                    break;

                default:
                    System.out.println("!! Comando nao reconhecido!");
                    esperarEnter();
            }
        }
    }

    private void exibirPlayer() {

        System.out.println("+===========================================+");
        System.out.println("|          JMusicBox Player                 |");
        System.out.println("+===========================================+");

        Musicas atual = controller.getMusicaAtual();

        if (atual != null) {
            String status;
            if (MusicPlayer.isTocando()) {
                status = "> TOCANDO";
            } else if (MusicPlayer.isPausado()) {
                status = "|| PAUSADO";
            } else {
                status = "[] PARADO";
            }

            System.out.printf("  (*) %-38s \n", truncar(atual.getTitulo(), 38));
            System.out.printf("  (') %-38s \n", truncar(atual.getArtista(), 38));
            System.out.printf("  (o) %-38s \n", truncar(atual.getAlbum(), 38));

            String infoExtra = "";
            if (atual.getAno() > 0) {
                infoExtra += "#" + atual.getAno();
            }
            if (atual.getGenero() != null && !atual.getGenero().equals("Desconhecido")) {
                if (!infoExtra.isEmpty()) {
                    infoExtra += "  ";
                }
                infoExtra += "~" + truncar(atual.getGenero(), 15);
            }
            if (!infoExtra.isEmpty()) {
                System.out.printf("  (i) %-38s \n", infoExtra);
            }

            System.out.printf("  Duração: %-31s \n", atual.getDuracao());
            System.out.printf("  Status: %-33s \n", status);

        } else {
            System.out.println("|         -- Nenhuma musica carregada --    |");
        }

        System.out.println("+-------------------------------------------+");
        System.out.printf("  Playlist: %d musicas\n", controller.getTamanhoPlaylist());
        System.out.printf("  Posicao: %d de %d\n", controller.getIndiceAtual(), controller.getTamanhoPlaylist());
        System.out.printf("  AutoPlay: %-3s   |   Aleatorio: %-3s\n",
                controller.isAutoPlay() ? "ON" : "OFF",
                controller.isModoAleatorio() ? "ON" : "OFF");
        System.out.println("+-------------------------------------------+");
        System.out.println("| play | pause | next | prev | stop | list  |");
        System.out.println("| autoplay | random | playlists | criar     |");
        System.out.println("| carregar | add | voltar | rescan | quit   |");
        System.out.println("+-------------------------------------------+");
        System.out.println("  Dica: 'V'=voltar a playlist geral | 'R'=rescan | 'I'=ajuda");
    }

    private void play() {
        Musicas atual = controller.getMusicaAtual();
        if (atual != null) {
            System.out.println("\n> Tocando: " + atual.getArtista() + " - " + atual.getTitulo());
            MusicPlayer.tocar(atual.getCaminho_arquivo());
            iniciarMonitoramentoFimMusica();
        } else {
            System.out.println("!! Nenhuma musica na playlist!");
            esperarEnter();
        }
    }

   private void iniciarMonitoramentoFimMusica() {
    new Thread(() -> {
        // Aguarda 2 segundos para a música iniciar
        try {
            Thread.sleep(2000);
        } catch (InterruptedException e) {
            return;
        }
        
        // Só continua se a música estiver realmente tocando
        if (!MusicPlayer.isTocando() && !MusicPlayer.isPausado()) {
            return;
        }
        
        // Monitora o fim da música
        long tempoSemTocar = 0;
        
        while (tempoSemTocar < 3) { // 3 segundos sem tocar = terminou
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                return;
            }
            
            if (MusicPlayer.isTocando() || MusicPlayer.isPausado()) {
                tempoSemTocar = 0; // Reset se voltou a tocar
            } else {
                tempoSemTocar++; // Incrementa se não está tocando
            }
        }
        
        // Música realmente terminou
        if (controller.isAutoPlay()) {
            System.out.println("\n>> AutoPlay: Proxima musica...");
            next();
        }
    }).start();
}

    private void next() {
        controller.next();
        play();
    }

    private void previous() {
        controller.previous();
        play();
    }

    private void stop() {
        MusicPlayer.parar();
        System.out.println("[] Musica parada");
        esperarEnter();
    }

    private void listarMusicas() {
        System.out.println("\n=== BIBLIOTECA COMPLETA ===");
        System.out.println("+------+------------------------------------------------------+");

        List<Musicas> todas = controller.carregarBiblioteca();
        for (int i = 0; i < todas.size(); i++) {
            Musicas m = todas.get(i);
            String info = m.toCompactString();
            if (m.getAno() > 0) {
                info += " (" + m.getAno() + ")";
            }
            System.out.printf("| %2d   | %-52s |\n", i + 1, truncar(info, 52));
        }
        System.out.println("+------+------------------------------------------------------+");

        esperarEnter();
    }

    private void voltarParaBiblioteca() {
        List<Musicas> biblioteca = controller.carregarBiblioteca();
        playlistAtual = biblioteca;
        controller.setPlaylistAtual(playlistAtual);
        System.out.println(">> Voltou para biblioteca geral - " + playlistAtual.size() + " musicas!");
        esperarEnter();
    }

    private void listarPlaylists() {
        List<Playlists> playlists = controller.listarPlaylists();

        System.out.println("\n=== PLAYLISTS SALVAS ===");
        if (playlists.isEmpty()) {
            System.out.println("  Nenhuma playlist encontrada.");
        } else {
            for (Playlists p : playlists) {
                System.out.printf("  [%d] %s\n", p.getId_playlist(), p.getNome_playlist());
            }
        }

        esperarEnter();
    }

    private void criarPlaylist() {
        System.out.print("\n>> Nome da playlist: ");
        String nome = scanner.nextLine();

        if (!nome.isEmpty()) {
            controller.criarPlaylist(nome);
            System.out.println(">> Playlist criada com sucesso!");
        } else {
            System.out.println("!! Nome invalido!");
        }

        esperarEnter();
    }

    private void carregarPlaylist() {
        List<Playlists> playlists = controller.listarPlaylists();

        if (playlists.isEmpty()) {
            System.out.println("!! Nenhuma playlist disponivel!");
            esperarEnter();
            return;
        }

        System.out.println("\n=== PLAYLISTS DISPONIVEIS ===");
        for (Playlists p : playlists) {
            System.out.printf("  [%d] %s\n", p.getId_playlist(), p.getNome_playlist());
        }

        System.out.print("\n>> Digite o ID da playlist: ");
        int id = scanner.nextInt();
        scanner.nextLine();

        List<Musicas> playlistCarregada = controller.carregarPlaylist(id);

        if (!playlistCarregada.isEmpty()) {
            controller.setPlaylistAtual(playlistCarregada);
            System.out.println(">> Playlist carregada com " + playlistCarregada.size() + " musicas!");
        } else {
            System.out.println("!! Playlist vazia ou nao encontrada!");
        }

        esperarEnter();
    }

    private void adicionarMusicaNaPlaylist() {
        List<Musicas> todasMusicas = controller.carregarBiblioteca();

        if (todasMusicas.isEmpty()) {
            System.out.println("!! Nenhuma musica disponivel na biblioteca!");
            esperarEnter();
            return;
        }

        System.out.println("\n=== MUSICAS DISPONIVEIS ===");
        for (Musicas m : todasMusicas) {
            System.out.printf("  [%d] %s\n", m.getId_musica(), m.toString());
        }

        System.out.print("\n>> ID da musica: ");
        int idMusica = scanner.nextInt();
        scanner.nextLine();

        List<Playlists> playlists = controller.listarPlaylists();

        if (playlists.isEmpty()) {
            System.out.println("!! Nenhuma playlist disponivel! Crie uma primeiro.");
            esperarEnter();
            return;
        }

        System.out.println("\n=== PLAYLISTS DISPONIVEIS ===");
        for (Playlists p : playlists) {
            System.out.printf("  [%d] %s\n", p.getId_playlist(), p.getNome_playlist());
        }

        System.out.print("\n>> ID da playlist: ");
        int idPlaylist = scanner.nextInt();
        scanner.nextLine();

        List<Musicas> musicasNaPlaylist = controller.carregarPlaylist(idPlaylist);
        int proximaOrdem = musicasNaPlaylist.size() + 1;

        controller.adicionarMusicaNaPlaylist(idPlaylist, idMusica, proximaOrdem);
        System.out.println(">> Musica adicionada a playlist!");
        esperarEnter();
    }

    private void mostrarInfo() {
        System.out.println("\n+===============================================+");
        System.out.println("|              ATALHOS DO PLAYER                |");
        System.out.println("+===============================================+");
        System.out.println("|  play / p     > Tocar/Pausar/Retomar musica   |");
        System.out.println("|  pause        > Pausar musica                 |");
        System.out.println("|  next / n     > Proxima musica                |");
        System.out.println("|  prev / b     > Musica anterior               |");
        System.out.println("|  stop / s     > Parar musica                  |");
        System.out.println("|  list / l     > Listar biblioteca             |");
        System.out.println("|  autoplay     > Ativar/Desativar AutoPlay     |");
        System.out.println("|  random       > Ativar/Desativar Aleatorio    |");
        System.out.println("|  playlists    > Listar playlists              |");
        System.out.println("|  criar        > Criar nova playlist           |");
        System.out.println("|  carregar     > Carregar playlist             |");
        System.out.println("|  add          > Adicionar musica a playlist   |");
        System.out.println("|  voltar / v   > Voltar a playlist geral       |");
        System.out.println("|  rescan / r   > Reescanear pasta              |");
        System.out.println("|  info / i     > Mostrar esta ajuda            |");
        System.out.println("|  quit / q     > Sair do programa              |");
        System.out.println("+===============================================+");

        esperarEnter();
    }

    private void esperarEnter() {
        System.out.print("\n>> Pressione Enter para continuar...");
        scanner.nextLine();
    }

    private String truncar(String texto, int tamanho) {
        if (texto == null) {
            return "";
        }
        if (texto.length() <= tamanho) {
            return texto;
        }
        return texto.substring(0, tamanho - 3) + "...";
    }
}
