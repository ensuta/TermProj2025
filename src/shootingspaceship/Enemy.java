package shootingspaceship; 
import java.awt.Graphics;
import java.awt.Image;
import java.awt.Rectangle;
import java.awt.Color; 
import javax.imageio.ImageIO; 
import java.awt.image.BufferedImage; 
import java.io.IOException; 
import java.io.File; 

public class Enemy {
	private static Image enemyImg;

    float x_pos; 
    float y_pos; 
    float delta_x; 
    float delta_y; 
    int max_x; 
    int max_y; 
    float delta_y_inc; 
    final int collision_distance = 10;
    BufferedImage enemyImage; 
    private static final String babycrocodiro_IMAGE_PATH = "/shootingspaceship/Image/babycrocodiro-removebg-preview.png";
    //스테이지마다 적과 보스의 이미지가 달라져야하는데.... 모르겟음
    // 적 생성자: 위치, 속도, 화면 크기, 속도 증가량을 받아 초기화
    public Enemy(int x, int y, float delta_x, float delta_y, int max_x, int max_y, float delta_y_inc) {
        x_pos = x; 
        y_pos = y; 
        this.delta_x = delta_x; 
        this.delta_y = delta_y; 
        this.max_x = max_x; 
        this.max_y = max_y; 
        this.delta_y_inc = delta_y_inc; 
        
        try {
        	enemyImage = ImageIO.read(getClass().getResource(babycrocodiro_IMAGE_PATH));
        } catch (IOException e) {
            e.printStackTrace();
        }
    
    }
    
    // 적 이동 메서드
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
        // if (y_pos > max_y) { // 아래쪽 화면을 벗어나면
        //     y_pos = 0; // y좌표 맨 위로 이동
        //     delta_y += delta_y_inc; // y속도 증가(난이도 상승)
        // }
        //지금은 바닥에 닿으면 게임오버처리.
    }
    
    public float getX() {
        return x_pos;
    }

    public float getY() {
        return y_pos;
    }
    //적이 총알과 충돌했는지 검사
    public boolean isCollidedWithShot(Shot[] shots) {
        for (Shot shot : shots) { 
            if (shot == null) { 
                continue;
            }
            if (-collision_distance <= (y_pos - shot.getY()) && (y_pos - shot.getY() <= collision_distance)) {
                if (-collision_distance <= (x_pos - shot.getX()) && (x_pos - shot.getX() <= collision_distance)) {
                    shot.collided(); 
                    return true; 

                }
            }
        }
        return false;
    }

    // 적이 플레이어와 충돌했는지 검사
    public boolean isCollidedWithPlayer(Player player) {
    	Rectangle bombBounds = getBounds();
        Rectangle playerBounds = player.getBounds();
    	
    	int collision_distance = 32;
        if (-collision_distance <= (y_pos - player.getY()) && (y_pos - player.getY() <= collision_distance)) {
            if (-collision_distance <= (x_pos - player.getX()) && (x_pos - player.getX() <= collision_distance)) {
            	System.out.println("폭탄과 플레이어 충돌!");
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
        } else { //이미지가없으면, 아마 의미없음
            g.setColor(Color.yellow); 
            int[] x_poly = {(int) x_pos, (int) x_pos - 10, (int) x_pos, (int) x_pos + 10};
            int[] y_poly = {(int) y_pos + 15, (int) y_pos, (int) y_pos + 10, (int) y_pos};
            g.fillPolygon(x_poly, y_poly, 4); 
            g.drawImage(enemyImage, max_x, max_y, null); 
        }
  }

	public Rectangle getBounds() {
		// TODO Auto-generated method stub
		return null;
	}
}
