package shootingspaceship;

import javax.sound.sampled.*;
import java.io.File;
import java.io.IOException;

public class SoundPlayer {

    public static void playSound(String soundFilePath) {
        try {
        	// 소리 파일 읽기
            AudioInputStream audioIn = AudioSystem.getAudioInputStream(new File(soundFilePath));
            Clip clip = AudioSystem.getClip();
            clip.open(audioIn); // 클립에 소리 데이터 넣음
            clip.start(); // 재생 시작
        } catch (Exception e) { // 에러 발생시 출력
            e.printStackTrace();
        }
    }
}

