package shootingspaceship;

import javax.imageio.ImageIO;
import java.io.IOException;
import java.awt.*;
import javax.swing.*;
import java.awt.event.*;
import java.util.*;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList; // 스레드 안전한 리스트 사용

public class Shootingspaceship extends JPanel implements Runnable {
    private Player player;
    private Shot[] shots;
    private CopyOnWriteArrayList<Enemy> enemies; // 게임 내 모든 적들
    private Boss boss = null;
    private Thread th; // 게임 루프 스레드
    private int enemySize; // 현재 화면에 있는 적의 수
    private javax.swing.Timer timer; // 적 생성 타이머
    private boolean playerMoveLeft;
    private boolean playerMoveRight;
    protected boolean playerMoveUp;
    protected boolean playerMoveDown;
    private Image dbImage; // 더블 버퍼링용 백버퍼 이미지
    private Graphics dbg; // 백버퍼 이미지에 그릴 Graphics 객체
    private Random rand;
    private StageManager stageManager;
    private CharacterType selectedCharacter;

    // 각종 파라미터
    private final int width = 1280;
    private final int height = 720;
    private final int shotSpeed = -5;
    private int maxShotNum = 10000;
    private final int playerMargin = 10;
    private int currentPlayerMoveSpeed;
    private int currentShotInterval;

    // 적 관련 파라미터
    private final int enemyMaxDownSpeed = 1;
    private final int enemyMaxHorizonSpeed = 2;
    private final float enemyDownSpeedInc = 0.5f;
    private final int enemyTimeGap = 500;
    private final int maxEnemySize = 20;
    private boolean bossAppear = false;
    private int bossThreshold; // 보스 등장을 위한 적 처치 카운트

    private boolean shooting = false;
    private long lastShotTime = 0;

    private Image backgroundImg;

    protected CopyOnWriteArrayList<Bomb> bombs = new CopyOnWriteArrayList<>();
    private CopyOnWriteArrayList<Bomb> activeBombs = new CopyOnWriteArrayList<>();
    long lastBombTime = 0;
    long bombInterval = 1500;
    long currentTime = System.currentTimeMillis();
    private boolean useBombTriggered = false; // 폭탄 사용 트리거 플래그
    List<Shot> bossShots = new CopyOnWriteArrayList<>();

    // 2페이지 바람 방해 패턴 변수
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
        MusicPlayer.playBackgroundMusic("sounds/backgroundmusic.wav");
        enemies = new CopyOnWriteArrayList<>();
        enemySize = 0;
        rand = new Random(1);
        timer = new javax.swing.Timer(enemyTimeGap, new addANewEnemy()); // 적 생성 타이머 설정
        timer.start();
        addKeyListener(new ShipControl());
        setFocusable(true);
        requestFocusInWindow();
        addFocusListener(new FocusAdapter() {
            @Override
            public void focusLost(FocusEvent e) { // 포커스를 잃으면 이동 및 발사 중지
                shooting = false;
                playerMoveLeft = false;
                playerMoveRight = false;
                playerMoveUp = false;
                playerMoveDown = false;
                
                setBackground(Color.black);
            }
        });
        bossThreshold = stageManager.getEnemyCountForStage();
        setPreferredSize(new Dimension(width, height));

