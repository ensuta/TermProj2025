package shootingspaceship;

import java.awt.Graphics;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Random;
import java.awt.Color;

public class BombardiroCrocodilo extends Boss {
	
	
	private boolean isCharging = false; //돌진에 필요한 변수
	private int chargeSpeed = 5;
	private int mouthX, mouthY;
	
	private boolean isMoving = false;  //2페이지 위치이동
	private int moveStage = 0;
	
	private int bombXDropTimer = 0; //x축 낙하폭탄
	private int bombXDropIndex = 0;
	private final int[] dropPositions = {150, 250, 350, 450, 550};
	
	
//	private SmokeEffect smokeEffect;// 만들면 되는거
	private final int GOAL_X = 700; // 예시 골 좌표
	
	private int frameCount = 0;
	private int positionState = 0; // 0: 오른쪽, 1: 왼쪽 상단, 2: 아래 등
	private Random random = new Random();
	
	//입에서 폭탄 발사하는 메소드
	private List<Bomb> activeBombs = new ArrayList<>();
	//2페이지 바람공격
	private boolean windAttackTriggered = false;
	private static final int someThreshold = 50; // 예시: 체력 50 이하일 때 발동
	private Player player;
	private Runnable windEffectCallback;
	//3페이지 연기, 장애물 공격
	protected List<Debris> debrisList = new ArrayList<>();
	private SmokeEffect smokeEffect;
	
	public BombardiroCrocodilo(int x, int y) {
		super(x, y, 0.5f, 1.0f, 800, 600, 0.05f, "crocodiro.png", 1);
	}
	
	public void attack(List<Bomb> activeBombs) {
		MouthBombbreathe(activeBombs);
	}
	
	public void MouthBombbreathe(List<Bomb> activeBombs) {
		List<Bomb> bombs = new ArrayList<>();
		
		for(int i=0; i<5; i++) {
			bombs.add(new Bomb(mouthX, mouthY + (i * 20), 5));
		}
		
		int index = new Random().nextInt(bombs.size());
        Bomb parryableBomb = bombs.get(index);
        parryableBomb.setParryable(true);
        parryableBomb.setColor(Color.CYAN);
        
        activeBombs.addAll(bombs);
	}
	
	public void updatePhase(int currentHealth, int currentStage) {
	    if (currentHealth < someThreshold && currentStage >= 2 && !windAttackTriggered) {
	        triggerWindAttack();
	        windAttackTriggered = true;
	    }
	}
	
	public void triggerWindAttack() {
	    System.out.println("바람 공격 시작!");
	    player.freeze(2000);         // 2초 동안 정지
	    windEffectCallback.run();    // GUI에 바람 이펙트 나타나게 함
	}
}
