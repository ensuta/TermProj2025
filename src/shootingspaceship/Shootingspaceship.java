package shootingspaceship;

import java.awt.*;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.event.*;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.*;
import java.util.List;
import javax.swing.Timer;

public class Shootingspaceship extends JPanel implements Runnable {//게임클래스

	private List<Bomb> activeBombs = new ArrayList<>();
    private Player player;
    private Shot[] shots;
    private ArrayList<Enemy> enemies;
    private Boss boss = null;

    private Thread th;
    private int enemySize;
    private javax.swing.Timer timer;
    private boolean playerMoveLeft;
    private boolean playerMoveRight;
    private Image dbImage;
    private Graphics dbg;
    private Random rand;
    private StageManager stageManager;

    //각종 파라미터
    private final int width = 500;
    private final int height = 500;
    //플레이어
    private final int shotSpeed = -2;
    private int maxShotNum = 10000;
    private final int playerMargin = 10;
    private final int playerLeftSpeed = -2;
    private final int playerRightSpeed = 2;
    //적
    private final int enemyMaxDownSpeed = 1;
    private final int enemyMaxHorizonSpeed = 1;
    private final float enemyDownSpeedInc = 0.5f;//적수직속도 증가량
    //적 난이도?
    private final int enemyTimeGap = 500;
    private final int maxEnemySize = 20;
    // 보스 등장 관련
    private boolean bossAppear = false;
    private int bossThreshold;

    // 총알 연사 관련
    private boolean shooting = false;
    private long lastShotTime = 0;
    private int shotInterval = 50; // 총알 발사 간격
    
    //배경 이미지
    private Image backgroundImg;
    
    long lastBombTime = 0;
    long bombInterval = 1500; // 1.5초
    //폭탄 객체 리스트(clearbomb도 같은 리스트 사용)
    protected List<Bomb> bombs = new ArrayList<>();
    
    //2페이지 바람 방해 패턴 변수
    private boolean showWindEffect = false;
    private long windEffectEndTime = 0;
    //3페이지 연기, 장애물 패턴 변수
    protected List<Debris> debrisList = new ArrayList<>();
    private SmokeEffect smokeEffect;
    
    //마지막 스테이지 바람 공격

    public Shootingspaceship() {//생성자
    	boss = new BombardiroCrocodilo(100, 50);
        stageManager = new StageManager(); 
        shots = new Shot[ maxShotNum ]; 
        enemies = new ArrayList<Enemy>(); 
        enemySize = 0; 
        rand = new Random(1); 
        timer = new javax.swing.Timer(enemyTimeGap, new addANewEnemy()); 
        timer.start(); 
        addKeyListener(new ShipControl()); 
        setFocusable(true); 
        bossThreshold = stageManager.getEnemyCountForStage(); // 보스등장조건


        setBackground(Color.black); // background color
        setPreferredSize(new Dimension(width, height)); // game size
        player = new Player(width / 2, (int) (height * 0.9), playerMargin, width-playerMargin ); // 플레이어 생성

        try {
        	backgroundImg = ImageIO.read(getClass().getResource("/shootingspaceship/Image/gamesky.jpg"));
        } catch (IOException e) {
        	e.printStackTrace();
        }
        
        
    }

    public void start() {//루프시작
        th = new Thread(this);
        th.start();
    }

        private class ShipControl implements KeyListener {
        public void keyPressed(KeyEvent e) {
            switch (e.getKeyCode()) {
                case KeyEvent.VK_LEFT:
                    playerMoveLeft = true;
                    break;
                case KeyEvent.VK_RIGHT:
                    playerMoveRight = true;
                    break;
                case KeyEvent.VK_UP:
                    shooting = true;
                    break;
            }
        }

        public void keyReleased(KeyEvent e) {
            switch (e.getKeyCode()) {
                case KeyEvent.VK_LEFT:
                    playerMoveLeft = false;
                    break;
                case KeyEvent.VK_RIGHT:
                    playerMoveRight = false;
                    break;
                case KeyEvent.VK_UP:
                    shooting = false;
                    break;
            }
        }

        public void keyTyped(KeyEvent e) {
        }
    }


    private class addANewEnemy implements ActionListener {//적 생성
        public void actionPerformed(ActionEvent e) {
            if (!bossAppear && enemySize < maxEnemySize) {
                // 적 생성
                //속도설정
                float downspeed;
                do {
                    downspeed = rand.nextFloat() * enemyMaxDownSpeed;
                } while (downspeed == 0);
                float horspeed = rand.nextFloat() * 2 * enemyMaxHorizonSpeed - enemyMaxHorizonSpeed;

                //최종출력
                Enemy newEnemy = new Enemy((int) (rand.nextFloat() * width), 0, horspeed, downspeed, width, height, enemyDownSpeedInc);
                enemies.add(newEnemy);
                ++enemySize;
                
                
            }
        }
    }
    private void spawnBoss() {
    	// 스테이지별 이미지 설정
    	int stage = stageManager.getCurrentStage();
    	// 이미지 깨질 때 대신 나오는 이미지
    	String bossImagePath = "missing.png";
    	// stage 별 나올 보스 이미지
    	switch (stage) {
    	case 1:
    		bossImagePath = "shark_128x128.png";
    		break;
    	case 2:
    		bossImagePath = "crocodiro.png";
    		
    		break;
    	case 3:
    		bossImagePath = "tung.png";
    		break;
    	default:
    		bossImagePath = "missing.png";
    		break;
    	}
    	
        boss = new Boss(width / 2, 50, 0.5f, stageManager.getBossSpeedForStage(), width, height, 0.05f, bossImagePath, stage);
        boss.setHealth(stageManager.getBossHealthForStage());
        
        bossAppear = true;
    }

