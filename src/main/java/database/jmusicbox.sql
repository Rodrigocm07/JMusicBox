CREATE DATABASE IF NOT EXISTS JMusicBox;
USE JMusicBox;

-- 1. Tabela de Músicas - COM ANO E GÊNERO
CREATE TABLE IF NOT EXISTS musicas (
    id_musica INT AUTO_INCREMENT PRIMARY KEY,
    titulo VARCHAR(150) NOT NULL,
    artista VARCHAR(150) DEFAULT 'Desconhecido',
    album VARCHAR(150),
    ano INT DEFAULT 0,
    genero VARCHAR(100) DEFAULT 'Desconhecido',
    duracao VARCHAR(10),
    caminho_arquivo VARCHAR(255) NOT NULL UNIQUE
);

-- 2. Tabela de Playlists
CREATE TABLE IF NOT EXISTS playlists (
    id_playlist INT AUTO_INCREMENT PRIMARY KEY,
    nome_playlist VARCHAR(100) NOT NULL,
    data_criacao TIMESTAMP DEFAULT CURRENT_TIMESTAMP()
);

-- 3. Tabela de relacionamento Playlist-Músicas
CREATE TABLE IF NOT EXISTS playlist_musicas (
    id_playlist INT NOT NULL,
    id_musica INT NOT NULL,
    ordem_na_lista INT,
    PRIMARY KEY (id_playlist, id_musica),
    CONSTRAINT fk_playlist FOREIGN KEY (id_playlist) REFERENCES playlists(id_playlist) ON DELETE CASCADE,
    CONSTRAINT fk_musica FOREIGN KEY (id_musica) REFERENCES musicas(id_musica) ON DELETE CASCADE
);
select * from musicas;
select * from playlists;
select * from playlist_musicas;