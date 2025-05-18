package shootingspaceship;

import java.awt.*;
import java.awt.image.BufferedImage;
import javax.imageio.ImageIO;
import java.io.File;
import java.io.IOException;

public class Boss extends Enemy {
    private int health;
    private final int initialHealth = 20; // 보스의 초기 체력
    private final Color bossColor = Color.RED; // 보스의 색상
    BufferedImage bossImage;
    private static final String IMAGE_PATH = "src\\shootingspaceship\\image\\shark_128x128.png";
    
    public Boss(int x, int y, float delta_x, float delta_y, int max_x, int max_y, float delta_y_inc){
        super(x, y, delta_x, delta_y, max_x, max_y, delta_y_inc); //super로 생성자 상속
        this.health = initialHealth;
        try {
            bossImage = ImageIO.read(new File(IMAGE_PATH));// 경로 확인!
        } catch(IOException e) {
            e.printStackTrace();
        }
    }
    
    public int getHealth() {    //현재 체력을 보여주는 함수
        return health;
    }

    public void decreaseHealth() {    //체력을 깎는 함수
        this.health--;
    }
    
    public void setHealth(int health) {
        this.health = health;
    }
    
    @Override
    public void draw(Graphics g) {//boss
        if(bossImage != null) {
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
    public boolean isCollidedWithShot(Shot[] shots) {    //만약 총알에 맞는다면
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