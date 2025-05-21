package shootingspaceship; 
import java.awt.*; 
import java.awt.image.BufferedImage; 
import javax.imageio.ImageIO; 
import java.io.File; 
import java.io.IOException; 

public class Boss extends Enemy { 
    private int health; 
    private final Color bossColor = Color.RED; // 보스의 색상
    BufferedImage bossImage; 
    private static final String shark_IMAGE_PATH = "src\\shootingspaceship\\image\\shark_128x128.png"; 

    public Boss(int x, int y, float delta_x, float delta_y, int max_x, int max_y, float delta_y_inc){
        super(x, y, delta_x, delta_y, max_x, max_y, delta_y_inc); 
        try {
            bossImage = ImageIO.read(new File(shark_IMAGE_PATH)); 
        } catch(IOException e) {
            e.printStackTrace(); 
        }
    }
    public int getHealth() {    // 현재 체력 반환
        return health;
    }
    public void setHealth(int health) { // 체력 직접 설정
        this.health = health;
    }

    @Override
    public void draw(Graphics g) { 
        if(bossImage != null) { 
            int imgW = bossImage.getWidth(); 
            int imgH = bossImage.getHeight(); 
            g.drawImage(bossImage, (int)(x_pos - imgW/2),(int)(y_pos - imgH /2),null); 
            g.setColor(bossColor);
            g.drawString("Health: " + health, (int) x_pos - 20, (int) y_pos - 10); 
            
        }else { // 이미지가 없으면, 아마 의미없음
            g.setColor(bossColor); 
            int[] x_poly = {(int) x_pos, (int) x_pos - 15, (int) x_pos, (int) x_pos + 15}; 
            int[] y_poly = {(int) y_pos + 20, (int) y_pos, (int) y_pos + 15, (int) y_pos}; 
            g.fillPolygon(x_poly, y_poly, 4); 
            g.setColor(Color.WHITE); 
            g.drawString("Health: " + health, (int) x_pos - 20, (int) y_pos - 10);
        }
    }

    @Override
    public boolean isCollidedWithShot(Shot[] shots) {    
        for (Shot shot : shots) { 
            if (shot == null || !shot.isAlive()) { 
                continue;
            }
            // 충돌거리, 보스는 2배크기
            if (-collision_distance * 2 <= (y_pos - shot.getY()) && (y_pos - shot.getY() <= collision_distance * 2)) {
                if (-collision_distance * 2 <= (x_pos - shot.getX()) && (x_pos - shot.getX() <= collision_distance * 2)) {
                    shot.collided(); 
                    this.health -= shot.getDamage(); // Shot의 데미지만큼 감소
                    return health <= 0;
                }
            }
        }
        return false; 
    }
}