        player = new Player(width / 2, (int) (height * 0.9), playerMargin, width - playerMargin, 0, height - playerMargin, this.selectedCharacter.bulletDamage);

        
    }

    public void start() {
        th = new Thread(this); // 게임 루프 스레드 시작
        th.start();
    }

    private class ShipControl implements KeyListener {
        public void keyPressed(KeyEvent e) {
            switch (e.getKeyCode()) {
                case KeyEvent.VK_LEFT: playerMoveLeft = true; break;
                case KeyEvent.VK_RIGHT: playerMoveRight = true; break;
                case KeyEvent.VK_UP: playerMoveUp = true; break;
                case KeyEvent.VK_DOWN: playerMoveDown = true; break;
                case KeyEvent.VK_Z: shooting = true; break;
                case KeyEvent.VK_B:
                    useBombTriggered = true;
                    // 폭탄 로직을 새 스레드에서 처리하여 논블로킹 실행
                    new Thread(() -> {
                        if (player.getScreenBombCount() > 0) {
                            player.useScreenBomb();
                            // 적, 활성화된 폭탄, 보스 총알 리스트 클리어
                            enemies.clear();
                            activeBombs.clear();
                            bossShots.clear();
                        }
                    }).start();
                    break;
            }
        }

        public void keyReleased(KeyEvent e) {
            switch (e.getKeyCode()) {
                case KeyEvent.VK_LEFT: playerMoveLeft = false; break;
                case KeyEvent.VK_RIGHT: playerMoveRight = false; break;
                case KeyEvent.VK_UP: playerMoveUp = false; break;
                case KeyEvent.VK_DOWN: playerMoveDown = false; break;
                case KeyEvent.VK_Z: shooting = false; break;
            }
        }

        public void keyTyped(KeyEvent e) {}
    }


    private class addANewEnemy implements ActionListener {
        public void actionPerformed(ActionEvent e) {
            // 보스 미등장 & 최대 적 수 미달 시 새 적 생성
            if (!bossAppear && enemySize < maxEnemySize) {
                float downspeed;
                do { downspeed = rand.nextFloat() * enemyMaxDownSpeed; } while (downspeed == 0);
                float horspeed = rand.nextFloat() * 2 * enemyMaxHorizonSpeed - enemyMaxHorizonSpeed;

                Enemy newEnemy = new Enemy((int) (rand.nextFloat() * width), 0, horspeed, downspeed, width, height, enemyDownSpeedInc);
                enemies.add(newEnemy); // CopyOnWriteArrayList는 스레드 안전
                ++enemySize;
            }
        }
    }

    private void spawnBoss() { // 스테이지에 맞는 보스 생성
        int stage = stageManager.getCurrentStage();

        boss = new Boss(width / 2, 50, 0.5f, stageManager.getBossSpeedForStage(), width, height, 0.05f, stage);
        boss.setBossImage(stageManager.getBossImagePathForStage());
        boss.setHealth(stageManager.getBossHealthForStage());
        bossAppear = true;
    }


    public void run() { // 게임 루프
        Thread.currentThread().setPriority(Thread.MIN_PRIORITY);

        while (true) {
            if (shooting) { // 플레이어 총알 발사 처리
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
            // 플레이어 총알 이동 및 화면 밖 제거
            for (int i = 0; i < shots.length; i++) {
                if (shots[i] != null) {
                    shots[i].moveShot(shotSpeed);
                    if (shots[i].getY() < 0) {
                        shots[i] = null;
                    }
                }
            }

            // 플레이어 이동 처리
            if (playerMoveLeft && !playerMoveRight) { player.moveX(-currentPlayerMoveSpeed); }
            else if (playerMoveRight && !playerMoveLeft) { player.moveX(currentPlayerMoveSpeed); }
            if (playerMoveUp && !playerMoveDown) { player.moveY(-currentPlayerMoveSpeed); }
            else if (playerMoveDown && !playerMoveUp) { player.moveY(currentPlayerMoveSpeed); }

            boolean needClearEnemies = false;

            // 적 처리 (이동, 총알 발사, 충돌 검사)
            Iterator<Enemy> enemyList = enemies.iterator();
            while (enemyList.hasNext()) {
                Enemy enemy = enemyList.next();
                enemy.move();
                enemy.tryToShoot();
                enemy.updateEnemyShots(height);

                if (enemy.isCollidedWithShot(shots)) { // 적이 플레이어 총알에 맞았을 때
                    enemies.remove(enemy); // 적 제거
                    if (!bossAppear) {
                        --bossThreshold; // 보스 등장 카운트 감소
                        --enemySize;
                        System.out.println("남은 보스 등장 처치 조건: " + bossThreshold);
                        if (bossThreshold <= 0 && !bossAppear) { // 보스 등장 조건 충족
                            needClearEnemies = true;
                            spawnBoss();
                            timer.stop(); // 적 생성 타이머 중지
                            break;
                        }
                    }
                }
                // 게임 종료 조건: 적이 플레이어와 충돌 또는 화면 아래 도달
                if (enemy.isCollidedWithPlayer(player)) {
                    JOptionPane.showMessageDialog(this, "게임오버: 플레이어와 충돌");
                    System.exit(0);
                }
                if (enemy.getY() >= height) {
                    JOptionPane.showMessageDialog(this, "게임오버: 적이 화면 아래에 도달");
                    System.exit(0);
                }
            }

            if (needClearEnemies) { // 적 제거 플래그가 설정되면
                enemies.clear();
                enemySize = 0;
            }

            if (boss != null) { // 보스 처리 (이동, 총알 충돌, 게임 종료)
                boss.move();
                if (boss.isCollidedWithShot(shots)) { // 보스가 플레이어 총알에 맞았을 때
                    if (boss.getHealth() <= 0) { // 보스 처치
                        boss = null;
                        bossAppear = false;
                        System.out.println("보스 처치!");

                        if (!stageManager.isFinalStage()) { // 다음 스테이지 진행
                            stageManager.advanceStage();
                            JOptionPane.showMessageDialog(this, "다음 스테이지로 진행: " + stageManager.getCurrentStage());
                            bossThreshold = stageManager.getEnemyCountForStage();
                            enemySize = 0;
                            enemies.clear();
                            timer.start(); // 적 생성 타이머 재시작
                        } else { // 마지막 스테이지 클리어
                            JOptionPane.showMessageDialog(this, "게임 클리어!");
                        }
                        continue; // 다음 반복으로 이동하여 null 보스 오류 방지
                    }
                }
                // 게임 종료 조건: 보스가 플레이어와 충돌 또는 화면 아래 도달
                if (boss.isCollidedWithPlayer(player)) {
                    JOptionPane.showMessageDialog(this, "게임오버: 보스가 플레이어와 충돌");
                    System.exit(0);
                }
                if (boss.getY() >= height) {
                    JOptionPane.showMessageDialog(this, "게임오버: 보스가 화면 아래에 도달");
                    System.exit(0);
                }
            }

            repaint(); // 화면 다시 그리기
            try { Thread.sleep(5); } catch (InterruptedException ex) {}
            Thread.currentThread().setPriority(Thread.MAX_PRIORITY);
        }
    }

    public void initImage(Graphics g) { // 더블 버퍼링 이미지 초기화
        if (dbImage == null) {
            dbImage = createImage(this.getSize().width, this.getSize().height);
            dbg = dbImage.getGraphics();
        }
        dbg.setColor(getBackground());
        dbg.fillRect(0, 0, this.getSize().width, this.getSize().height);
        dbg.setColor(getForeground());

        if (backgroundImg != null) { dbg.drawImage(backgroundImg, 0, 0, this.getSize().width, this.getSize().height, this); }
        else { dbg.setColor(Color.BLACK); dbg.fillRect(0, 0, this.getSize().width, this.getSize().height); }
    }

    public void paintComponent(Graphics g) { // 컴포넌트 그리기 (더블 버퍼링 적용)
        if (dbImage == null) {
            dbImage = createImage(this.getSize().width, this.getSize().height);
            dbg = dbImage.getGraphics();
        }

        if (backgroundImg != null) { dbg.drawImage(backgroundImg, 0, 0, this.getSize().width, this.getSize().height, this); }
        else { dbg.setColor(Color.BLACK); dbg.fillRect(0, 0, this.getSize().width, this.getSize().height); }

        Image bg = stageManager.getBackgroundImage();
        if (bg != null) { dbg.drawImage(bg, 0, 0, width, height, this); }

        player.drawPlayer(dbg);

        // 적 그리기 및 이미지 설정
        for (Enemy enemy : enemies) {
            enemy.draw(dbg);
            enemy.setEnemyImage(stageManager.getEnemyImagePathForStage());
        }

        // 플레이어 총알 그리기
        for (int i = 0; i < shots.length; i++) {
            if (shots[i] != null) { shots[i].drawShot(dbg,Color.RED); }
        }

        // 적 총알 그리기 및 플레이어 충돌 검사
        for (Enemy enemy : enemies) {
            for (Shot s : enemy.getEnemyShots()) {
                s.drawShot(dbg, Color.YELLOW);
                if (player.isHitByShot(s)) { // 플레이어가 적 총알에 맞았을 때
                    s.collided();
                    player.decreasehealth(); // 체력 감소
                    if (player.getHealth() <= 0) { // 게임 오버
                        JOptionPane.showMessageDialog(this, "Game Over!");
                        System.exit(0);
                    }
                }
            }
        }

        if (boss != null) { boss.draw(dbg); }

        // 폭탄 그리기
        for (Bomb bomb : bombs) {
            if (bomb != null) { bomb.drawBomb(dbg); }
        }

        if (showWindEffect) { // 바람 효과 표시
            if (System.currentTimeMillis() > windEffectEndTime) { showWindEffect = false; }
        }

        // 남은 폭탄 개수 표시
        dbg.setColor(Color.YELLOW);
        dbg.setFont(new Font("Arial", Font.BOLD, 16));
        dbg.drawString("Bombs Left: " + player.getScreenBombCount(), 0, 40);

        if (stageManager.getCurrentStage() == stageManager.getMaxStage()) { // 마지막 스테이지 특수 효과
            dbg.setColor(new Color(135, 206, 250, 128));
            dbg.fillRect(0, 0, getWidth(), getHeight());
            dbg.setColor(Color.BLUE);
            dbg.drawString("wind!", getWidth() / 2 - 20, 50);
        }
        // 스테이지 정보 표시
        dbg.setColor(Color.WHITE);
        dbg.drawString("Stage: " + stageManager.getCurrentStage(), 10, 20);

        g.drawImage(dbImage, 0, 0, this); // 최종 이미지 화면에 그리기
    }


    public static void main(String[] args) {
        CharacterType[] characterOptions = CharacterType.values();
        // 캐릭터 선택 대화 상자
        CharacterType chosenCharacter = (CharacterType) JOptionPane.showInputDialog(
                null,
                "플레이할 캐릭터를 선택하세요:",
                "캐릭터 선택",
                JOptionPane.PLAIN_MESSAGE,
                null,
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
        ship.start(); // 게임 시작
    }
}