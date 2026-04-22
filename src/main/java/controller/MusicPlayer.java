package controller;

import javazoom.jl.player.Player;
import javax.sound.sampled.*;
import java.io.*;

public class MusicPlayer {
    private static Player mp3Player;
    private static Clip audioClip;
    private static boolean tocando = false;
    private static boolean pausado = false;
    private static String caminhoAtual = "";
    private static long posicaoPausa = 0; // Para MP3 (não suporta seek facilmente)
    private static long microsegundosPausa = 0; // Para WAV
    private static FileInputStream mp3Stream;
    
    public static void tocar(String caminho) {
        parar();
        caminhoAtual = caminho;
        pausado = false;
        
        try {
            String extensao = caminho.toLowerCase();
            
            if (extensao.endsWith(".mp3")) {
                mp3Stream = new FileInputStream(caminho);
                mp3Player = new Player(mp3Stream);
                new Thread(() -> {
                    try {
                        mp3Player.play();
                        tocando = false;
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }).start();
                tocando = true;
                
            } else if (extensao.endsWith(".wav") || extensao.endsWith(".aiff") || 
                       extensao.endsWith(".aif") || extensao.endsWith(".au") || 
                       extensao.endsWith(".snd")) {
                AudioInputStream ais = AudioSystem.getAudioInputStream(new File(caminho));
                audioClip = AudioSystem.getClip();
                audioClip.open(ais);
                audioClip.start();
                tocando = true;
            }
            
        } catch (Exception e) {
            System.out.println("❌ Erro ao tocar: " + e.getMessage());
        }
    }
    
    public static void pausar() {
        if (!tocando || pausado) return;
        
        try {
            if (mp3Player != null) {
                // MP3: JLayer não suporta pause nativo
                // Salvamos a referência mas não conseguimos retomar do mesmo ponto
                mp3Player.close();
                mp3Player = null;
                System.out.println("⚠️  MP3 não suporta retomada exata. Reiniciando do começo ao dar play.");
            }
            
            if (audioClip != null && audioClip.isRunning()) {
                microsegundosPausa = audioClip.getMicrosecondPosition();
                audioClip.stop();
                pausado = true;
                tocando = false;
                System.out.println("⏸ Pausado em: " + formatarTempo(microsegundosPausa));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    
    public static void retomar() {
        if (!pausado) return;
        
        try {
            if (audioClip != null) {
                audioClip.setMicrosecondPosition(microsegundosPausa);
                audioClip.start();
                pausado = false;
                tocando = true;
                System.out.println("▶ Retomando de: " + formatarTempo(microsegundosPausa));
            } else if (caminhoAtual != null && caminhoAtual.endsWith(".mp3")) {
                // MP3: Não suporta retomada, reinicia do começo
                tocar(caminhoAtual);
                System.out.println("▶ Reiniciando MP3 do começo");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    
    public static void parar() {
        if (mp3Player != null) {
            mp3Player.close();
            mp3Player = null;
        }
        if (mp3Stream != null) {
            try {
                mp3Stream.close();
            } catch (IOException e) {}
            mp3Stream = null;
        }
        if (audioClip != null) {
            audioClip.stop();
            audioClip.close();
            audioClip = null;
        }
        tocando = false;
        pausado = false;
        microsegundosPausa = 0;
    }
    
    public static long getPosicaoAtual() {
        if (audioClip != null && audioClip.isOpen()) {
            return audioClip.getMicrosecondPosition();
        }
        if (pausado) {
            return microsegundosPausa;
        }
        return 0;
    }
    
    public static long getDuracaoTotal() {
        if (audioClip != null && audioClip.isOpen()) {
            return audioClip.getMicrosecondLength();
        }
        return 0;
    }
    
    public static boolean isTocando() {
        if (mp3Player != null) {
            return !mp3Player.isComplete();
        }
        if (audioClip != null) {
            return audioClip.isRunning();
        }
        return false;
    }
    
    public static boolean isPausado() {
        return pausado;
    }
    
    public static String getCaminhoAtual() {
        return caminhoAtual;
    }
    
    private static String formatarTempo(long microsegundos) {
        long segundos = microsegundos / 1_000_000;
        long minutos = segundos / 60;
        segundos = segundos % 60;
        return String.format("%02d:%02d", minutos, segundos);
    }
}