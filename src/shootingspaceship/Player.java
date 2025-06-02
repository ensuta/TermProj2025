package shootingspaceship;

import java.awt.Graphics;
import java.awt.Rectangle;
import java.util.List;
import java.awt.Color;

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

    public Player(int x, int y, int min_x, int max_x, int min_y, int max_y, int bulletDamage) {
        x_pos = x;
        y_pos = y;
        this.min_x = min_x;
        this.max_x = max_x;
        this.min_y = min_y;
        this.max_y = max_y;
        this.bulletDamage = bulletDamage;
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

    public int getX() {
        return x_pos;
    }

    public int getY() {
        return y_pos;
    }

    public Shot generateShot() {
        return new Shot(x_pos, y_pos, this.bulletDamage); // 캐릭터별 데미지 사용
    }

    public void drawPlayer(Graphics g) {
        g.setColor(Color.blue);
        int[] x_poly = {x_pos, x_pos - 10, x_pos, x_pos + 10};
        int[] y_poly = {y_pos, y_pos + 15, y_pos + 10, y_pos + 15};
        g.fillPolygon(x_poly, y_poly, 4);
    }
}
