/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package shootingspaceship;

import java.awt.Graphics;
import java.awt.Color;
import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.File;

/**
 *
 * @author wgpak
 */
public class Enemy {

    float x_pos;
    float y_pos;
    float delta_x;
    float delta_y;
    int max_x;
    int max_y;
    float delta_y_inc;
    final int collision_distance = 10;
    BufferedImage enemyImage;
    private static final String IMAGE_PATH = "/Users/onbln_1/Downloads/babyshark_64x64.png";


    public Enemy(int x, int y, float delta_x, float delta_y, int max_x, int max_y, float delta_y_inc) {
        x_pos = x;
        y_pos = y;
        this.delta_x = delta_x;
        this.delta_y = delta_y;
        this.max_x = max_x;
        this.max_y = max_y;
        this.delta_y_inc = delta_y_inc;
        try {
        	enemyImage = ImageIO.read(new File(IMAGE_PATH));// 경로 확인!
        } catch(IOException e) {
        	e.printStackTrace();
        }
    }

    public void move() {
        x_pos += delta_x;
        y_pos += delta_y;

        if (x_pos < 0) {
            x_pos = 0;
            delta_x = -delta_x;
        } else if (x_pos > max_x) {
            x_pos = max_x;
            delta_x = -delta_x;
        }
        if (y_pos > max_y) {
            y_pos = 0;
            delta_y += delta_y_inc;
        }
    }

    public boolean isCollidedWithShot(Shot[] shots) {
        for (Shot shot : shots) {
            if (shot == null) {
                continue;
            }
            if (-collision_distance <= (y_pos - shot.getY()) && (y_pos - shot.getY() <= collision_distance)) {
                if (-collision_distance <= (x_pos - shot.getX()) && (x_pos - shot.getX() <= collision_distance)) {
                    //collided.
                    shot.collided();
                    return true;
                }
            }
        }
        return false;
    }

    public boolean isCollidedWithPlayer(Player player) {
        if (-collision_distance <= (y_pos - player.getY()) && (y_pos - player.getY() <= collision_distance)) {
            if (-collision_distance <= (x_pos - player.getX()) && (x_pos - player.getX() <= collision_distance)) {
                //collided.
                return true;
            }
        }
        return false;
    }

    public void draw(Graphics g) {
    	if(enemyImage != null) {
    		int imgW = enemyImage.getWidth();
    		int imgH = enemyImage.getHeight();
    		g.drawImage(enemyImage, (int)(x_pos - imgW/2),(int)(y_pos - imgH /2),null);
    	}else {
	        g.setColor(Color.yellow);
	        int[] x_poly = {(int) x_pos, (int) x_pos - 10, (int) x_pos, (int) x_pos + 10};
	        int[] y_poly = {(int) y_pos + 15, (int) y_pos, (int) y_pos + 10, (int) y_pos};
	        g.fillPolygon(x_poly, y_poly, 4);
	        g.drawImage(enemyImage, max_x, max_y, null);
    	}
    	}
}

class Boss extends Enemy {
	private int health;
    private final int initialHealth = 20; // 보스의 초기 체력
    private final Color bossColor = Color.RED; // 보스의 색상
    BufferedImage bossImage;
    private static final String IMAGE_PATH = "/Users/onbln_1/Downloads/shark_200x200.png";
    
	Boss(int x, int y, float delta_x, float delta_y, int max_x, int max_y, float delta_y_inc){
		super(x, y, delta_x, delta_y, max_x, max_y, delta_y_inc); //super로 생성자 상속
		this.health = initialHealth;
		try {
        	bossImage = ImageIO.read(new File(IMAGE_PATH));// 경로 확인!
        } catch(IOException e) {
        	e.printStackTrace();
        }
	}
	
	public int getHealth() {	//현재 체력을 보여주는 함수
        return health;
    }

    public void decreaseHealth() {	//체력을 깎는 함수
        this.health--;
    }
    
    @Override
    public void draw(Graphics g) {//boss
    	if(enemyImage != null) {
    		int imgW = bossImage.getWidth();
    		int imgH = bossImage.getHeight();
    		g.drawImage(bossImage, (int)(x_pos - imgW/2),(int)(y_pos - imgH /2),null);
    		g.drawString("Health: " + health, (int) x_pos - 20, (int) y_pos - 10);
    	}else {
	    	g.setColor(bossColor);
	        int[] x_poly = {(int) x_pos, (int) x_pos - 15, (int) x_pos, (int) x_pos + 15};
	        int[] y_poly = {(int) y_pos + 20, (int) y_pos, (int) y_pos + 15, (int) y_pos};
	        g.fillPolygon(x_poly, y_poly, 4);
	        g.setColor(Color.WHITE);
	        g.drawString("Health: " + health, (int) x_pos - 20, (int) y_pos - 10);
    	}
	}
    
    @Override
    public boolean isCollidedWithShot(Shot[] shots) {	//만약 총알에 맞는다면
        for (Shot shot : shots) {
            if (shot == null || !shot.isAlive()) {
                continue;
            }
            if (-collision_distance * 2 <= (y_pos - shot.getY()) && (y_pos - shot.getY() <= collision_distance * 2)) {
                if (-collision_distance * 2 <= (x_pos - shot.getX()) && (x_pos - shot.getX() <= collision_distance * 2)) {
                    shot.collided();
                    decreaseHealth();
                    return health <= 0; // 체력이 0 이하이면 충돌로 간주하여 제거
                }
            }
        }
        return false;
    }
    
}
