package shootingspaceship;

import java.awt.*;
import java.awt.image.BufferedImage;
import javax.imageio.ImageIO;
import java.io.File;
import java.io.IOException;
import java.net.URL;

public class Boss extends Enemy {
    private int health;
    protected int maxHealth;
    private final Color bossColor = Color.RED;
    BufferedImage[] bossImage; //이미지 파일 BufferedImage 객체 배열로 만들어서 애니메이션 효과줌
    int currentFrame = 0; // 현재 보여줄 프레임 인덱스(0번 프레임)
    long lastFrameTime = 0; // 마지막으로프레임이 전환된 시간 -> System.currentTImeMillis() 기준 시간 저장
    int frameDelay = 300; // 프레임 전환 간의 지연 시간 0.3초마다 다음 프레임으로 애니메이션 전환 
    
    public Boss(int x, int y, float delta_x, float delta_y, int max_x, int max_y, float delta_y_inc, int i){
        super(x, y, delta_x, delta_y, max_x, max_y, delta_y_inc);
        
    }
    public int getHealth() {    // 현재 체력 반환
        return health;
    }
    public void setHealth(int health) { // 체력 설정 및 최대 체력 업데이트
        this.health = health;
        this.maxHealth = health;
    }
    
    public void setBossImage(String[] imagePath) {
    	try {
    		if(imagePath != null) {
    		bossImage = new BufferedImage[imagePath.length]; // 외부에서 전달받은 이미지 경로 개수만큼 bufferedImage 배열 생성
    		}
    		for(int i=0;i<imagePath.length;++i) { //각 이미지 돌면서 파일 로드
    			String fullPath = "/shootingspaceship/image/" + imagePath[i]; 
    			URL imageURL = getClass().getResource(fullPath);
    			if(imageURL == null) { // 이미지 파일 존재하지 않는 경우
    				System.err.println("이미지 파일을 찾을 수 없습니다.");
    				continue;
    			}
    			bossImage[i] = ImageIO.read(imageURL);// 이미지 파일읽어서 BufferedImage 객체로 저장
    		}
        } catch(IOException e) {
        	e.printStackTrace();
        }
    }
    public Bomb shootBomb() {    // 보스 폭탄 발사 패턴
        return new Bomb((int)x_pos, (int)y_pos + 20, 3);
    }

    @Override
    public void draw(Graphics g) {    // 보스 및 체력바 그리기
    	
    	long currentTime = System.currentTimeMillis();
        if(bossImage != null && bossImage.length > 0) {
        	if(currentTime - lastFrameTime > frameDelay) { //frameDelay만큼 시간이 지났다면 다음 프레임으로 전환
			currentFrame = (currentFrame + 1)% bossImage.length; //프레임 인덱스 순환
			lastFrameTime = currentTime; //마지막 프레임 변경시간 갱신
        	}
            int imgW = bossImage[currentFrame].getWidth();
            int imgH = bossImage[currentFrame].getHeight();
            g.drawImage(bossImage[currentFrame], (int)(x_pos - imgW/2),(int)(y_pos - imgH /2),null);
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