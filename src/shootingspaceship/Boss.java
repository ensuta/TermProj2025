package shootingspaceship; 
import java.awt.*; 
import java.awt.image.BufferedImage; 
import javax.imageio.ImageIO; 
import java.io.File; 
import java.io.IOException; 

public class Boss extends Enemy { 
    private int health; 
    protected int maxHealth;
    
    private final Color bossColor = Color.RED; // 보스의 색상
    BufferedImage bossImage; 
    //private static final String shark_IMAGE_PATH = "src\\shootingspaceship\\image\\shark_128x128.png"; 

    public Boss(int x, int y, float delta_x, float delta_y, int max_x, int max_y, float delta_y_inc, String imagePath){
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
    public void setHealth(int health) { // 체력 직접 설정
        this.health = health;
        this.maxHealth = health;
    }
    
    public void setBossImage(Image img) {
    	this.bossImage = (BufferedImage) img;
    }

    @Override
    public void draw(Graphics g) { 
        if(bossImage != null) { 
            int imgW = bossImage.getWidth(); 
            int imgH = bossImage.getHeight(); 
            g.drawImage(bossImage, (int)(x_pos - imgW/2),(int)(y_pos - imgH /2),null); 
            g.setColor(bossColor);
            
            //체력바 그리기
            int barWidth = imgW; //체력바 너비는 보스 이미지 너비와 동
            int barHeight = 10; //체력바 높이는 고정값으로 설정 (10픽셀)
            int barX = (int)(x_pos - imgW/2); //체력바 X위치 -> 이미지 왼쪽 끝과 일치
            int barY = (int)(y_pos - imgH /2)- 15; // 체력바 Y위치는 이미지 위쪽에서 약간 띄운 위치

            float healthRatio = (float) health / (float) maxHealth; //현재 체력을 최대 체력으로 나눠 비율 계산
            int currentBarWidth = (int)(barWidth * healthRatio); //비율기반 체력바 현재 너비 계산

            // 체력바 배경 (회색)
            g.setColor(Color.GRAY);
            g.fillRect(barX, barY, barWidth, barHeight);

            // 체력바 실제 체력 (빨강)
            g.setColor(Color.RED);
            g.fillRect(barX, barY, currentBarWidth, barHeight);

            // 테두리
            g.setColor(Color.BLACK);
            g.drawRect(barX, barY, barWidth, barHeight);
            
            //체력 숫자
            g.setColor(Color.WHITE);
            g.drawString(health + "/" + maxHealth, barX + 5, barY - 2); //체력 숫자를 흰색으로 표시   
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