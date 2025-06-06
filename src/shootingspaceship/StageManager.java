package shootingspaceship;

import javax.imageio.ImageIO;
import javax.swing.ImageIcon;

import java.io.*;
import java.awt.Graphics;
import java.awt.Image;

public class StageManager {
    //스테이지 설정
    private int currentStage = 1; 
    private int maxStage = 5;
    private int lastMusicStage = -1; // 마지막 재생된 스테이지 음악

    // 각 스테이지별 설정값
    private final int[] enemiesPerStage = {5, 10, 15, 20, 25}; 
    private final int[] bossHealthPerStage = {20, 30, 40, 50, 60}; 
    private final float[] bossSpeedPerStage = {0.2f, 0.4f, 0.6f, 0.7f, 0.8f}; 
    
    //배경 이미지
    private Image backgroundImage;
    
    public StageManager() {
        try {
            backgroundImage = ImageIO.read(getClass().getResource("/shootingspaceship/Image/gamesky.jpg"));
        } catch (IOException e) {
            e.printStackTrace();
        }
        // 스테이지 1 음악 강제 재생
        MusicPlayer.playStageMusic(currentStage);
        lastMusicStage = currentStage;  // 기록-중복재생안되게
    }
    
    public Image loadImage(String fileName) {
        return new ImageIcon(getClass().getResource("/shootingspaceship/Image/" + fileName)).getImage();
    }
    
    public int getCurrentStage() {
        return currentStage; 
    }

    public int getEnemyCountForStage() {
        return enemiesPerStage[currentStage - 1]; 
    }

    public int getBossHealthForStage() {
        return bossHealthPerStage[currentStage - 1]; 
    }

    public float getBossSpeedForStage() {
        return bossSpeedPerStage[currentStage - 1]; 
    }

    public boolean isFinalStage() {
        return currentStage == maxStage; 
    }

    public boolean advanceStage() {
        if (currentStage < maxStage) {
            currentStage++;
            playStageMusicOnce(); // 다음 스테이지 음악 한 번만 재생
            return true;
        } else {
            return false;
        }
    }
    
    public void drawBackground(Graphics g) {
        if (backgroundImage != null) {
            g.drawImage(backgroundImage, 0, 0, null);
        }
    }

    // ⭐ 스테이지 음악을 한 번만 재생하는 함수
    private void playStageMusicOnce() {
        if (currentStage != lastMusicStage) {
            MusicPlayer.playStageMusic(currentStage);
            lastMusicStage = currentStage;
        }
    }
}
