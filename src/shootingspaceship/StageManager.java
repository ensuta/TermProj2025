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

    // 각 스테이지별 설정값
    private final int[] enemiesPerStage = {5, 10, 15, 20, 25}; 
    private final int[] bossHealthPerStage = {20, 30, 40, 50, 60}; //스테이지 별 보스 피 
    private final float[] bossSpeedPerStage = {0.2f, 0.4f, 0.6f, 0.7f, 0.8f}; //스테이지 별 보스 속도
    
    //배경 이미지
    private Image backgroundImage;
    
    public StageManager() {
    	try {
    		backgroundImage = ImageIO.read(getClass().getResource("/shootingspaceship/Image/gamesky.jpg"));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    
    public Image loadImage(String fileName) {
    	return new ImageIcon(getClass().getResource("/shootingspaceship/Image/" + fileName)).getImage();
    }
    //추가기능
    public int getMaxStage() {
        return maxStage;
    }
    
    public int getCurrentStage() {
        return currentStage; 
    }

    public int getEnemyCountForStage() {
        return enemiesPerStage[currentStage - 1]; //스테이지 넘어갈 때마다 bossthreshold 값 증가
    }

    public int getBossHealthForStage() {
        return bossHealthPerStage[currentStage - 1]; //스테이지 넘어갈 때마다 보스 피 늘어나게 하는 함수
    }

    public float getBossSpeedForStage() {
        return bossSpeedPerStage[currentStage - 1]; //스테이지 넘어갈 때마다 보스 빨라지게 하는 함수
    }

    public boolean isFinalStage() {
        return currentStage == maxStage; //마지막 스테이지인지 확인하는 함수
    }

    public boolean advanceStage() {
        if (currentStage < maxStage) { //다음 스테이지로
            currentStage++;
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
}
