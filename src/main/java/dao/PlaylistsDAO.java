package dao;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import model.Musicas;
import model.Playlists;
import util.conexao;

public class PlaylistsDAO {

    private final Connection conn;

    public PlaylistsDAO() {
        this.conn = new conexao().getConnection();
    }

    public int adicionarPlaylist(Playlists p) {
        String sql = "INSERT INTO playlists(nome_playlist) VALUES (?)";
        
        try (PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            stmt.setString(1, p.getNome_playlist());
            stmt.executeUpdate();
            
            ResultSet rs = stmt.getGeneratedKeys();
            if (rs.next()) {
                return rs.getInt(1);
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return -1;
    }

    public void removerPlaylist(Playlists p) {
        String sql = "DELETE FROM playlists WHERE id_playlist=?";
        
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, p.getId_playlist());
            stmt.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Erro ao deletar: " + e.getMessage());
        }
    }

    public boolean editarPlaylist(Playlists p) {
        String sql = "UPDATE playlists SET nome_playlist = ? WHERE id_playlist = ?";
        
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, p.getNome_playlist());
            stmt.setInt(2, p.getId_playlist());
            
            int linhas = stmt.executeUpdate();
            return linhas > 0;
        } catch (SQLException e) {
            throw new RuntimeException("Erro ao editar: " + e.getMessage());
        }
    }

    public Playlists buscarPorIdPlaylists(int id) {
        String sql = "SELECT * FROM playlists WHERE id_playlist = ?";
        
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, id);
            ResultSet rs = stmt.executeQuery();
            
            if (rs.next()) {
                return new Playlists(
                        rs.getInt("id_playlist"),
                        rs.getString("nome_playlist"),
                        rs.getString("data_criacao")
                );
            }
        } catch (SQLException e) {
            throw new RuntimeException("Erro ao buscar ID: " + e.getMessage());
        }
        return null;
    }

    public List<Playlists> listarPlaylists() {
        String sql = "SELECT * FROM playlists";
        List<Playlists> lista = new ArrayList<>();
        
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                Playlists p = new Playlists(
                        rs.getInt("id_playlist"),
                        rs.getString("nome_playlist"),
                        rs.getString("data_criacao")
                );
                lista.add(p);
            }
        } catch (SQLException e) {
            throw new RuntimeException("Erro ao listar: " + e.getMessage());
        }
        return lista;
    }

    public void adicionarMusicaNaPlaylist(int idPlaylist, int idMusica, int ordem) {
        String sql = "INSERT INTO playlist_musicas (id_playlist, id_musica, ordem_na_lista) VALUES (?, ?, ?)";
        
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, idPlaylist);
            stmt.setInt(2, idMusica);
            stmt.setInt(3, ordem);
            stmt.execute();
        } catch (SQLException e) {
            throw new RuntimeException("Erro ao vincular música: " + e.getMessage());
        }
    }

    public List<Musicas> listarMusicasDaPlaylist(int idPlaylist) {
        List<Musicas> lista = new ArrayList<>();
        String sql = "SELECT m.* FROM musicas m "
                + "JOIN playlist_musicas pm ON m.id_musica = pm.id_musica "
                + "WHERE pm.id_playlist = ? "
                + "ORDER BY pm.ordem_na_lista";
        
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, idPlaylist);
            ResultSet rs = stmt.executeQuery();
            
            while (rs.next()) {
                Musicas m = new Musicas(
                        rs.getInt("id_musica"),
                        rs.getString("titulo"),
                        rs.getString("artista"),
                        rs.getString("album"),
                        rs.getInt("ano"),
                        rs.getString("genero"),
                        rs.getString("duracao"),
                        rs.getString("caminho_arquivo")
                );
                lista.add(m);
            }
        } catch (SQLException e) {
            throw new RuntimeException("Erro ao listar músicas da playlist: " + e.getMessage());
        }
        return lista;
    }

    public void removerMusicaDaPlaylist(int idPlaylist, int idMusica) {
        String sql = "DELETE FROM playlist_musicas WHERE id_playlist = ? AND id_musica = ?";
        
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, idPlaylist);
            stmt.setInt(2, idMusica);
            stmt.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Erro ao remover música da playlist: " + e.getMessage());
        }
    }
}