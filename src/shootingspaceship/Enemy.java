package shootingspaceship; 
import java.awt.Graphics;
import java.awt.Rectangle;
import java.awt.Color; 
import javax.imageio.ImageIO; 
import java.awt.image.BufferedImage; 
import java.io.IOException; 
import java.io.File; 
import java.net.URL;

public class Enemy { 

    float x_pos; 
    float y_pos; 
    float delta_x; 
    float delta_y; 
    int max_x; 
    int max_y; 
    float delta_y_inc; 
    final int collision_distance = 10;
    BufferedImage[] enemyImage; //이미지 파일 BufferedImage 객체 배열로 만들어서 애니메이션 효과줌
    int currentFrame = 0; // 현재 보여줄 프레임 인덱스(0번 프레임)
    long lastFrameTime = 0; // 마지막으로프레임이 전환된 시간 -> System.currentTImeMillis() 기준 시간 저장
    int frameDelay = 300; // 프레임 전환 간의 지연 시간 0.3초마다 다음 프레임으로 애니메이션 전환 
   
    
    public Enemy(int x, int y, float delta_x, float delta_y, int max_x, int max_y, float delta_y_inc) {
        x_pos = x; 
        y_pos = y; 
        this.delta_x = delta_x; 
        this.delta_y = delta_y; 
        this.max_x = max_x; 
        this.max_y = max_y; 
        this.delta_y_inc = delta_y_inc; 
    }
    
    public void setEnemyImage(String[] imagePath) {
    	try {
    		enemyImage = new BufferedImage[imagePath.length]; // 외부에서 전달받은 이미지 경로 개수만큼 bufferedImage 배열 생성
    		for(int i=0;i<imagePath.length;++i) { //각 이미지 돌면서 파일 로드
    			String fullPath = "/shootingspaceship/image/" + imagePath[i]; 
    			URL imageURL = getClass().getResource(fullPath);
    			if(imageURL == null) { // 이미지 파일 존재하지 않는 경우
    				System.err.println("이미지 파일을 찾을 수 없습니다.");
    				continue;
    			}
    			enemyImage[i] = ImageIO.read(imageURL);// 이미지 파일읽어서 BufferedImage 객체로 저장
    		}
        } catch(IOException e) {
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
        if (-collision_distance <= (y_pos - player.getY()) && (y_pos - player.getY() <= collision_distance)) {
            if (-collision_distance <= (x_pos - player.getX()) && (x_pos - player.getX() <= collision_distance)) {
                return true; 
            }
        }
        return false; 
    }

    public void draw(Graphics g) {
    	long currentTime = System.currentTimeMillis(); //현재 시간 측정 (애니메이션 프레임 전환)
    	
    	//enemyImage가 null이 아니고 이미지 배열이 비어있지 않은 경우에 실행
    	if(enemyImage != null && enemyImage.length > 0) {
    		if(currentTime - lastFrameTime > frameDelay) { //frameDelay만큼 시간이 지났다면 다음 프레임으로 전환
    			currentFrame = (currentFrame + 1)% enemyImage.length; //프레임 인덱스 순환
    			lastFrameTime = currentTime; //마지막 프레임 변경시간 갱신
    		}
    		int imgW = enemyImage[currentFrame].getWidth(); //현재 프레임의 높이, 너비 계산
    		int imgH = enemyImage[currentFrame].getHeight();
    		g.drawImage(enemyImage[currentFrame], (int)(x_pos - imgW/2),(int)(y_pos - imgH /2),null); //이미지 x_pos, y_pos 중심으로 화면에 그리기
        } else { //이미지가없으면, 아마 의미없음
            g.setColor(Color.yellow); 
            int[] x_poly = {(int) x_pos, (int) x_pos - 10, (int) x_pos, (int) x_pos + 10};
            int[] y_poly = {(int) y_pos + 15, (int) y_pos, (int) y_pos + 10, (int) y_pos};
            g.fillPolygon(x_poly, y_poly, 4); 
        }
    }

	public Rectangle getBounds() {
		// TODO Auto-generated method stub
		return null;
	}
}
