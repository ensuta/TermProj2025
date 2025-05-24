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
	
	private boolean page3Started = false;   //페이지3 변수들
	private boolean smokeActive = false;
	private boolean debrisActive = false;
//	private List<Debris> debrisList = new ArrayList<>();// 만들면
//	private SmokeEffect smokeEffect;// 되는거
	private final int GOAL_X = 700; // 예시 골 좌표
	
	private int frameCount = 0;
	private int positionState = 0; // 0: 오른쪽, 1: 왼쪽 상단, 2: 아래 등
	private Random random = new Random();
	
	
	public BombardiroCrocodilo(int x, int y) {
		super(x, y, 0.5f, 1.0f, 800, 600, 0.05f, "image_crocodiro-removebg-preview (1).png", 1);
	}
	//입에서 폭탄 발사하는 메소드
	private List<Bomb> activeBombs = new ArrayList<>();
	
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
	
/*	private void updateBombsS_1() {
		for(Bomb bomb : activeBombs) {
			bomb.update();
		}
	}
	public void drawBombs(Graphics g) {
		for(Bomb bomb : activeBombs) {
			bomb.draw(g);
		}
	}
	
	
	//바람 불어서 방해하는 메소드
	public class WindAttack {
	    private int x, y;
	    private int forceWind;

	    public WindAttack(int x, int y, int force) {
	        this.x = x;
	        this.y = y;
	        this.forceWind = forceWind;
	    }

	    public void applyEffect(Player player) {
	        player.push(forceWind); // push는 플레이어 클래스에서 정의할 메서드
	    }

	    public void draw(Graphics g) {
	        g.setColor(new Color(135, 206, 250, 128)); // 투명한 바람 색
	        g.fillRect(x, y, 100, 20); // 예시: 바람 시각화
	    }
	}
	//2페이지 돌진 공격
	private void performChargeAttack() {
		if(!isCharging) {
			isCharging = true;
			chargeX = this.x;
		}
		this.x -= 10;
		//화면 밖으로 나가면 복귀
		if(this.x + size < 0) {
			this.x = chargeX; //원위치복귀
			isCharging = false;
		}
	}
	//위치 이동
	private void performRepositionMovement() {
		if(!isMoving) {
			isMoving = true;
			moveStage = 0;
		}
		switch(moveStage) {
		case 0:
			y -= 5;
			if(y < 100) {
				moveStage++;
				break;
			}
		case 1:
			x -= 5;
			if(x < 300) {
				moveStage++;
				break;
			}
		case 2:
			y += 5;
			if(y >= 300) {
				isMoving = false;
			}
			break;
		}
	}
	//x축 낙하 폭탄
	private void performFallingBombAttack() {
		bombXDropTimer++;
		
		if(bombXDropTimer % 30 == 0 && bombXDropIndex < dropPositions.length) {
			int dropX = dropPositions[bombXDropIndex];
			Bomb fallingBomb = new Bomb(dropX, y, 0, 5);
			activeBombs.add(fallingBomb);
			bombXDropIndex++;
		}
		if(bombXDropIndex >= dropPositions.length) {
			bombXDropTimer = 0;
			bombXDropIndex = 0;
		}
	}
	//3페이지 
	private void startPage3Sequence() {
		//폭탄 터짐 이펙트
	    triggerBigExplosionEffect(x, y);
	    //연기 나기시작
	    smokeEffect = new SmokeEffect();
	    smokeActive = true;
	    //파편 생성
	    debrisList.clear();
	    for (int i = 0; i < 10; i++) {
	    	int startX = 400; // 중심 폭발 위치
	    	int startY = 300;
	    	double angle = Math.random() * 2 * Math.PI;
	        double speed = 4 + Math.random() * 3;
	        debrisList.add(new Debris(startX, startY, Math.cos(angle) * speed, Math.sin(angle) * speed));
	    }
	    debrisActive = true;
	}
	//연기 기능
	private void updateSmoke(Player player) {
		if(smokeEffect != null) {
			smokeEffect.spread();
		if(smokeEffect.affects(player)) {
			player.setVisionObstructed(true);
		} else {
			player.setVisionObstructed(false);
		}
		}
	}
	//파편 기능
	private void updateDebris(Player player) {
		Iterator<Debris> iterator = debrisList.iterator();
		
		while(iterator.hasNext()) {
			Debris debris = iterator.next();
			debris.move();
			
			if(debris.collidesWith(player)) {
				player.takeDamage(1);
				iterator.remove();
			}
			if(debris.isOutOfBounds()) {
				iterator.remove();
			}
		}
	}
	//골인확인
	private void checkPlayerGoalReached(Player player) {
		if(goalArea != null) {
			player.setStageCleared(true);
			endStage();  
		}
		else if(goalArea.contains(player.getX(), player.getY())) {
			player.setStageCleared(true);
			endStage();
		}
	}
	
	private void endStage() {
		smokeActive = false;
		debrisActive = false;
		
		smokeEffect = null;
		debrisList.clear();
		
		this.setAlive(false);
		
		System.out.println("Stage Cleared!");
		
		triggerStageClearEvent();

		}
	
	private void triggerStageClearEvent() {
		// TODO Auto-generated method stub
		
	}
	private void setAlive(boolean b) {
		// TODO Auto-generated method stub
		
	}
	// 예시: 200프레임마다 패턴 전환
/*	if(FrameCount % 200 == 0) {
		int pattern = random.nextInt(3);
		if(pattern == 0) {
			startCharge();
		}
		else if(pattern == 1) {
			changePosition();
		}
		else {
			startBombAttack();
		}
	}  
	
	private void updateBombs() {
		Iterator<Bomb> iterator = activeBombs.iterator();
		while(iterator.hasNext()) {
			Bomb bomb = iterator.next();
			bomb.moveShot(2);
			if(!bomb.beingActive() || bomb.getY() > 600) {
				iterator.remove();
			}
		}
	}
	
	//시간 지남에 따라 공격패턴 업데이트
	@Override
	public void updatePattern(Player player) {
		updateBombsS1();
		switch(page) {
		case 1:
			handlePage1Pattern(player);
			break;
		case 2:
			handlePage2Pattern(player);
			break;
		case 3:
			handlePage3Pattern(player);
			break;
		}
	}

	
	
	
	
	//페이지 1 공격
	private void handlePage1Pattern(Player player) {
		
		if(canShootBomb()) {
			List<Bomb> bombs = shootVerticalBombs();
			
			Bomb parryableBomb = chooseRandomBomb(bombs);
			if(parryableBomb != null) {
				parryableBomb.setParryable(true);
	            parryableBomb.setColor(Color.CYAN);
			}
			
			activeBombs.addAll(bombs);
		}
		if(WindAttack()) {
			player.pushBack(windPower);
		}
	        
		if(shouldSpawnMinions()) {
			spawnMinion("suicide");
	        spawnMinion("replicator");
	        spawnMinion("normal");
		}  
	}
	private Bomb chooseRandomBomb(List<Bomb> bombs) {
		// TODO Auto-generated method stub
		return null;
	}
	private List<Bomb> shootVerticalBombs() {
		// TODO Auto-generated method stub
		return null;
	}
	private boolean canShootBomb() {
		// TODO Auto-generated method stub
		return false;
	}
	private boolean WindAttack() {
		// TODO Auto-generated method stub
		return false;
	}
	private void spawnMinion(String string) {
		// TODO Auto-generated method stub
		
	}
	private boolean shouldSpawnMinions() {
		// TODO Auto-generated method stub
		return false;
	}
	//페이지 2 공격
	private void handlePage2Pattern(Player player) {
		
		page2Timer++;
		    // 패턴 전환 (3초마다)
		if(page2Timer - patternSwitchTimer > 180) {
			
			currentPatternIndex = (currentPatternIndex + 1) % 3;
		    patternSwitchTimer = page2Timer;
	    }

		    switch (currentPatternIndex) {
		        case 0:
		            performChargeAttack(); // 돌진
		            break;
		        case 1:
		            performRepositionMovement(); // 이동 패턴
		            break;
		        case 2:
		            performFallingBombAttack(); // x축 낙하 폭탄
		            break;
		    }
	}
	//페이지 3 공격
	private void handlePage3Pattern(Player player) {
		
		if (!page3Started) { //3페이지 시작여부
	        startPage3Sequence();
	        page3Started = true;
	    }

	    if (smokeActive) {  //연기효과 활성화
	        updateSmoke(player);
	    }

	    if (debrisActive) {  //파편 움직이기
	        updateDebris(player);
	    } 

	    checkPlayerGoalReached(player); //플레이어가 목표지점 도달 확인
		 
	}
*/
}