package shootingspaceship;

import java.awt.Graphics;
import java.awt.Rectangle;
import java.util.List;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.geom.Path2D;



public class Player {
    private int bulletDamage;
    private int x_pos;
    private int y_pos;
    private int min_x;
    private int max_x;
    protected int min_y;	//최소 y 값
    protected int max_y;	//최대 y 값

    //추가기능
    private static final int WIDTH = 32;
    private static final int HEIGHT = 32;
    //추가기능(바람 방해 패턴)
    private boolean isFrozen = false;
    private long frozenUntil = 0;
    //추가기능(화면 정리 폭탄)
    private int screenBombCount = 30;
    //추가기능(장애물 패턴용 이전 위치 저장)
    private int prevX, prevY;

    //플레이어 피통 추가
    private int health;
    private final int PlayerHealth = 5;
    protected boolean isHit = false;
    protected long hitTime = 0;
    public Player(int x, int y, int min_x, int max_x, int min_y, int max_y, int bulletDamage) {

        x_pos = x;
        y_pos = y;
        this.min_x = min_x;
        this.max_x = max_x;
        this.min_y = min_y;
        this.max_y = max_y;
        this.bulletDamage = bulletDamage;
        this.health =  PlayerHealth;
    }
    
    public void decreasehealth() { //
    	long now = System.currentTimeMillis();
    	if(now - hitTime > 1000) {
    		--health;
    		hitTime = now;
    		isHit = true;
    	}
    }
    
    public int getHealth() { //health값 가져오는 메소드
    	return health;
    }

    public void moveX(int speed) {
    	//isFrozen만 추가기능
    //	if(!isFrozen) {
    		x_pos += speed;
    		if( x_pos < min_x) x_pos = min_x;
    		if( x_pos > max_x) x_pos = max_x;
   // 	}
        
    }
    
    public void moveY(int speed) {
    		y_pos += speed;
    		if( y_pos < min_y) y_pos = min_y;
    		if( y_pos > max_y) y_pos = max_y;
    }
    //추가기능
    public Rectangle getBounds() {
    	 return new Rectangle(x_pos, y_pos, WIDTH, HEIGHT);
    }
    //추가기능
    public void savePrevPosition() {
    	prevX = x_pos;
    	prevY = y_pos;
    }
    //추가기능
    public int getPrevX() {
    	return prevX;
    }
    //추가기능
    public int getPrevY() {
        return prevY;
    }
    //추가기능
    public void setX(int x) {
        this.x_pos = x;
    }
    //추가기능
    public void setY(int y) {
        this.y_pos = y;
    }
    //추가기능(플레이어 폭탄사용)
    public int getScreenBombCount() {
        return screenBombCount;
    }
  //추가기능(플레이어 폭탄사용)
    public void useScreenBomb() {
        if (screenBombCount > 0) {
            screenBombCount--;
        }
    }
    
  //추가기능(ClearBomb 화면 에너미 처치)
    public void useBomb(List<Enemy> enemies, List<Bomb> bombs) {
        if (screenBombCount > 0) {
        	screenBombCount--;

            // 모든 적 제거
            enemies.clear();

            // 화면의 모든 폭탄 제거
            bombs.clear();

            System.out.println("폭탄 사용! 모든 적과 폭탄이 제거되었습니다.");
        }
    }
    
    public boolean isHitByShot(Shot shot) { // 플레이어가 총알에 맞았는지 판단하는 메소드
        Rectangle playerRect = new Rectangle(x_pos, y_pos, 40, 40); // 플레이어의 위치, 크기 기준으로 충돌 판정용 사각형 생성 
        Rectangle shotRect = new Rectangle(shot.getX(), shot.getY(), 3, 3); // shot 크기와 동일한 작은 사각형 생
        return playerRect.intersects(shotRect); // 두 사각형이 겹치는지 판단
    }


    public int getX() {
        return x_pos;
    }

    public int getY() {
        return y_pos;
    }

    public Shot generateShot() {
    	SoundPlayer.playSound("sounds/gunshot.wav");
        return new Shot(x_pos, y_pos, this.bulletDamage); // 캐릭터별 데미지 사용
    }

    public void drawPlayer(Graphics g) {
        g.setColor(Color.blue);
        int[] x_poly = {x_pos, x_pos - 10, x_pos, x_pos + 10};
        int[] y_poly = {y_pos, y_pos + 15, y_pos + 10, y_pos + 15};
        g.fillPolygon(x_poly, y_poly, 4);
        drawHealthBar(g);
    }
    
    protected void drawHealthBar(Graphics g) {
    	int barWidth = 10;
    	int barHeight = 10;
    	int spacing = 5;
    	for (int i = 0; i < PlayerHealth; i++) {
            if (i < health) {
                g.setColor(Color.RED);
            } else {
                g.setColor(Color.GRAY);
            }
            g.fillRect(x_pos - 25 + i * (barWidth + spacing), y_pos + 50, barWidth, barHeight);
        }
    
    }
}
