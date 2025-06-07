package shootingspaceship;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Rectangle;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;

public class Bomb extends Enemy{

    private boolean Parry;
    private Color color= Color.RED;
    private boolean active; // 폭탄 활성화 여부
    private int speed;
    private static BufferedImage bombImage; // 폭탄 이미지
    private int speedY;
    private int x;
    private int y;
    
    int scaledBWidth = 1;  // 폭탄 이미지 너비
    int scaledBHeight = 150; // 폭탄 이미지 높이
    
    public static final int WIDTH = 32;
    public static final int HEIGHT = 32;
    private static final int SCREEN_HEIGHT = 800; // 화면 높이
    
    public Bomb(float x, float y, int speedY) {
        super((int) x, (int) y, y, y, speedY, speedY, y);
        this.x = (int)x;
        this.y = (int)y;
        this.speed = speed;
        this.speedY = speedY;
        this.Parry = false;
        this.Parry = false;
        this.color = Color.RED;
        this.active = true;
        this.speedY = speedY;

        // 이미지 로딩 시도 (File 경로)
        try {
            bombImage = ImageIO.read(new File("src/shootingspaceship/image/firstBomb.png"));
        } catch (IOException e) {
            System.out.println("Bomb 이미지 로드 실패");
            e.printStackTrace();
        }
    
    // 이미지 로딩 시도 (클래스패스 경로)
        if (bombImage == null) {
            try {
                bombImage = ImageIO.read(getClass().getResource("/shootingspaceship/Image/firstBomb.png"));
                if (bombImage == null) {
                    System.err.println("Bomb 이미지 경로 잘못됨: 파일이 존재하지 않음");
                }
            } catch (IOException e) {
                System.err.println("Bomb 이미지 로딩 실패");
                e.printStackTrace();
            }
        }
    }
    public void update() { // 폭탄 상태 업데이트 (이동 및 화면 이탈 처리)
        y += speedY;
        if (y > SCREEN_HEIGHT) {
            active = false;
        }
    }
    
    @Override
    public boolean isCollidedWithPlayer(Player player) { // 플레이어와의 충돌 여부 확인
        if (bombImage == null) {
            return false;
        }

        Rectangle bombBounds = new Rectangle((int)x, (int)y, bombImage.getWidth(), bombImage.getHeight());
        Rectangle playerBounds = player.getBounds();

        // 실제 충돌 검사 로직이 누락되어 있음 (항상 false 반환)
        return false;
    }
    
    public void move() { // 폭탄 이동
        y += speed;
    }
    
    public void moveBomb(int delta) { // 특정 값만큼 폭탄 이동
        this.y += delta;
    }
    
    public void drawBomb(Graphics g) { // 폭탄 그리기
        if (!active) return;
        
        if(bombImage != null) {
            g.drawImage(bombImage, (int)x, (int)y, scaledBWidth, scaledBHeight, null);
        } else { // 이미지가 없으면 기본 원형으로 그림
            g.setColor(Color.YELLOW);
            g.fillOval((int)x, (int)y, WIDTH, HEIGHT);
        }
    }
    
    public void setColor(Color color) { // 폭탄 색상 설정
        this.color = color;
    }
    
    public void deactivate() { // 폭탄 비활성화
        this.active = false;
    }
    public boolean isActive() { // 폭탄 활성화 상태 반환
        return active;
    }    
}