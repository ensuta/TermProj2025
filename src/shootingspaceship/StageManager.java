package shootingspaceship;

import javax.swing.ImageIcon;
import java.awt.Graphics;
import java.awt.Image;

public class StageManager {
    //스테이지 설정
    private int currentStage = 1; 
    private int maxStage = 4;
    private int lastMusicStage = -1; // 마지막 재생된 스테이지 음악

    // 각 스테이지별 설정값
    private final int[] enemiesPerStage = {5, 5, 5, 20}; 
    private final int[] bossHealthPerStage = {1, 1, 1, 35}; //스테이지 별 보스 피 
    private final float[] bossSpeedPerStage = {0.2f, 0.4f, 0.6f, 0.8f}; //스테이지 별 보스 속도
    
    //배경 이미지
    private Image backgroundImage;
    
    public StageManager() {
        // 스테이지 1 음악 강제 재생
        MusicPlayer.playStageMusic(currentStage);
        lastMusicStage = currentStage;  // 기록-중복재생안되게
    }

    private final String[] backgroundImagePaths = {
    	    "bbeach.png",
    	    "bsky.png",
    	    "btung.png",
    	    "bdessert.png"
    };
    
    public String getBackgroundImagePathForStage() {
        return backgroundImagePaths[currentStage - 1];
    }

    public Image getBackgroundImage() {
        if (backgroundImage == null) {
            loadBackgroundImage();
        }
        return backgroundImage;
    }
    
    public Image loadBackgroundImage() {
        String path = getBackgroundImagePathForStage();
        java.net.URL imgURL = getClass().getResource("/shootingspaceship/image/"+path);
        if (imgURL == null) {
            System.err.println("이미지 경로를 찾을 수 없습니다: " + path);
            return null;
        }
        backgroundImage = new ImageIcon(imgURL).getImage();  // 필드에 할당
        return backgroundImage;
    }
    public int getMaxStage() { // 최대 스테이지 가져오기
        return maxStage;
    }
    
    public int getCurrentStage() {  // 현재 스테이지 가져오기
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
            loadBackgroundImage();
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
    
    public String[] getEnemyImagePathForStage() { //스테이지 별 적 이미지
    	switch(currentStage) {
    	case 1: return new String[]{"trala_R.png","trala_L.png"};
    	case 2: return new String[]{"babycroco_L.png","babycroco_L.png"};
    	case 3: return new String[]{"tung_R.png","tung_L.png"};
    	case 4: return new String[]{"riril_R.png","riril_L.png"};
    	default: return new String[]{"trala_R.png","trala_L.png"};
    	}
    }
    public String[] getBossImagePathForStage() { //스테이지 별 적 이미지
    	switch(currentStage) {
    	case 1: return new String[]{"tralaboss_R.png","tralaboss_L.png"};
    	case 2: return new String[]{"crocoboss.png","crocoboss.png"};
    	case 3: return new String[]{"tungboss_R.png","tungboss_L.png"};
    	case 4: return new String[]{"ririlboss_R.png","ririlboss_L.png"};
    	default: return new String[]{"tralaboss_R.png","tralaboss_L.png"};
    	}
    }
}
