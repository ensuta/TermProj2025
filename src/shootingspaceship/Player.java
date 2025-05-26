/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package shootingspaceship;

import java.awt.Graphics;
import java.awt.Rectangle;
import java.util.ArrayList;
import java.util.List;
import java.awt.Color;

public class Player {
    private int x_pos;
    private int y_pos;
    private int min_x;
    private int max_x;
    //Player 고유 크기
    private static final int WIDTH = 20;
    private static final int HEIGHT = 20;
    //2페이지 바람 방해 패턴
    private boolean isFrozen = false;
    private long frozenUntil = 0;
    //화면정리 폭탄 변수
    private int screenBombCount = 3; // 예: 3번 사용 가능


    public Player(int x, int y, int min_x, int max_x) {
        x_pos = x;
        y_pos = y;
        this.min_x = min_x;
        this.max_x = max_x;
    }

    public void moveX(int speed) {
        x_pos += speed;
        if( x_pos < min_x) x_pos = min_x;
        if( x_pos > max_x) x_pos = max_x;
    }

    public int getX() {
        return x_pos;
    }

    public int getY() {
        return y_pos;
    }

    public Shot generateShot() {
        return new Shot(x_pos, y_pos, 5);
    }
    
    public Rectangle getBounds() {
    	return new Rectangle((int)x_pos, (int)y_pos, 32, 32); // 고정 크기
    }
    
    public void freeze(long durationMillis) {
        isFrozen = true;
        frozenUntil = System.currentTimeMillis() + durationMillis;
    }
    
    public void useScreenClearBomb(List<Enemy> enemies, List<Bomb> bombs) {
        if (screenBombCount > 0) {
            screenBombCount--;
            enemies.clear();      // 적 제거
            bombs.clear();   // 적의 폭탄 제거

            // 이펙트 또는 사운드 효과 표시용 코드 추가 가능
            System.out.println("화면 정리 폭탄 사용");
        }
    }
    
    public int getScreenBombCount() {
        return screenBombCount;
    }
    
    //ClearBomb 기능
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
    
    public void updateMovement() {
        if (isFrozen && System.currentTimeMillis() > frozenUntil) {
            isFrozen = false;
        }
        
        if (!isFrozen) {
            // 기존 이동 처리
        	moveX(5); //5는 예시용
        }
    }
    public void drawPlayer(Graphics g) {
        g.setColor(Color.blue);
        int[] x_poly = {x_pos, x_pos - 10, x_pos, x_pos + 10};
        int[] y_poly = {y_pos, y_pos + 15, y_pos + 10, y_pos + 15};
        g.fillPolygon(x_poly, y_poly, 4);
    }

	public void useScreenClearBomb(ArrayList<Enemy> enemies, List<Bomb> bombs) {
		// TODO Auto-generated method stub
		
	}
}
