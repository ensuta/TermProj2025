package shootingspaceship;

import java.awt.*;
import java.awt.image.BufferedImage;
import javax.imageio.ImageIO;
import java.io.File;
import java.io.IOException;

public class Boss extends Enemy {
    private int health;
    protected int maxHealth;
    private final Color bossColor = Color.RED;
    BufferedImage bossImage;
    
    public Boss(int x, int y, float delta_x, float delta_y, int max_x, int max_y, float delta_y_inc, String imagePath, int i){
        super(x, y, delta_x, delta_y, max_x, max_y, delta_y_inc);
        try {
            bossImage = ImageIO.read(new File("src\\shootingspaceship\\image\\"+imagePath));
        } catch(IOException e) {
            e.printStackTrace();
        }
    }
    public int getHealth() {    // 현재 체력 반환
        return health;
    }
    public void setHealth(int health) { // 체력 설정 및 최대 체력 업데이트
        this.health = health;
        this.maxHealth = health;
    }
    
    public void setBossImage(Image img) {
        this.bossImage = (BufferedImage) img;
    }
    public Bomb shootBomb() {    // 보스 폭탄 발사 패턴
        return new Bomb((int)x_pos, (int)y_pos + 20, 3);
    }

    @Override
    public void draw(Graphics g) {    // 보스 및 체력바 그리기
        if(bossImage != null) {
            int imgW = bossImage.getWidth();
            int imgH = bossImage.getHeight();
            g.drawImage(bossImage, (int)(x_pos - imgW/2),(int)(y_pos - imgH /2),null);
            g.setColor(bossColor);
            
            // 체력바 그리기
            int barWidth = imgW;
            int barHeight = 10;
            int barX = (int)(x_pos - imgW/2);
            int barY = (int)(y_pos - imgH /2)- 15;

            float healthRatio = (float) health / (float) maxHealth;
            int currentBarWidth = (int)(barWidth * healthRatio);

            // 체력바 배경 (회색)
            g.setColor(Color.GRAY);
            g.fillRect(barX, barY, barWidth, barHeight);

            // 체력바 실제 체력 (빨강)
            g.setColor(Color.RED);
            g.fillRect(barX, barY, currentBarWidth, barHeight);

            // 체력바 테두리
            g.setColor(Color.BLACK);
            g.drawRect(barX, barY, barWidth, barHeight);
            
            // 체력 숫자 표시
            g.setColor(Color.WHITE);
            g.drawString(health + "/" + maxHealth, barX + 5, barY - 2);    
            
        }else { // 이미지가 없을 경우 (대체 그리기)
            g.setColor(bossColor);    
            int[] x_poly = {(int) x_pos, (int) x_pos - 15, (int) x_pos, (int) x_pos + 15};    
            int[] y_poly = {(int) y_pos + 20, (int) y_pos, (int) y_pos + 15, (int) y_pos};    
            g.fillPolygon(x_poly, y_poly, 4);    
            g.setColor(Color.WHITE);    
            g.drawString("Health: " + health, (int) x_pos - 20, (int) y_pos - 10);
        }
    }

    @Override
    public boolean isCollidedWithShot(Shot[] shots) {    // 총알과의 충돌 검사
        for (Shot shot : shots) {    
            if (shot == null || !shot.isAlive()) {    
                continue;
            }
            // 충돌 거리 계산 (보스는 2배 크기 고려)
            if (-collision_distance * 2 <= (y_pos - shot.getY()) && (y_pos - shot.getY() <= collision_distance * 2)) {
                if (-collision_distance * 2 <= (x_pos - shot.getX()) && (x_pos - shot.getX() <= collision_distance * 2)) {
                    shot.collided();    // 총알 충돌 처리
                    this.health -= shot.getDamage(); // 보스 체력 감소
                    return health <= 0; // 체력이 0 이하면 true 반환
                }
            }
        }
        return false;    // 충돌 없으면 false 반환
    }
}