package model;

public class Playlists {
    
    private int id_playlist;
    private String nome_playlist;
    private String data_criacao;

    // Construtor completo com ID (para consultar do banco)
    public Playlists(int id_playlist, String nome_playlist, String data_criacao) {
        this.id_playlist = id_playlist;
        this.nome_playlist = nome_playlist;
        this.data_criacao = data_criacao;
    }
    
    // Construtor sem ID e sem data (para criar nova playlist)
    public Playlists(String nome_playlist) {
        this.nome_playlist = nome_playlist;
    }
    
    // Construtor apenas com ID e nome (útil para listagens)
    public Playlists(int id_playlist, String nome_playlist) {
        this.id_playlist = id_playlist;
        this.nome_playlist = nome_playlist;
    }

    public int getId_playlist() {
        return id_playlist;
    }

    public String getNome_playlist() {
        return nome_playlist;
    }

    public String getData_criacao() {
        return data_criacao;
    }

    public void setId_playlist(int id_playlist) {
        this.id_playlist = id_playlist;
    }

    public void setNome_playlist(String nome_playlist) {
        this.nome_playlist = nome_playlist;
    }

    public void setData_criacao(String data_criacao) {
        this.data_criacao = data_criacao;
    }
    
    // Método toString para exibição
    @Override
    public String toString() {
        return nome_playlist + " (ID: " + id_playlist + ")";
    }
    
    // Método para exibir de forma simples
    public String toSimpleString() {
        return nome_playlist;
    }
}