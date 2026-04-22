package model;

public class Musicas {

    private int id_musica;
    private String titulo;
    private String artista;
    private String album;
    private int ano;
    private String genero;
    private String duracao;
    private String caminho_arquivo;

    // Construtor completo com ID (para consultar do banco)
    public Musicas(int id_musica, String titulo, String artista, String album, 
                   int ano, String genero, String duracao, String caminho_arquivo) {
        this.id_musica = id_musica;
        this.titulo = titulo;
        this.artista = artista;
        this.album = album;
        this.ano = ano;
        this.genero = genero;
        this.duracao = duracao;
        this.caminho_arquivo = caminho_arquivo;
    }

    // Construtor sem ID (para novas músicas - salvar no banco)
    public Musicas(String titulo, String artista, String album, int ano, 
                   String genero, String duracao, String caminho_arquivo) {
        this.titulo = titulo;
        this.artista = artista;
        this.album = album;
        this.ano = ano;
        this.genero = genero;
        this.duracao = duracao;
        this.caminho_arquivo = caminho_arquivo;
    }

    // Construtor simplificado (compatibilidade com código antigo)
    public Musicas(int id_musica, String titulo, String artista, String album, 
                   String duracao, String caminho_arquivo) {
        this.id_musica = id_musica;
        this.titulo = titulo;
        this.artista = artista;
        this.album = album;
        this.ano = 0;
        this.genero = "Desconhecido";
        this.duracao = duracao;
        this.caminho_arquivo = caminho_arquivo;
    }

    // Construtor simplificado sem ID (compatibilidade)
    public Musicas(String titulo, String artista, String album, 
                   String duracao, String caminho_arquivo) {
        this.titulo = titulo;
        this.artista = artista;
        this.album = album;
        this.ano = 0;
        this.genero = "Desconhecido";
        this.duracao = duracao;
        this.caminho_arquivo = caminho_arquivo;
    }

    // Getters e Setters
    public int getId_musica() {
        return id_musica;
    }

    public void setId_musica(int id_musica) {
        this.id_musica = id_musica;
    }

    public String getTitulo() {
        return titulo;
    }

    public void setTitulo(String titulo) {
        this.titulo = titulo;
    }

    public String getArtista() {
        return artista;
    }

    public void setArtista(String artista) {
        this.artista = artista;
    }

    public String getAlbum() {
        return album;
    }

    public void setAlbum(String album) {
        this.album = album;
    }

    public int getAno() {
        return ano;
    }

    public void setAno(int ano) {
        this.ano = ano;
    }

    public String getGenero() {
        return genero;
    }

    public void setGenero(String genero) {
        this.genero = genero;
    }

    public String getDuracao() {
        return duracao;
    }

    public void setDuracao(String duracao) {
        this.duracao = duracao;
    }

    public String getCaminho_arquivo() {
        return caminho_arquivo;
    }

    public void setCaminho_arquivo(String caminho_arquivo) {
        this.caminho_arquivo = caminho_arquivo;
    }

    // Método toString para exibição
    @Override
    public String toString() {
        return artista + " - " + titulo + " [" + duracao + "]";
    }

    // Método para exibir de forma compacta
    public String toCompactString() {
        return artista + " - " + titulo;
    }
    
    // Método para exibição completa com ano e gênero
    public String toFullString() {
        StringBuilder sb = new StringBuilder();
        sb.append(artista).append(" - ").append(titulo);
        if (ano > 0) {
            sb.append(" (").append(ano).append(")");
        }
        if (!genero.equals("Desconhecido")) {
            sb.append(" [").append(genero).append("]");
        }
        sb.append(" - ").append(duracao);
        return sb.toString();
    }
}