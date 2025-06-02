package shootingspaceship;

import javax.imageio.ImageIO;
import java.io.IOException;
import java.awt.*;
import javax.swing.*;
import java.awt.event.*;
import java.util.*;
import java.util.List;

public class Shootingspaceship extends JPanel implements Runnable {
    private Player player;
    private Shot[] shots;
    private ArrayList<Enemy> enemies;
    private Boss boss = null;
    private Thread th;
    private int enemySize;
    private javax.swing.Timer timer;
    private boolean playerMoveLeft;
    private boolean playerMoveRight;
    protected boolean playerMoveUp;		
    protected boolean playerMoveDown;	
    private Image dbImage;
    private Graphics dbg;
    private Random rand;
    private StageManager stageManager;
    private CharacterType selectedCharacter; 

    //각종 파라미터
    private final int width = 1280;
    private final int height = 720;
    //플레이어
    private final int shotSpeed = -5; // 총알 자체의 y축 이동 속도
    private int maxShotNum = 10000;
    private final int playerMargin = 10;
    private int currentPlayerMoveSpeed;
    private int currentShotInterval;
    
    //적
    private final int enemyMaxDownSpeed = 1;
    private final int enemyMaxHorizonSpeed = 2;
    private final float enemyDownSpeedInc = 0.5f;
    //적 난이도?
    private final int enemyTimeGap = 500;
    private final int maxEnemySize = 20;
    // 보스 등장 관련
    private boolean bossAppear = false;
    private int bossThreshold;

    // 총알 연사 관련
    private boolean shooting = false;
    private long lastShotTime = 0;

    //배경 이미지
    private Image backgroundImg;
    
  //폭탄 객체 리스트(clearbomb도 같은 리스트 사용)
    protected List<Bomb> bombs = new ArrayList<>();
    private List<Bomb> activeBombs = new ArrayList<>();
    //폭탄 관련 변수
    long lastBombTime = 0;
    long bombInterval = 1500; // 1.5초
    long currentTime = System.currentTimeMillis();
    //폭탄 사용 변수
    private boolean useBombTriggered = false;
    List<Shot> bossShots = new ArrayList<>();

  //2페이지 바람 방해 패턴 변수
    private boolean showWindEffect = false;
    private long windEffectEndTime = 0;
    
    private final float[] enemySpeedPerStage = {1.0f, 1.5f, 2.0f, 2.5f, 3.0f};

    public float getEnemySpeedForStage() {
        return enemySpeedPerStage[stageManager.getCurrentStage() - 1];
    }
    
