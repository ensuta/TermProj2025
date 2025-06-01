package shootingspaceship;

import javax.sound.sampled.*;
import java.io.File;
import java.io.IOException;

public class MusicPlayer {
    private static Clip clip;

    public static void playLoop(String filepath) {
        try {
            File soundFile = new File(filepath);
            AudioInputStream audioIn = AudioSystem.getAudioInputStream(soundFile); // 파일 -> 오디오데이터
            clip = AudioSystem.getClip();
            clip.open(audioIn); // 클립에 오디오 넣기
            clip.loop(Clip.LOOP_CONTINUOUSLY); // 무한 반복
            clip.start(); // 재생 시작
        } catch (UnsupportedAudioFileException | IOException | LineUnavailableException e) {
            e.printStackTrace(); // 에러나면 출력
        }
    }
}
