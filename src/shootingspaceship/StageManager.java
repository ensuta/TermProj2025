package shootingspaceship;

public class StageManager {
    private int currentStage = 1; //현재 스테이지
    private final int maxStage = 4; //마지막 스테이지

    // 각 스테이지별 설정값
    private final int[] enemiesPerStage = {3, 5, 7, 10}; //스테이지 별 적 수
    private final int[] bossHealthPerStage = {20, 30, 40, 50}; //스테이지 별 보스 피 
    private final float[] bossSpeedPerStage = {0.2f, 0.4f, 0.6f, 0.8f}; //스테이지 별 보스 속도

    public int getCurrentStage() {
        return currentStage; 
    }

    public int getEnemyCountForStage() {
        return enemiesPerStage[currentStage - 1]; //스테이지 넘어갈 때마다 적 수 많아지게 하는 함수
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
        if (currentStage < maxStage) { //스테이지 값 추가되도
            currentStage++;
            return true;
        } else {
            return false;
        }
    }
}