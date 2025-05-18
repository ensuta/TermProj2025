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
    private static final String IMAGE_PATH = "src\\shootingspaceship\\image\\babyshark_64x64.png";


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