    public Shootingspaceship(CharacterType selectedCharacterFromMain) {
        this.selectedCharacter = selectedCharacterFromMain;
        currentPlayerMoveSpeed = this.selectedCharacter.moveSpeed;
        currentShotInterval = this.selectedCharacter.shotInterval; 
        stageManager = new StageManager();
        shots = new Shot[maxShotNum];
        enemies = new ArrayList<Enemy>(); 
        enemySize = 0; 
        rand = new Random(1); 
        timer = new javax.swing.Timer(enemyTimeGap, new addANewEnemy()); 
        timer.start(); 
        addKeyListener(new ShipControl()); 
        setFocusable(true);
        requestFocusInWindow();
        addFocusListener(new FocusAdapter() {
            @Override
            public void focusLost(FocusEvent e) {
                shooting = false;
                playerMoveLeft = false;
                playerMoveRight = false;
                playerMoveUp = false;
                playerMoveDown = false;
            }
        });
        bossThreshold = stageManager.getEnemyCountForStage();
        setPreferredSize(new Dimension(width, height));

        player = new Player(width / 2, (int) (height * 0.9), playerMargin, width-playerMargin,  0, height-playerMargin, this.selectedCharacter.bulletDamage);

        try {
            backgroundImg = ImageIO.read(getClass().getResource("/shootingspaceship/Image/gamesky.jpg"));
        } catch (IOException e) {
            e.printStackTrace();
        }
        setBackground(Color.black); 
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
                	playerMoveUp = true;
                	break;
                case KeyEvent.VK_DOWN:
                	playerMoveDown = true;
                	break;
                case KeyEvent.VK_Z:
                    shooting = true;
                    break;
                case KeyEvent.VK_B:  
                	useBombTriggered = true;
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
                	playerMoveUp = false;
                	break;
                case KeyEvent.VK_DOWN:
                	playerMoveDown = false;
                	break;
                case KeyEvent.VK_Z:
                    shooting = false;
                    break;
            }
        }

        public void keyTyped(KeyEvent e) {}
    }


    private class addANewEnemy implements ActionListener {
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
    	case 4:
    		bossImagePath = "lirili_larila.png";
    		break;
    	default:
    		bossImagePath = "missing.png";
    		break;
    	}
        boss = new Boss(width / 2, 50, 0.5f, stageManager.getBossSpeedForStage(), width, height, 0.05f, bossImagePath, stage);
        boss.setHealth(stageManager.getBossHealthForStage());
        
        bossAppear = true;
    }

    
    public void run() { 
        Thread.currentThread().setPriority(Thread.MIN_PRIORITY);

        while (true) {
            if (shooting) {
                long now = System.currentTimeMillis();
                if (now - lastShotTime > currentShotInterval) { 
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
         
            repaint();
            try {
                Thread.sleep(1); // 수정부분
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            
          
            if (useBombTriggered && player.getScreenBombCount() > 0) {
                useBombTriggered = false; // 다시 false로 꺼줌 (1번만 발동되도록)
                player.useScreenBomb(); // 폭탄 1회 차감

                // 적 제거
                enemies.clear();

                // 보스 탄 등 제거
                activeBombs.clear();  // 화면에 떠 있는 폭탄이 있다면
                bossShots.clear();    // 보스 공격 등도 제거할 수 있음
            }


            // 수평 이동
            if (playerMoveLeft && !playerMoveRight) {
                player.moveX(-currentPlayerMoveSpeed); 
            } else if (playerMoveRight && !playerMoveLeft) {
                player.moveX(currentPlayerMoveSpeed);  
            }
            // 수직 이동
            if (playerMoveUp && !playerMoveDown) {
                player.moveY(-currentPlayerMoveSpeed); 
            } else if (playerMoveDown && !playerMoveUp) {
                player.moveY(currentPlayerMoveSpeed); 
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
                        System.out.println("보스 등장까지 남은 처치 수: " + bossThreshold);
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
                
            }
            if (needClearEnemies) {
                enemies.clear();
                enemySize = 0;
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
    
    

    public void initImage(Graphics g) {
        if (dbImage == null) {
            dbImage = createImage(this.getSize().width, this.getSize().height);
            dbg = dbImage.getGraphics();
        }
        dbg.setColor(getBackground());
        dbg.fillRect(0, 0, this.getSize().width, this.getSize().height);

        dbg.setColor(getForeground());

        // 배경 이미지를 먼저 그리기 위해, dbg에 그린 후 마지막에 g.drawImage로 출력합니다.
        if (backgroundImg != null) {
            dbg.drawImage(backgroundImg, 0, 0, this.getSize().width, this.getSize().height, this);
        } else { // 배경 이미지가 로드되지 않은 경우 검은색으로 채움
            dbg.setColor(Color.BLACK);
            dbg.fillRect(0, 0, this.getSize().width, this.getSize().height);
        }
    }


    public void paintComponent(Graphics g) {
        // 더블 버퍼링을 위한 이미지 준비
        if (dbImage == null) {
            dbImage = createImage(this.getSize().width, this.getSize().height);
            dbg = dbImage.getGraphics();
        }

        if (backgroundImg != null) {
            dbg.drawImage(backgroundImg, 0, 0, this.getSize().width, this.getSize().height, this);
        } else {
            dbg.setColor(Color.BLACK);
            dbg.fillRect(0, 0, this.getSize().width, this.getSize().height);
        }
        
        player.drawPlayer(dbg);
        Iterator<Enemy> enemyList = enemies.iterator();
        while (enemyList.hasNext()) {
            Enemy enemy = enemyList.next();
            enemy.draw(dbg);
        }
        for (int i = 0; i < shots.length; i++) {
            if (shots[i] != null) {
                shots[i].drawShot(dbg);
            }
        }
        if (boss != null) {
            boss.draw(dbg);
        }

        for (int i = 0; i < bombs.size(); i++) {
            if (bombs.get(i) != null) {
                bombs.get(i).drawBomb(dbg);
            }
        }
        
        if(showWindEffect) {
            if(System.currentTimeMillis() > windEffectEndTime) {
                showWindEffect = false;
            }
        }
        
        //추가기능(플레이어가 쓰는 폭탄 개수)
        dbg.setColor(Color.YELLOW);
        dbg.setFont(new Font("Arial", Font.BOLD, 16));
        dbg.drawString("Bombs Left: " + player.getScreenBombCount(), 0, 40);
        
        if(stageManager.getCurrentStage() == stageManager.getMaxStage()) {
            dbg.setColor(new Color(135, 206, 250, 128));
            dbg.fillRect(0, 0, getWidth(), getHeight());
            dbg.setColor(Color.BLUE);
            dbg.drawString("wind!", getWidth() / 2 - 20, 50);
            
       }
        // 스테이지 정보
        dbg.setColor(Color.WHITE);
        dbg.drawString("Stage: " + stageManager.getCurrentStage(), 10, 20);
        g.drawImage(dbImage, 0, 0, this);
    }


    public static void main(String[] args) {

        CharacterType[] characterOptions = CharacterType.values();
        CharacterType chosenCharacter = (CharacterType) JOptionPane.showInputDialog(
                null, 
                "플레이할 캐릭터를 선택하세요:", 
                "캐릭터 선택",
                JOptionPane.PLAIN_MESSAGE, 
                null, // icon
                characterOptions, 
                characterOptions[0] 
        );
        JFrame frame = new JFrame("Shooting");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        Shootingspaceship ship = new Shootingspaceship(chosenCharacter);
        frame.getContentPane().add(ship);
        frame.pack();
        frame.setVisible(true);
        ship.requestFocusInWindow(); 
        ship.start();
    }
}

