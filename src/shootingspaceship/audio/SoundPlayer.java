package shootingspaceship.audio;

import javax.sound.sampled.*;
import java.io.IOException;
import java.io.InputStream;
import java.io.BufferedInputStream;

public class SoundPlayer {

    public static void playSound(String soundFilePath) {
        try {
        	// 소리 파일 읽기
            InputStream audioSrc = SoundPlayer.class.getResourceAsStream(soundFilePath);
            if (audioSrc == null) {
                throw new IOException("Resource not found: " + soundFilePath);
            }
            AudioInputStream audioIn = AudioSystem.getAudioInputStream(new BufferedInputStream(audioSrc));
            Clip clip = AudioSystem.getClip();
            clip.open(audioIn); // 클립에 소리 데이터 넣음
            clip.start(); // 재생 시작
        } catch (Exception e) { // 에러 발생시 출력
            e.printStackTrace();
        }
    }
}

