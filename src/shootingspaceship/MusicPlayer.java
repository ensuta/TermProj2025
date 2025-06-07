package shootingspaceship;

import javax.sound.sampled.*;
import java.io.File;
import java.io.IOException;

public class MusicPlayer {
    private static Clip bgClip;       // 배경음악용 클립
    private static Clip effectClip;   // 스테이지 효과음용 클립

    // 🔁 배경음악 무한 반복
    public static void playBackgroundMusic(String filepath) {
        stopBackgroundMusic(); // 중복 방지
        try {
            File soundFile = new File(filepath);
            AudioInputStream audioIn = AudioSystem.getAudioInputStream(soundFile);
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
            case 1 -> "sounds/트랄라레오트랄랄라.wav";
            case 2 -> "sounds/봄바르딜로 크로코딜로.wav";
            case 3 -> "sounds/퉁사후르.wav";
            case 4 -> "sounds/니릴리라릴라.wav";
            default -> null;
        };

        if (filename != null) {
            try {
                File soundFile = new File(filename);
                AudioInputStream audioIn = AudioSystem.getAudioInputStream(soundFile);
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


