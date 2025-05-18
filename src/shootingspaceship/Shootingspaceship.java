package shootingspaceship;

import java.awt.*;
import javax.swing.*;
import java.awt.event.*;
import java.util.*;


public class Shootingspaceship extends JPanel implements Runnable {

    private Thread th;
    private Player player;
    private Shot[] shots;
    private ArrayList enemies;
    private Boss boss = null;
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
        enemies = new ArrayList();
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
            if (!bossAppear && ++enemySize <= maxEnemySize) {
                float downspeed;
                do {
                    downspeed = rand.nextFloat() * enemyMaxDownSpeed;
                } while (downspeed == 0);

                float horspeed = rand.nextFloat() * 2 * enemyMaxHorizonSpeed - enemyMaxHorizonSpeed;
                //System.out.println("enemySize=" + enemySize + " downspeed=" + downspeed + " horspeed=" + horspeed);
                Enemy newEnemy = new Enemy((int) (rand.nextFloat() * width), 0, horspeed, downspeed, width, height, enemyDownSpeedInc);
                enemies.add(newEnemy);
            } else if(!bossAppear && enemySize >= bossThreshold) {
                timer.stop();
            }
        }
    }

    private void spawnBoss() {
    	boss = new Boss(width / 2, 50, 0.5f, 02f, width, height, 0.05f);
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

            Iterator enemyList = enemies.iterator();
            while (enemyList.hasNext()) {
                Enemy enemy = (Enemy) enemyList.next();
                enemy.move();
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

        Iterator enemyList = enemies.iterator();
        while (enemyList.hasNext()) {
            Enemy enemy = (Enemy) enemyList.next();
            enemy.draw(g);
            if (enemy.isCollidedWithShot(shots)) {
                enemyList.remove();
                
                if(!bossAppear) {
                	bossThreshold--;
                	System.out.println("남은 처치 조건: " +bossThreshold);
                	
                	if(bossThreshold <= 0 && enemies.isEmpty() && !bossAppear) {
                		spawnBoss();
                		timer.stop();
                	}
                }
            }
            if (enemy.isCollidedWithPlayer(player)) {
                enemyList.remove();
                System.exit(0);
            }
        }
        
        if(boss != null) {
        	boss.move();
        	
        	if(boss.isCollidedWithShot(shots)) {
        		if(boss.getHealth() <= 0 ) {
        			boss = null;
        			bossAppear = false;
        			System.out.println("보스 처치!");
        		}
        	}
        	
        	if(boss.isCollidedWithPlayer(player)) {
        		boss = null;
        		System.exit(0);
        	}
        }

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
        // TODO code application logic here
        JFrame frame = new JFrame("Shooting");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        Shootingspaceship ship = new Shootingspaceship();
        frame.getContentPane().add(ship);
        frame.pack();
        frame.setVisible(true);
        ship.start();
    }
}

