/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package shootingspaceship;

import java.awt.Graphics;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.geom.Path2D;



public class Player {
    private int x_pos;
    private int y_pos;
    private int min_x;
    private int max_x;
    protected int min_y;	//최소 y 값
    protected int max_y;	//최대 y 값
    private int maxHealth = 3;  // 최대 체력 (하트 3개)
    private int currentHealth = 3;  // 현재 체력
    
    // 체력관리
    public void takeDamage() {
        if (currentHealth > 0) {
            currentHealth--;

            if (currentHealth == 0) {
                System.out.println("게임 오버!");
                // 여기에 게임 종료
            }
        }
    }
    
 // 간단한 하트 그리기 함수
    private void drawHeartShape(Graphics g, int x, int y, int size) {
        Graphics2D g2 = (Graphics2D) g;
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2.setColor(Color.RED);

        int w = size;
        int h = size;

        // 곡선 하트 경로
        Path2D.Double heart = new Path2D.Double();
        heart.moveTo(x + w / 2.0, y + h / 5.0);

        heart.curveTo(x + w * 1.2, y - h / 3.0, x + w * 1.2, y + h * 0.8, x + w / 2.0, y + h);
        heart.curveTo(x - w * 0.2, y + h * 0.8, x - w * 0.2, y - h / 3.0, x + w / 2.0, y + h / 5.0);

        g2.fill(heart);
    }




    
 // 현재 체력을 하트 모양으로 그려주는 함수
    public void drawHealth(Graphics g) {
        int heartSize = 20;
        int xStart = 10; // 왼쪽 여백
        int yStart = 10; // 위쪽 여백

        g.setColor(Color.RED);
        for (int i = 0; i < currentHealth; i++) {
            drawHeartShape(g, xStart + i * (heartSize + 5), yStart, heartSize);
        }
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