    @Override
    public void run() { //루프
        Thread.currentThread().setPriority(Thread.MIN_PRIORITY);

        while (true) {
        	long currentTime = System.currentTimeMillis();
        	
        	// 폭탄 생성 조건 (보스가 존재하고, 일정 시간 간격마다)
            if (boss != null && currentTime - lastBombTime > bombInterval) {
                Bomb bomb = new Bomb(boss.getX(), boss.getY(), 3);
                bombs.add(boss.shootBomb());
                lastBombTime = currentTime;
            }
         
            repaint();
            try {
                Thread.sleep(16); // 약 60fps
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        	
        	
        	// 총알 이동, 밖으로 나간 총알 제거
            if (shooting) {
                long now = System.currentTimeMillis();
                if (now - lastShotTime > shotInterval) {
                    for (int i = 0; i < shots.length; i++) {
                        if (shots[i] == null) {
                            shots[i] = player.generateShot();
                            lastShotTime = now;
                            break;
                        }
                    }
                }
            }
            // 총알 이동, 밖으로 나간 총알 제거
            for (int i = 0; i < shots.length; i++) {
                if (shots[i] != null) {
                    shots[i].moveShot(shotSpeed);
                    if (shots[i].getY() < 0) {
                        shots[i] = null;
                    }
                }
            }

            //이동 처리
            if (playerMoveLeft) {
                player.moveX(playerLeftSpeed);
            } else if (playerMoveRight) {
                player.moveX(playerRightSpeed);
            }
            boolean needClearEnemies = false;
            Iterator<Enemy> enemyList = enemies.iterator();

            while (enemyList.hasNext()) {
                Enemy enemy = enemyList.next();
                enemy.move();
                //적제거
                if (enemy.isCollidedWithShot(shots)) {
                    enemyList.remove();
                    if (!bossAppear) {
                        --bossThreshold;
                        --enemySize;
                        System.out.println("남은 보스 등장 처치 조건: " + bossThreshold);
                        //보스등장
                        if (bossThreshold <= 0 && !bossAppear) {
                            needClearEnemies = true;
                            spawnBoss();
                            timer.stop();
                            break; 
                        }
                    }
                }
                // 게임종료
                if (enemy.isCollidedWithPlayer(player)) {
                    enemyList.remove();
                    JOptionPane.showMessageDialog(this, "게임오버: 플레이어와 충돌");
                    System.exit(0);
                }
                if (enemy.getY() >= height) {
                    JOptionPane.showMessageDialog(this, "게임오버: 적이 화면 아래에 도달");
                    System.exit(0);
                }
                //폭탄 공격
                for (int i = 0; i < bombs.size(); i++) {
                    Bomb bomb = bombs.get(i);
                    bomb.update();
                    
                    if (bomb.isCollidedWithPlayer(player)) {
                        JOptionPane.showMessageDialog(this, "게임오버: 폭탄이 플레이어와 충돌");
                        System.exit(0);
                    }

                    if (!bomb.isActive()) {
                        bombs.remove(i);
                        i--; // 리스트 요소 제거 후 인덱스 보정
                    }
                }
                //장애물 패턴
                for(Debris d : debrisList) {
                	 if(player.getBounds().intersects(d.getBounds())) {
                		 System.out.println("Debris와 충돌!");
                		//플레이어 위치 되돌리기 또는 데미지
                		 player.setX(player.getPrevX());
                		 player.setY(player.getPrevY());
                		 break;
                	 }
                }
                

            }
            if (needClearEnemies) {
                enemies.clear();
                enemySize = 0;
            }
            
            if (boss != null) {
                boss.move();

                // 보스와 총알 충돌 시 체력 감소 및 보스 처치 처리
                if (boss.isCollidedWithShot(shots)) {
                    if (boss.getHealth() <= 0) {
                        boss = null;
                        bossAppear = false;
                        System.out.println("보스 처치!");

                        // 다음 스테이지로 진행
                        if (!stageManager.isFinalStage()) {
                            stageManager.advanceStage();
                            JOptionPane.showMessageDialog(this, "다음 스테이지로 진행: " + stageManager.getCurrentStage());
                            bossThreshold = stageManager.getEnemyCountForStage();
                            enemySize = 0;
                            enemies.clear();
                            timer.start();
                        } else {
                            // 마지막 스테이지 클리어 시
                            JOptionPane.showMessageDialog(this, "게임 클리어!");
                            repaint();
                        }
                        continue;
                    }
                }

                // 게임종료
                if (boss.isCollidedWithPlayer(player)) {
                    JOptionPane.showMessageDialog(this, "게임오버: 보스가 플레이어와 충돌");
                    System.exit(0);
                }
                if (boss.getY() >= height) {
                    JOptionPane.showMessageDialog(this, "게임오버: 보스가 화면 아래에 도달");
                    System.exit(0);
                }
            }

            repaint();
            try {
                Thread.sleep(5); 
            } catch (InterruptedException ex) {
            }

            Thread.currentThread().setPriority(Thread.MAX_PRIORITY);
        }
    }
    
    //2페이지 바람 방해 패턴
    public void spawnWindEffect() {
        showWindEffect = true;
        windEffectEndTime = System.currentTimeMillis() + 3000;
    }
    
    
    
    //화면 내 enemy 처치하는 ClearBomb
    public void keyPressed(KeyEvent e) {
        int key = e.getKeyCode();

        if (key == KeyEvent.VK_B) { //B키로 폭탄 사용
        	player.useScreenClearBomb(enemies, bombs);   //기존의 적, 적폭탄 리스트 전달
        }

        // 기존 이동 키 처리 등...
    }
    

    public void initImage(Graphics g) {
        if (dbImage == null) {
            dbImage = createImage(this.getSize().width, this.getSize().height);
            dbg = dbImage.getGraphics();
        }
        dbg.setColor(getBackground());
        dbg.fillRect(0, 0, this.getSize().width, this.getSize().height);

        dbg.setColor(getForeground());

        g.drawImage(dbImage, 0, 0, this);
    }


    public void paintComponent(Graphics g) {
    	super.paintComponent(g);
        // 각종 그리기
        initImage(g);
        player.drawPlayer(g);
        Iterator<Enemy> enemyList = enemies.iterator();
        while (enemyList.hasNext()) {
            Enemy enemy = enemyList.next();
            enemy.draw(g);
            
        }
        for (int i = 0; i < bombs.size(); i++) {
            if (bombs.get(i) != null) {
                bombs.get(i).drawBomb(g);
            }
        }
        for (int i = 0; i < shots.length; i++) {
            if (shots[i] != null) {
                shots[i].drawShot(g);
            }
        }
        if (boss != null) {
            boss.draw(g);
        }
        
        if(showWindEffect) {
        	if(System.currentTimeMillis() > windEffectEndTime) {
        		showWindEffect = false;
        	}
        }
        if (stageManager.isFinalStage()) {
        	if(smokeEffect != null) {
        		smokeEffect.draw(g);
        	}
        }
        if(debrisList != null) {
        	for(Debris d : debrisList) {
        		d.draw(g);
        	}
        }
        //마지막 스테이지 장애물(debris)
        if(stageManager.getCurrentStage() == 5 && debrisList.isEmpty()) {
        	Image debrisImage = stageManager.loadImage("debris.png");
        	//왼쪽 벽
        	for(int y=0; y<getHeight(); y+=60) {
        		debrisList.add(new Debris(0, y, 40, 40, debrisImage));
        	}
        	//오른쪽 벽
        	for(int y=30; y<getHeight(); y+=60) {
        		debrisList.add(new Debris(getWidth()-40, y, 40, 40, debrisImage));
        	}
        	//위쪽 벽
        	for(int x=60; x<getWidth()-60; x+=100) {
        		debrisList.add(new Debris(x, 0, 40, 40, debrisImage));
        	}
        	for(int x=60; x<getWidth()-60; x+=100) {
        		debrisList.add(new Debris(x, getHeight()-40, 40, 40, debrisImage));
        	}
        }
        //debris 그리기
        if(stageManager.isFinalStage()) {
        	if(smokeEffect != null) {
        		smokeEffect.draw(g);
        	}
        	for(Debris d : debrisList) {
        		d.draw(g);
        	}
        }
        
        
        if(stageManager.getCurrentStage() == stageManager.getMaxStage()) {
        	g.setColor(new Color(135, 206, 250, 128));
        	g.fillRect(0, 0, getWidth(), getHeight());
        	g.setColor(Color.BLUE);
        	g.drawString("강풍!", getWidth() / 2 - 20, 50);
        	
       }
        // 스테이지 정보
        g.setColor(Color.WHITE);
        g.drawString("Stage: " + stageManager.getCurrentStage(), 10, 20);
        g.drawString("Bombs: " + player.getScreenBombCount(), 10, 40);
    }


    public static void main(String[] args) {
        JFrame frame = new JFrame("Shooting");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        Shootingspaceship ship = new Shootingspaceship();
        frame.getContentPane().add(ship);
        frame.setResizable(false);
        frame.pack();
        frame.setVisible(true);
        frame.setLocationRelativeTo(null);
        ship.start();
    }
}
