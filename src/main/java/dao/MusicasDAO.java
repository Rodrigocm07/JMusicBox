package dao;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import model.Musicas;
import util.conexao;

public class MusicasDAO {

    private final Connection conn;

    public MusicasDAO() {
        this.conn = new conexao().getConnection();
    }

    public int adicionarMusica(Musicas m) {
        String sql = "INSERT INTO musicas(titulo, artista, album, ano, genero, duracao, caminho_arquivo) VALUES (?,?,?,?,?,?,?)";
        
        try (PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            stmt.setString(1, m.getTitulo());
            stmt.setString(2, m.getArtista());
            stmt.setString(3, m.getAlbum());
            stmt.setInt(4, m.getAno());
            stmt.setString(5, m.getGenero());
            stmt.setString(6, m.getDuracao());
            stmt.setString(7, m.getCaminho_arquivo());
            
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

    public void removerMusica(Musicas m) {
        String sql = "DELETE FROM musicas WHERE id_musica=?";
        
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, m.getId_musica());
            stmt.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Erro ao deletar: " + e.getMessage());
        }
    }

    public boolean editarMusica(Musicas m) {
        String sql = "UPDATE musicas SET titulo = ?, artista = ?, album = ?, ano = ?, genero = ?, duracao = ?, caminho_arquivo = ? WHERE id_musica = ?";
        
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, m.getTitulo());
            stmt.setString(2, m.getArtista());
            stmt.setString(3, m.getAlbum());
            stmt.setInt(4, m.getAno());
            stmt.setString(5, m.getGenero());
            stmt.setString(6, m.getDuracao());
            stmt.setString(7, m.getCaminho_arquivo());
            stmt.setInt(8, m.getId_musica());
            
            int linhas = stmt.executeUpdate();
            return linhas > 0;
        } catch (SQLException e) {
            throw new RuntimeException("Erro ao editar: " + e.getMessage());
        }
    }

    public Musicas buscarPorIdMusicas(int id) {
        String sql = "SELECT * FROM musicas WHERE id_musica = ?";
        
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, id);
            ResultSet rs = stmt.executeQuery();
            
            if (rs.next()) {
                return new Musicas(
                        rs.getInt("id_musica"),
                        rs.getString("titulo"),
                        rs.getString("artista"),
                        rs.getString("album"),
                        rs.getInt("ano"),
                        rs.getString("genero"),
                        rs.getString("duracao"),
                        rs.getString("caminho_arquivo")
                );
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return null;
    }

    public List<Musicas> listarMusicas() {
        String sql = "SELECT * FROM musicas ORDER BY artista, titulo";
        List<Musicas> lista = new ArrayList<>();
        
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
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
            throw new RuntimeException("Erro ao listar: " + e.getMessage());
        }
        return lista;
    }
}