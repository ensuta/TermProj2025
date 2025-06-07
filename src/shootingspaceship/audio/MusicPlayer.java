package shootingspaceship.audio;

import javax.sound.sampled.*;
import java.io.IOException;
import java.io.InputStream;
import java.io.BufferedInputStream;

public class MusicPlayer {
    private static Clip bgClip;       // 배경음악용 클립
    private static Clip effectClip;   // 스테이지 효과음용 클립

    // 🔁 배경음악 무한 반복
    public static void playBackgroundMusic(String filepath) {
        stopBackgroundMusic(); // 중복 방지
        try {
            InputStream audioSrc = MusicPlayer.class.getResourceAsStream(filepath);
            if (audioSrc == null) {
                throw new IOException("Resource not found: " + filepath);
            }
            AudioInputStream audioIn = AudioSystem.getAudioInputStream(new BufferedInputStream(audioSrc));
            bgClip = AudioSystem.getClip();
            bgClip.open(audioIn);
            bgClip.loop(Clip.LOOP_CONTINUOUSLY);
            bgClip.start();
        } catch (UnsupportedAudioFileException | IOException | LineUnavailableException e) {
            e.printStackTrace();
        }
    }

    // 배경음악 멈추기 (필요할 경우)
    public static void stopBackgroundMusic() {
        if (bgClip != null && bgClip.isRunning()) {
            bgClip.stop();
            bgClip.close();
        }
    }

    // 🔊 스테이지 음악 한 번만 재생
    public static void playStageMusic(int stage) {
        stopEffectClip(); // 이전 효과음 멈추기

        String filename = switch (stage) {
            case 1 -> "/shootingspaceship/resources/sounds/트랄라레오트랄랄라.wav";
            case 2 -> "/shootingspaceship/resources/sounds/봄바르딜로 크로코딜로.wav";
            case 3 -> "/shootingspaceship/resources/sounds/퉁사후르.wav";
            case 4 -> "/shootingspaceship/resources/sounds/니릴리라릴라.wav";
            default -> null;
        };

        if (filename != null) {
            try {
                InputStream audioSrc = MusicPlayer.class.getResourceAsStream(filename);
                if (audioSrc == null) {
                    throw new IOException("Resource not found: " + filename);
                }
                AudioInputStream audioIn = AudioSystem.getAudioInputStream(new BufferedInputStream(audioSrc));
                effectClip = AudioSystem.getClip();
                effectClip.open(audioIn);
                effectClip.start(); // ❗한 번만 재생
            } catch (UnsupportedAudioFileException | IOException | LineUnavailableException e) {
                e.printStackTrace();
            }
        }
    }

    // 스테이지 효과음 멈추기
    private static void stopEffectClip() {
        if (effectClip != null && effectClip.isRunning()) {
            effectClip.stop();
            effectClip.close();
        }
    }
}


