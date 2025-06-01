/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package shootingspaceship;

import java.awt.Graphics;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Graphics2D;


public class Player {
    private int x_pos;
    private int y_pos;
    private int min_x;
    private int max_x;
    protected int min_y;	//최소 y 값
    protected int max_y;	//최대 y 값
    private int maxHealth = 3;  // 최대 체력 (하트 3개)
    private int currentHealth = 3;  // 현재 체력
    
    // 체력 깎는 함수
    public void takeDamage(int amount) {
        currentHealth -= amount;

        if (currentHealth < 0) {
            currentHealth = 0;
        }
    }
    
 // 간단한 하트 그리기 함수
    private void drawHeartShape(Graphics g, int x, int y, int size) {
        Graphics2D g2 = (Graphics2D) g;
        int r = size / 2;

        // 왼쪽 원
        g2.fillOval(x, y, r, r);
        // 오른쪽 원
        g2.fillOval(x + r, y, r, r);
        // 아래 삼각형
        int[] xPoints = { x, x + size, x + size / 2 };
        int[] yPoints = { y + r, y + r, y + size };
        g2.fillPolygon(xPoints, yPoints, 3);
    }


    
    public Player(int x, int y, int min_x, int max_x, int min_y, int max_y) {
        x_pos = x;
        y_pos = y;
        this.min_x = min_x;
        this.max_x = max_x;
        this.min_y = min_y;
        this.max_y = max_y;
    }

    public void moveX(int speed) {
        x_pos += speed;
        if( x_pos < min_x) x_pos = min_x;
        if( x_pos > max_x) x_pos = max_x;
    }
    
    public void moveY(int speed) {
    	y_pos += speed;
    	if( y_pos < min_y) y_pos = min_y;
        if( y_pos > max_y) y_pos = max_y;
    }
    
    public int getX() {
        return x_pos;
    }

    public int getY() {
        return y_pos;
    }

    public Shot generateShot() {
    	SoundPlayer.playSound("sounds/gunshot.wav");
        return new Shot(x_pos, y_pos, 5);
    }

    public void drawPlayer(Graphics g) {
        g.setColor(Color.blue);
        int[] x_poly = {x_pos, x_pos - 10, x_pos, x_pos + 10};
        int[] y_poly = {y_pos, y_pos + 15, y_pos + 10, y_pos + 15};
        g.fillPolygon(x_poly, y_poly, 4);
    }
}
