package shootingspaceship;

import java.awt.*;
import javax.swing.*;
import java.awt.event.*;
import java.util.*;


public class Shootingspaceship extends JPanel implements Runnable {

    private Thread th;
    private Player player;
    private Shot[] shots;
    private ArrayList<Enemy> enemies;
    private Boss boss = null; // 보스 객체
    private final int shotSpeed = -2;
    private final int playerLeftSpeed = -2;
    private final int playerRightSpeed = 2;
    private final int width = 500;
    private final int height = 500;
    private final int playerMargin = 10;
    private final int enemyMaxDownSpeed = 1;
    private final int enemyMaxHorizonSpeed = 1;
    private final int enemyTimeGap = 2000; //unit: msec
    private final float enemyDownSpeedInc = 0.3f;
    private final int maxEnemySize = 10;
    private int enemySize;
    private javax.swing.Timer timer;
    private boolean playerMoveLeft;
    private boolean playerMoveRight;
    private Image dbImage;
    private Graphics dbg;
    private Random rand;
    private int maxShotNum = 20;
    private boolean bossAppear = false;	// 보스 등장 여부 플래그
    private int bossThreshold = 3; // 특정 수의 적을 처치하면 보스 등장
    
    
    public Shootingspaceship() {
        setBackground(Color.black);
        setPreferredSize(new Dimension(width, height));
        player = new Player(width / 2, (int) (height * 0.9), playerMargin, width-playerMargin );
        shots = new Shot[ maxShotNum ];
        enemies = new ArrayList<Enemy>();
        enemySize = 0;
        rand = new Random(1);
        timer = new javax.swing.Timer(enemyTimeGap, new addANewEnemy());
        timer.start();
        addKeyListener(new ShipControl());
        setFocusable(true);
    }

    public void start() {
        th = new Thread(this);
        th.start();
    }

    private class addANewEnemy implements ActionListener {

        public void actionPerformed(ActionEvent e) {
            if (!bossAppear && ++enemySize <= maxEnemySize) { //만약 보스가 없고 적이 maxEnemySize보다 적을 때
                float downspeed;
                do {
                    downspeed = rand.nextFloat() * enemyMaxDownSpeed;
                } while (downspeed == 0);

                float horspeed = rand.nextFloat() * 2 * enemyMaxHorizonSpeed - enemyMaxHorizonSpeed;
                //System.out.println("enemySize=" + enemySize + " downspeed=" + downspeed + " horspeed=" + horspeed);
                Enemy newEnemy = new Enemy((int) (rand.nextFloat() * width), 0, horspeed, downspeed, width, height, enemyDownSpeedInc);
                enemies.add(newEnemy);
            } else if (!bossAppear && enemySize >= bossThreshold) {	//만약 보스가 없고 적 수가 보스등장조건보다 많을 때
            	spawnBoss();
                timer.stop();
            }
        }
    }
    private void spawnBoss() {	//보스 생성 함수
        boss = new Boss(width / 2, 50, 0.5f, 0.2f, width, height, 0.05f);
        bossAppear = true;
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
                    // generate new shot and add it to shots array
                    for (int i = 0; i < shots.length; i++) {
                        if (shots[i] == null) {
                            shots[i] = player.generateShot();
                            break;
                        }
                    }
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
            }
        }

        public void keyTyped(KeyEvent e) {
        }
    }

    public void run() {
        //int c=0;
        Thread.currentThread().setPriority(Thread.MIN_PRIORITY);

        while (true) {
            //System.out.println( ++c );
            // do operations on shots in shots array
            for (int i = 0; i < shots.length; i++) {
                if (shots[i] != null) {
                    // move shot
                    shots[i].moveShot(shotSpeed);

                    // test if shot is out
                    if (shots[i].getY() < 0) {
                        // remove shot from array
                        shots[i] = null;
                    }
                }
            }

            if (playerMoveLeft) {
                player.moveX(playerLeftSpeed);
            } else if (playerMoveRight) {
                player.moveX(playerRightSpeed);
            }

         // 적 리스트를 순회하며 각 적에 대한 처리
            Iterator<Enemy> enemyList = enemies.iterator();
            while (enemyList.hasNext()) {
                Enemy enemy = enemyList.next(); // 다음 적 객체를 가져옴

                enemy.move(); // 적을 이동시킴 (Enemy 클래스 내 move() 메소드 호출)

                // 적과 총알의 충돌 여부 확인
                if (enemy.isCollidedWithShot(shots)) {
                    // 충돌이 발생했다면
                    enemyList.remove(); // 적 리스트에서 현재 적을 제거 (Iterator의 remove() 사용)

                    // 보스가 아직 등장하지 않았다면
                    if (!bossAppear) {
                        bossThreshold--; // 보스 등장 조건 카운트 감소
                        System.out.println("남은 처치 조건: " + bossThreshold);

                        // 보스 등장 조건이 충족되고, 적 리스트가 비어 있으며, 보스가 아직 등장하지 않았다면
                        if (bossThreshold <= 0 && enemies.isEmpty() && !bossAppear) {
                            spawnBoss(); // 보스를 생성하는 메소드 호출
                            timer.stop(); // 일반 적 생성 타이머 중지
                        }
                    }
                }

                // 적과 플레이어의 충돌 여부 확인
                if (enemy.isCollidedWithPlayer(player)) {
                    // 충돌이 발생했다면
                    enemyList.remove(); // 적 리스트에서 현재 적을 제거
                    System.exit(0); // 게임 종료
                }
            }

            // 보스가 존재한다면
            if (boss != null) {
                boss.move(); // 보스를 이동시킴 (Boss 클래스 내 move() 메소드 호출)

                // 보스와 총알의 충돌 여부 확인
                if (boss.isCollidedWithShot(shots)) {
                    // 충돌이 발생했다면
                    if (boss.getHealth() <= 0) {
                        boss = null; // 보스 객체를 null로 설정하여 제거
                        bossAppear = false; // 보스 등장 상태를 false로 변경
                        System.out.println("보스 처치!");
                        // 게임 클리어 로직 추가 가능
                    }
                }

                // 보스와 플레이어의 충돌 여부 확인
                if (boss.isCollidedWithPlayer(player)) {
                    boss = null; // 보스 객체를 null로 설정하여 제거
                    System.exit(0); // 게임 종료
                }
            }

            repaint();

            try {
                Thread.sleep(10);
            } catch (InterruptedException ex) {
                // do nothing
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
        //paint (dbg);

        g.drawImage(dbImage, 0, 0, this);
    }

    public void paintComponent(Graphics g) {
        initImage(g);

        // draw player
        player.drawPlayer(g);

        Iterator<Enemy> enemyList = enemies.iterator();
        while (enemyList.hasNext()) {
            Enemy enemy = (Enemy) enemyList.next();
            enemy.draw(g);
            if (enemy.isCollidedWithShot(shots)) {
                enemyList.remove();
            }
            if (enemy.isCollidedWithPlayer(player)) {
                enemyList.remove();
                System.exit(0);
            }
        }
        
        // 보스 그리기
        if (boss != null) {
        	boss.draw(g);
        }

        // draw shots
        for (int i = 0; i < shots.length; i++) {
            if (shots[i] != null) {
                shots[i].drawShot(g);
            }
        }
    }

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        JFrame frame = new JFrame("Shooting");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        Shootingspaceship ship = new Shootingspaceship();
        frame.getContentPane().add(ship);
        frame.pack();
        frame.setVisible(true);
        ship.start();
    }
}
